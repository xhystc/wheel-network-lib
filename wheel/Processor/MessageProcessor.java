package com.xhystc.wheel.Processor;

import com.xhystc.wheel.connection.ChannelConnection;
import com.xhystc.wheel.event.handler.EventHandler;
import com.xhystc.wheel.event.register.EventRegister;

public interface MessageProcessor
{
	void onMessageRecv(ChannelConnection connection, EventRegister register, EventHandler handler);
	void onMessageSend(ChannelConnection connection, EventRegister register, EventHandler handler);
	void onTouch(ChannelConnection connection, EventRegister register, EventHandler handler);
}
