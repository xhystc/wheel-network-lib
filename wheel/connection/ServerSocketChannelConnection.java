package com.xhystc.wheel.connection;

public interface ServerSocketChannelConnection extends ChannelConnection
{
	SocketChannelConnection accept();
	long conectionTimestamp();
}
