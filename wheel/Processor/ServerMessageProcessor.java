package com.xhystc.wheel.Processor;

import com.xhystc.wheel.connection.ChannelConnection;
import com.xhystc.wheel.event.handler.EventHandler;
import com.xhystc.wheel.event.register.EventRegister;

public interface ServerMessageProcessor extends MessageProcessor
{
	void onClientAccept(ChannelConnection connection, EventRegister register, EventHandler handler);
	void beforeConnectionClose(ChannelConnection connection);
}
