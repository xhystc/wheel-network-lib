package com.xhystc.wheel.event.handler;

import com.xhystc.wheel.event.request.EventListenRequest;

import java.util.List;


public interface EventHandler
{
	List<EventListenRequest> handleEvent(EventListenRequest request);
}
