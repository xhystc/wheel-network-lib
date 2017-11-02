package com.xhystc.wheel.event.register;

import com.xhystc.wheel.event.request.EventListenRequest;

public interface EventRegister
{
	void regist(EventListenRequest request);
	void ready(EventListenRequest request);
	EventListenRequest takeReady();
	EventListenRequest takeRegist();
	EventListenRequest takeReady(long mill);
	EventListenRequest takeRegist(long mill);
}
