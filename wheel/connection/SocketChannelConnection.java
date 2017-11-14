package com.xhystc.wheel.connection;

import com.xhystc.wheel.event.handler.ConnectionHandler;
import com.xhystc.wheel.event.manager.EventManager;

import java.io.InputStream;
import java.nio.channels.Channel;

public interface SocketChannelConnection extends ChannelConnection
{

	boolean isShutdownRead();
	boolean isShutdownWrite();
	boolean send(byte[] sendBuffer);
	boolean send(InputStream inputStream);
	byte[] peek();
	byte[] recv();
	String peekAsString(String charset);
	String recvAsString(String charset);
	long sendToChannel();
	long recvFromChannel();
	boolean sendOver();
	int recvBufferSize();
	void clearRecvBuffer();
	long conectionTimestamp();
	long lastSendTimestamp();
	long lastReceiveTimestamp();
}
