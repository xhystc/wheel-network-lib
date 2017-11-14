package com.xhystc.wheel.loop;

import com.xhystc.wheel.connection.ChannelConnection;
import com.xhystc.wheel.connection.ServerSocketChannelConnection;
import com.xhystc.wheel.connection.SocketChannelConnection;
import com.xhystc.wheel.connection.impl.SocketChannelConnectionImpl;
import com.xhystc.wheel.event.manager.EventManager;
import com.xhystc.wheel.poller.Poller;

import java.io.IOException;
import java.nio.channels.*;
import java.util.*;


public class EventLoop implements Runnable
{
	private Poller poller;
	private EventManager manager;
	private boolean quit = false;

	public EventLoop(Poller poller, EventManager manager, ChannelConnection seed) throws IOException
	{
		this.poller = poller;
		this.manager = manager;
		poller.regist(seed);
	}

	public EventLoop(Selector selector, EventManager register, List<ChannelConnection> seeds) throws IOException
	{
		this.poller = poller;
		this.manager = manager;
		poller.regist(seeds);
	}
	public EventLoop(){}

	public Poller getPoller()
	{
		return poller;
	}

	public void setPoller(Poller poller)
	{
		this.poller = poller;
	}

	public EventManager getManager()
	{
		return manager;
	}

	public void setManager(EventManager manager)
	{
		this.manager = manager;
	}


	@Override
	public void run()
	{
		System.out.println("thread"+Thread.currentThread().toString()+" start");

		while (!quit){
			try
			{
				List<ChannelConnection> connections = poller.poll(100);
				for(ChannelConnection connection : connections){
					if(connection instanceof ServerSocketChannelConnection){
						ServerSocketChannelConnection ssc = (ServerSocketChannelConnection) connection;
						while(true){
							SocketChannelConnection newConnection = doAccpet(ssc);
							if (newConnection==null){
								break;
							}
							newConnection.event().addCreate();
							manager.ready(newConnection);
						}
					}else if(connection instanceof SocketChannelConnection){
							manager.ready((SocketChannelConnection) connection);
					}else {
						throw new IOException("not support connection");
					}
				}
				SocketChannelConnection connection = null;
				while((connection = manager.takeRegist())!=null){
					if(connection.isShutdown()){
						System.out.println("shut down handle");
					}
					poller.regist(connection);
				}

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	private SocketChannelConnection doAccpet(ServerSocketChannelConnection connection) throws IOException
	{
		SocketChannelConnection newConnection =connection.accept();
		if(newConnection==null){
			return null;
		}
		((SocketChannel)newConnection.channel()).configureBlocking(false);
		System.out.println("connection accept ip:"+((SocketChannel)newConnection.channel()).getRemoteAddress().toString());
		return newConnection;
	}
}














