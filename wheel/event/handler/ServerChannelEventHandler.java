package com.xhystc.wheel.event.handler;

import com.xhystc.wheel.Processor.MessageProcessor;
import com.xhystc.wheel.Processor.ServerMessageProcessor;

import com.xhystc.wheel.connection.ChannelConnection;
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

public class ServerChannelEventHandler implements EventHandler
{
	static private Map<Channel,SocketChannelConnection> connections = new ConcurrentHashMap<>();
	private ServerMessageProcessor processor;

	public ServerChannelEventHandler(ServerMessageProcessor processor)
	{
		this.processor = processor;
	}
	public ServerChannelEventHandler(){}
	public ServerMessageProcessor getProcessor()
	{
		return processor;
	}

	public void setProcessor(ServerMessageProcessor processor)
	{
		this.processor = processor;
	}

	@Override
	public List<EventListenRequest> handleEvent(EventListenRequest request)
	{
		ChannelEventListenRequest channelRequest = (ChannelEventListenRequest) request;
		SelectableChannel channel = channelRequest.channel();
		assert  channel instanceof ServerSocketChannel || channel instanceof SocketChannel;
		List<EventListenRequest> ret = new LinkedList<>();
		synchronized (channel){

			if(channel instanceof ServerSocketChannel)
			{
				doServerSockChannel(channelRequest,ret);
			}
			else
			{
				doSocketChannel(channelRequest,ret);
			}
		}
			return ret;



	}

	private void doServerSockChannel(ChannelEventListenRequest request, List<EventListenRequest> requests){
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
					((ServerMessageProcessor)processor).onClientAccept(connection,request,this);
				}
				ChannelEventListenRequest r = new ChannelEventListenRequest(newChannel,this,request.register());
				requests.add(doRegist(r,connection));

			}
			requests.add(doRegist(request,null));

		}catch (IOException ioe){
			ioe.printStackTrace();
		}
	}

	private void doSocketChannel(ChannelEventListenRequest request,List<EventListenRequest> requests){
		SocketChannelConnection connection = connections.get(request.channel());
		if(connection==null){
			connection = new SocketChannelConnection((SocketChannel) request.channel());
			connections.put(request.channel(),connection);
		}
		if(request.isTouchable()){
			processor.onTouch(connection,request,this);
		}
		if(request.isReadable()){

			connection.recvFromChannel();
			processor.onMessageRecv(connection,request,this);

		}
		if(request.isWritable()){
			connection.sendToChannel();
			processor.onMessageSend(connection,request,this);
		}
		requests.add(doRegist(request,connection));
	}

	private ChannelEventListenRequest doRegist(ChannelEventListenRequest request,ChannelConnection connection){
		request.setRegistEvents(0);
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
			if(request.getRegistEvents()==0){
				processor.beforeConnectionClose(connection);
				connections.remove(request.channel());
			}

		}
		return request;
	}

}













