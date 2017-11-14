package com.xhystc.wheel.poller;

import com.xhystc.wheel.connection.ChannelConnection;

import java.io.IOException;
import java.util.List;

public interface Poller
{
	List<ChannelConnection> poll(long mill) throws IOException;
	List<ChannelConnection> poll() throws IOException;
	void regist(List<? extends ChannelConnection> connections);
	void regist(ChannelConnection connection);
}
