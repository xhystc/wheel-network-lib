package com.xhystc.wheel.event.register;

import com.xhystc.wheel.event.request.EventListenRequest;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class EventRegisterImpl implements EventRegister
{
	private LinkedBlockingQueue<EventListenRequest> eventQueue = new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<EventListenRequest> readyQueue = new LinkedBlockingQueue<>();
	@Override
	public void regist(EventListenRequest request)
	{
		eventQueue.add(request);
	}

	@Override
	public void ready(EventListenRequest request)
	{
		readyQueue.add(request);
	}

	@Override
	public EventListenRequest takeReady()
	{
		EventListenRequest request=null;
		try
		{
			request = readyQueue.take();
		}catch (InterruptedException ie){

		}
		return request;
	}

	@Override
	public EventListenRequest takeRegist()
	{
		try
		{
			return eventQueue.take();
		} catch (InterruptedException e)
		{
			return null;
		}
	}

	@Override
	public EventListenRequest takeReady(long mill)
	{
		try
		{
			return readyQueue.poll(mill, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			return null;
		}
	}

	@Override
	public EventListenRequest takeRegist(long mill)
	{
		try
		{
			return eventQueue.poll(mill,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			return null;
		}
	}


}
