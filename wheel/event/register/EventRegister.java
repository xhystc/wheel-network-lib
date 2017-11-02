package com.xhystc.wheel.event.register;

import com.xhystc.wheel.event.request.EventListenRequest;

public interface EventRegister
{
	void regist(EventListenRequest request);
	EventListenRequest takeRegist();
	EventListenRequest takeRegist(long mill);
	EventListenRequest takeReady(long mill);
	void ready(EventListenRequest request);
	EventListenRequest takeReady();
}
