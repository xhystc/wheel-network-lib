package com.xhystc.wheel.Processor;

import com.xhystc.wheel.connection.SocketChannelConnection;

public interface ServerMessageProcessor extends MessageProcessor
{
	void onClientAccept(SocketChannelConnection connection);
	boolean onConnectionTimeout(SocketChannelConnection connection);
	void onMessageRecv(SocketChannelConnection connection,long nread);
	void onMessageSend(SocketChannelConnection connection,long nwrite);
}
