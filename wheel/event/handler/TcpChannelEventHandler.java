package com.xhystc.wheel.event.handler;

import com.xhystc.wheel.Processor.MessageProcessor;
import com.xhystc.wheel.Processor.ServerMessageProcessor;

import com.xhystc.wheel.connection.impl.SocketChannelConnection;
import com.xhystc.wheel.event.register.EventRegister;
import com.xhystc.wheel.event.request.ChannelEventListenRequest;
import com.xhystc.wheel.event.request.EventListenRequest;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpChannelEventHandler implements EventHandler
{
	static private Map<Channel,SocketChannelConnection> connections = new ConcurrentHashMap<>();
	private MessageProcessor processor;

	public TcpChannelEventHandler(MessageProcessor processor)
	{
		this.processor = processor;
	}
	public TcpChannelEventHandler(){}
	public MessageProcessor getProcessor()
	{
		return processor;
	}

	public void setProcessor(MessageProcessor processor)
	{
		this.processor = processor;
	}

	@Override
	public List<EventListenRequest> handleEvent(EventListenRequest request, EventRegister register)
	{
		ChannelEventListenRequest channelRequest = (ChannelEventListenRequest) request;
		SelectableChannel channel = channelRequest.channel();
		assert  channel instanceof ServerSocketChannel || channel instanceof SocketChannel;
		List<EventListenRequest> ret = new LinkedList<>();
		if(channel instanceof ServerSocketChannel)
		{
			doServerSockChannel(channelRequest,ret,register);
		}
		else
		{
			doSocketChannel(channelRequest,ret,register);
		}
		return ret;

	}

	private void doServerSockChannel(ChannelEventListenRequest request, List<EventListenRequest> requests, EventRegister register){
		ServerSocketChannel channel = (ServerSocketChannel) request.channel();
		try
		{
			while (true){
				SocketChannel newChannel = channel.accept();
				if (newChannel==null)
				{
					break;
				}
				newChannel.configureBlocking (false);
				SocketChannelConnection connection = new SocketChannelConnection(newChannel);
				connections.put(newChannel,connection);
				if(processor instanceof ServerMessageProcessor){
					((ServerMessageProcessor)processor).onClientAccept(connection,register,this);
				}
				ChannelEventListenRequest r = new ChannelEventListenRequest(newChannel,this);
				requests.add(doRegist(r));

			}
			requests.add(doRegist(request));

		}catch (IOException ioe){
			ioe.printStackTrace();
		}
	}

	private void doSocketChannel(ChannelEventListenRequest request,List<EventListenRequest> requests, EventRegister register){
		SocketChannelConnection connection = connections.get(request.channel());
		if(connection==null){
			connection = new SocketChannelConnection((SocketChannel) request.channel());
			connections.put(request.channel(),connection);
		}
		if(request.isTouchable()){
			processor.onTouch(connection,register,this);
		}
		if(request.isReadable()){
			try
			{
				connection.recvFromChannel();
				processor.onMessageRecv(connection,register,this);
			}catch (IOException ioe){

			}
		}
		if(request.isWritable()){
			try
			{
				connection.sendToChannel();
				processor.onMessageSend(connection,register,this);
			}catch (IOException ioe){

			}
		}
		requests.add(doRegist(request));
	}

	private ChannelEventListenRequest doRegist(ChannelEventListenRequest request){
		request.setRegistEvents(0);
		SocketChannelConnection connection = connections.get(request.channel());
		if (connection==null){
			request.registAcceptEvent();
		}
		else{
			if(!connection.isShutdownRead())
			{
				request.registReadEvent();
			}
			if(!connection.isShutdownWrite() && !connection.sendOver())
			{
				request.registWriteEvent();
			}
			if(request.getRegistEvents()==0 && processor instanceof ServerMessageProcessor){
				((ServerMessageProcessor)processor).beforeConnectionClose(connection);
				connections.remove(request.channel());
			}
		}
		return request;
	}

}













