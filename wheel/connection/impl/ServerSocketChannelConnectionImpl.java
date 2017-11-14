package com.xhystc.wheel.connection.impl;

import com.xhystc.wheel.connection.ServerSocketChannelConnection;
import com.xhystc.wheel.connection.SocketChannelConnection;
import com.xhystc.wheel.event.handler.ConnectionHandler;
import com.xhystc.wheel.event.manager.EventManager;
import com.xhystc.wheel.event.ChannelEvent;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerSocketChannelConnectionImpl implements ServerSocketChannelConnection
{
	private final ServerSocketChannel channel;
	private long connectionTimestamp;

	private boolean isShutdown = false;

	private final ConnectionHandler handler;
	private final EventManager manager;
	private final ChannelEvent event = new ChannelEvent();

	public ServerSocketChannelConnectionImpl(ServerSocketChannel channel, ConnectionHandler handler, EventManager manager){
		connectionTimestamp = System.currentTimeMillis();
		this.channel=channel;
		this.manager = manager;
		this.handler = handler;
	}

	@Override
	public SocketChannelConnection accept()
	{
		try
		{
			SocketChannel newChannel = channel.accept();
			if(newChannel==null){
				return null;
			}
			newChannel.configureBlocking(false);
			SocketChannelConnection connection = new SocketChannelConnectionImpl(newChannel,handler,manager);
			connectionTimestamp = System.currentTimeMillis();
			return connection;

		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}



	@Override
	public long conectionTimestamp()
	{
		return connectionTimestamp;
	}



	@Override
	public long activeTimeStamp()
	{
		return connectionTimestamp;
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
	}

	@Override
	public Channel channel()
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
		return channel.hashCode() ;
	}

	@Override
	public boolean equals(Object o){
		return channel.equals(o);
	}
}
