package com.xhystc.wheel.Processor;

import com.xhystc.wheel.connection.ChannelConnection;

public interface ServerMessageProcessor extends MessageProcessor
{
	void onClientAccept(ChannelConnection connection);
	void beforeConnectionClose(ChannelConnection connection);
}
