package com.xhystc.wheel.loop;

public interface ThreadArray
{
	Thread get(int i);
	int size();
	boolean contains(Thread thread);
}
