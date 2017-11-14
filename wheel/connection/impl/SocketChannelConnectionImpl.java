package com.xhystc.wheel.connection.impl;

import com.xhystc.wheel.buffer.ChannelBuffer;
import com.xhystc.wheel.buffer.impl.SendQueueChannelBuffer;
import com.xhystc.wheel.connection.SocketChannelConnection;
import com.xhystc.wheel.connection.ConnectionUtil;
import com.xhystc.wheel.event.handler.ConnectionHandler;
import com.xhystc.wheel.event.manager.EventManager;
import com.xhystc.wheel.event.ChannelEvent;

import java.io.*;
import java.nio.channels.SocketChannel;

public class SocketChannelConnectionImpl implements SocketChannelConnection
{

	private final SocketChannel channel;
	private final ChannelBuffer buffer = new SendQueueChannelBuffer(1024*5);


	private volatile boolean isShutdownRead=false;
	private volatile boolean isShutdownWrite=false;
	private volatile boolean isShutdown = false;

	private long connectionTimestamp;
	private long lastSendTimestamp;
	private long lastReceiveTimestamp;

	private final ConnectionHandler handler;
	private final EventManager manager;

	private final ChannelEvent event = new ChannelEvent();


	public SocketChannelConnectionImpl(SocketChannel channel, ConnectionHandler handler, EventManager manager){
		connectionTimestamp = System.currentTimeMillis();
		this.channel=channel;
		this.manager = manager;
		this.handler = handler;
	}


	@Override
	public long conectionTimestamp()
	{
		return connectionTimestamp;
	}

	@Override
	public long lastSendTimestamp()
	{
		return lastSendTimestamp;
	}

	@Override
	public long lastReceiveTimestamp()
	{
		return lastReceiveTimestamp;
	}

	@Override
	public long activeTimeStamp()
	{
		return Math.max(lastReceiveTimestamp,Math.max(lastSendTimestamp,connectionTimestamp));
	}



	@Override
	public ConnectionHandler handler()
	{
		return handler;
	}



	@Override
	public EventManager manager()
	{
		return manager;
	}


	@Override
	synchronized public void shutdown()
	{
		try
		{
			channel.close();
		}catch (IOException ioe){

		}
		isShutdown = true;
		if(!ConnectionUtil.isInWorkEnv()){
			manager.regist(this);
		}
	}
	@Override
	public boolean isShutdownRead(){
		return isShutdownRead || !channel.isConnected();
	}
	@Override
	public boolean isShutdownWrite(){
		return isShutdownWrite || !channel.isConnected();
	}


	@Override
	public boolean send(byte[] sendBuffer)
	{
		return send(new ByteArrayInputStream(sendBuffer));

	}
	@Override
	public boolean send(InputStream inputStream)
	{
		if(isShutdownWrite())
		{
			return false;
		}
		buffer.write(inputStream);
		if(!ConnectionUtil.isInWorkEnv()){
			manager.regist(this);
		}
		return true;
	}



	@Override
	public int recvBufferSize()
	{
		return buffer.readBufferSize();
	}

	@Override
	public byte[] peek()
	{
		return buffer.peek();
	}

	@Override
	public byte[] recv()
	{
		if(!ConnectionUtil.isInWorkEnv()){
			throw new RuntimeException("not in work thread");
		}
		return buffer.read();
	}

	@Override
	public String peekAsString(String charset)
	{
		try
		{
			return new String(peek(),charset);
		}catch (UnsupportedEncodingException e){
			return null;
		}
	}

	@Override
	public String recvAsString(String charset)
	{
		try
		{
			return new String(recv(),charset);
		}catch (UnsupportedEncodingException e){
			return null;
		}
	}


	@Override
	public void clearRecvBuffer(){
		buffer.clearReadBuffer();
	}

	@Override
	public long sendToChannel()
	{
		if(isShutdownWrite())
		{
			return -1;
		}
		if(!ConnectionUtil.isInWorkEnv()){
			throw new RuntimeException("not in work thread");
		}
		long sum = 0;
		try
		{
			sum = buffer.sendToChannel(channel);

		}catch (IOException ioe){
			isShutdownWrite=true;
			return -1;
		}
		lastSendTimestamp = System.currentTimeMillis();
		return sum;

	}
	@Override
	public long recvFromChannel()
	{
		if(isShutdownRead())
		{
			return -1;
		}
		if(!ConnectionUtil.isInWorkEnv()){
			throw new RuntimeException("not in work thread");
		}
		long sum = 0;
		try
		{
			sum = buffer.recvFromChannel(channel);

		}catch (IOException ioe){
			isShutdownRead=true;
			return -1;
		}
		lastReceiveTimestamp = System.currentTimeMillis();
		return sum;
	}

	@Override
	public boolean sendOver(){
		return buffer.isWriteBufferEmpty() || isShutdownWrite();
	}

	@Override
	public SocketChannel channel()
	{
		return channel;
	}

	@Override
	public boolean isShutdown()
	{
		return isShutdown;
	}

	@Override
	public ChannelEvent event()
	{
		return event;
	}

	@Override
	public int hashCode(){
		return channel.hashCode();
	}

	@Override
	public boolean equals(Object o){
		return channel.equals(o);
	}
}





