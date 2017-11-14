package com.xhystc.wheel.event.manager;

import com.xhystc.wheel.connection.SocketChannelConnection;

public interface EventManager
{

	void regist(SocketChannelConnection request);
	SocketChannelConnection takeRegist();
	SocketChannelConnection takeRegist(long mill);
	SocketChannelConnection takeReady(long mill);
	void ready(SocketChannelConnection request);
	SocketChannelConnection takeReady();
}
