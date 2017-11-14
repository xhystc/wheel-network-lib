package com.xhystc.wheel.event.manager;

import com.xhystc.wheel.connection.SocketChannelConnection;
import com.xhystc.wheel.loop.ThreadArray;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class EventManagerImpl implements EventManager
{
	private final LinkedBlockingQueue<SocketChannelConnection> registQueue = new LinkedBlockingQueue<>();
	private final Map<Thread,LinkedBlockingQueue<SocketChannelConnection>> workQueue = new ConcurrentHashMap<>();
	private final Map<Thread,Integer> requestMap = new ConcurrentHashMap<>();
	private final Selector selector;
	private final ThreadArray threadArray;
	private final int WAKEUP_SIZE = 20;
	private int count = 0;

	public EventManagerImpl(Selector selector,ThreadArray threadArray){
		this.selector = selector;
		this.threadArray = threadArray;
	}


	@Override
	public void regist(SocketChannelConnection connection)
	{
		registQueue.add(connection);
		if(registQueue.size()>WAKEUP_SIZE){
			selector.wakeup();
		}
	}

	@Override
	public void ready(SocketChannelConnection connection)
	{
		int h = connection.hashCode();
		int index = (h ^ (h>>>16))%threadArray.size();

		Thread t = threadArray.get(index);
		assert t!=null;
		workQueue.get(t).add(connection);
	}

	@Override
	public SocketChannelConnection takeReady()
	{
		SocketChannelConnection request=null;
		LinkedBlockingQueue<SocketChannelConnection> queue = workQueue.get(Thread.currentThread());
		if(queue==null && threadArray.contains(Thread.currentThread())){
			queue = new LinkedBlockingQueue<>();
			workQueue.put(Thread.currentThread(),queue);
			requestMap.put(Thread.currentThread(),0);
			System.out.println("pool size:"+workQueue.size());
		}

		try
		{
			request = queue.take();
			requestMap.put(Thread.currentThread(),requestMap.get(Thread.currentThread())+1);
			System.out.println("thread:"+Thread.currentThread()+" take");

		}catch (InterruptedException ie){
			ie.printStackTrace();
		}
		return request;
	}

	@Override
	public SocketChannelConnection takeRegist()
	{
		SocketChannelConnection connection =registQueue.poll();
		return connection;
	}

	@Override
	public SocketChannelConnection takeReady(long mill)
	{
		SocketChannelConnection request=null;
		LinkedBlockingQueue<SocketChannelConnection> queue = workQueue.get(Thread.currentThread());
		try
		{
			request = queue.poll(mill,TimeUnit.MILLISECONDS);
		}catch (InterruptedException ie){

		}
		return request;
	}

	@Override
	public SocketChannelConnection takeRegist(long mill)
	{
		try
		{
			return registQueue.poll(mill,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			return null;
		}
	}


}
