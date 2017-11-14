package com.xhystc.wheel.event.handler;

import com.xhystc.wheel.connection.SocketChannelConnection;


public interface ConnectionHandler
{
	void handle(SocketChannelConnection connection, boolean create);
}
