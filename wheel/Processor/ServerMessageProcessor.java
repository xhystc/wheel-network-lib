package com.xhystc.wheel.Processor;

import com.xhystc.wheel.connection.ChannelConnection;
import com.xhystc.wheel.event.handler.EventHandler;
import com.xhystc.wheel.event.request.EventListenRequest;

public interface ServerMessageProcessor extends MessageProcessor
{
	void onClientAccept(ChannelConnection connection, EventListenRequest request, EventHandler handler);
	void beforeConnectionClose(ChannelConnection connection);
}
