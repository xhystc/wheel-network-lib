package com.xhystc.wheel.connection;

import com.xhystc.wheel.event.ChannelEvent;
import com.xhystc.wheel.event.handler.ConnectionHandler;
import com.xhystc.wheel.event.manager.EventManager;

import java.nio.channels.Channel;

public interface ChannelConnection
{
	long activeTimeStamp();
	ConnectionHandler handler();
	EventManager manager();
	void shutdown();
	Channel channel();
	boolean isShutdown();
	ChannelEvent event();
}
