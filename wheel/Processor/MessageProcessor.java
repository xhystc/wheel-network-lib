package com.xhystc.wheel.Processor;

import com.xhystc.wheel.connection.ChannelConnection;
import com.xhystc.wheel.event.handler.EventHandler;
import com.xhystc.wheel.event.request.EventListenRequest;

public interface MessageProcessor
{
	void onMessageRecv(ChannelConnection connection, EventListenRequest register, EventHandler handler);
	void onMessageSend(ChannelConnection connection, EventListenRequest register, EventHandler handler);
	void onTouch(ChannelConnection connection, EventListenRequest register, EventHandler handler);
}
