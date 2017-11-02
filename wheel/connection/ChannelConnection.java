package com.xhystc.wheel.connection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channel;

public interface ChannelConnection
{
	void shutdown();
	boolean isShutdownRead();
	boolean isShutdownWrite();
	boolean send(byte[] sendBuffer);
	boolean send(InputStream inputStream);
	long sendToChannel() throws IOException;
	int recvFromChannel() throws IOException;
	Channel channel();
	boolean sendOver();
	public int recvBufferSize();
	String peekAsString(String charset);
	String recvAsString(String charset);
	void clearRecvBuffer();
	Object getData();
	void setData(Object data);
}