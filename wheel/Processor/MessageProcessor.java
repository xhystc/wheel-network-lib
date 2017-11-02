package com.xhystc.wheel.Processor;

import com.xhystc.wheel.connection.ChannelConnection;
public interface MessageProcessor
{
	void onMessageRecv(ChannelConnection connection);
	void onMessageSend(ChannelConnection connection);
}
