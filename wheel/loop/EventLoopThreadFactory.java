package com.xhystc.wheel.loop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class EventLoopThreadFactory implements ThreadFactory,ThreadArray
{
	List<Thread> threads;
	public EventLoopThreadFactory(int n){
		threads = new ArrayList<>(n);

	}
	@Override
	public Thread newThread(Runnable r)
	{
		Thread thread = new Thread(r);
		thread.setName("work thread-"+threads.size());
		threads.add(thread);
		return thread;
	}

	@Override
	public Thread get(int i){
		return threads.get(i);
	}
	@Override
	public boolean contains(Thread thread){
		return threads.contains(thread);
	}

	@Override
	public int size()
	{
		return threads.size();
	}
}
