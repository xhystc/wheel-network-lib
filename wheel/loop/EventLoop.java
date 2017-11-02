package com.xhystc.wheel.loop;

import com.xhystc.wheel.event.request.ChannelEventListenRequest;
import com.xhystc.wheel.event.request.EventListenRequest;
import com.xhystc.wheel.event.register.EventRegister;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class EventLoop implements Runnable
{
	private Selector selector;
	private EventRegister register;
	private Map<Channel,EventListenRequest> requestMap = new HashMap<>();

	public EventLoop(Selector selector,EventRegister register,EventListenRequest seed) throws IOException
	{
		this.selector = selector;
		this.register = register;
		registChannelEvent(seed);
	}

	public EventLoop(Selector selector,EventRegister register,EventListenRequest[] seeds) throws IOException
	{
		this.selector = selector;
		this.register = register;
		for(EventListenRequest request : seeds){
			registChannelEvent(request);
		}
	}
	public EventLoop(){}

	public Selector getSelector()
	{
		return selector;
	}

	public void setSelector(Selector selector)
	{
		this.selector = selector;
	}

	public EventRegister getRegister()
	{
		return register;
	}

	public void setRegister(EventRegister register)
	{
		this.register = register;
	}


	@Override
	public void run()
	{
		System.out.println("thread"+Thread.currentThread().toString()+" start");
		boolean quit=false;

		while (!quit){
			try
			{
				int res = selector.select(100);

				if(res>0){
					Set<SelectionKey> readyKey = selector.selectedKeys();
					Iterator iterator = readyKey.iterator();
					while(iterator.hasNext()){
						SelectionKey key = (SelectionKey) iterator.next();
						int readys = key.readyOps();
						key.channel().register(selector,0);
						EventListenRequest request = requestMap.get(key.channel());
						request.setReadyEvents(readys);
						register.ready(request);
						iterator.remove();
					}
				}
				EventListenRequest request;
				int count=0;
				while((request=register.takeRegist(0))!=null){
					if(request instanceof ChannelEventListenRequest){
						registChannelEvent((ChannelEventListenRequest) request);
					}else {
						throw new RuntimeException("unsupport event request");
					}
					if (++count>100)
					{
						break;
					}
				}

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	private void registChannelEvent(EventListenRequest request) throws IOException
	{
		if(request instanceof  ChannelEventListenRequest){
			SelectableChannel channel = ((ChannelEventListenRequest)request).channel();
			int registEvents = request.getRegistEvents();
			if(registEvents==0){
				requestMap.remove(channel);
				SelectionKey key = channel.keyFor(selector);
				if(key!=null){
					key.cancel();
					channel.close();
				}
				System.out.println("connection close:"+requestMap.size());
			}else {
				channel.register(selector,registEvents);
				requestMap.put(channel,request);
			}
		}
		else{
			throw new RuntimeException("unsupport event request");
		}
	}
}














