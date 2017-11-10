package com.xhystc.wheel.loop;

import com.xhystc.wheel.connection.ConnectionUtil;
import com.xhystc.wheel.event.request.ChannelEventListenRequest;
import com.xhystc.wheel.event.request.EventListenRequest;
import com.xhystc.wheel.event.register.EventRegister;
import java.nio.channels.Selector;
import java.util.LinkedList;
import java.util.List;


public class EventhandleLoop implements Runnable
{
	private EventRegister register;
	private boolean quit = false;
	private Selector selector;

	public EventhandleLoop(){}

	public EventhandleLoop(Selector selector,EventRegister register){
		this.register=register;
		this.selector = selector;
	}

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
		ConnectionUtil.inWorkEnv();
		List<EventListenRequest> requests = new LinkedList<>();
		int count = 0;
		while (!quit){
			EventListenRequest request;
			if (requests.size()>0)
			{
				request = register.takeReady(10);
			}
			else
			{
				request = register.takeReady();
			}
			count++;
			if(request!=null){
				List<EventListenRequest> ret = request.getHandler().handleEvent(request);
				requests.addAll(ret);
			}
			if(requests.size()>0 && count>10){
				for(EventListenRequest r : requests){
					register.regist(r);
				}
				count=0;
				requests.clear();
				selector.wakeup();
			}
		}
	}
}










