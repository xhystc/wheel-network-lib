package com.xhystc.wheel.connection.impl;

import com.xhystc.wheel.buffer.ChannelBuffer;
import com.xhystc.wheel.buffer.impl.SendQueueChannelBuffer;
import com.xhystc.wheel.connection.ChannelConnection;
import com.xhystc.wheel.connection.ConnectionUtil;

import java.io.*;
import java.nio.channels.SocketChannel;

public class SocketChannelConnection implements ChannelConnection
{

	private SocketChannel channel;
	ChannelBuffer buffer = new SendQueueChannelBuffer(1024*5);
	private boolean isShutdownRead=false;
	private boolean isShutdownWrite=false;
	private Object data;


	public SocketChannelConnection(SocketChannel channel){
		this.channel=channel;
	}


	@Override
	public Object getData()
	{
		return data;
	}

	@Override
	public void setData(Object data)
	{
		this.data = data;
	}


	@Override
	public void shutdown()
	{
		if(ConnectionUtil.isInWorkEnv()){
			isShutdownRead=true;
			isShutdownWrite=true;

		}else {

		}

	}
	@Override
	public boolean isShutdownRead(){
		return isShutdownRead || !channel.isConnected();
	}
	@Override
	public boolean isShutdownWrite(){
		return isShutdownWrite || !channel.isConnected();
	}

	@Override
	public boolean send(byte[] sendBuffer)
	{
		return send(new ByteArrayInputStream(sendBuffer));

	}
	@Override
	public boolean send(InputStream inputStream)
	{
		if(isShutdownWrite())
			return false;
		return buffer.write(inputStream);
	}



	@Override
	public int recvBufferSize()
	{
		return buffer.readBufferSize();
	}

	@Override
	public byte[] peek()
	{
		return buffer.peek();
	}

	@Override
	public byte[] recv()
	{
		return buffer.read();
	}

	@Override
	public String peekAsString(String charset)
	{
		try
		{
			return new String(peek(),charset);
		}catch (UnsupportedEncodingException e){
			return null;
		}
	}

	@Override
	public String recvAsString(String charset)
	{
		try
		{
			return new String(recv(),charset);
		}catch (UnsupportedEncodingException e){
			return null;
		}
	}


	@Override
	public void clearRecvBuffer(){
		buffer.clearReadBuffer();
	}

	@Override
	public long sendToChannel() throws IOException
	{
		if(isShutdownWrite())
		{
			return -1;
		}
		assert ConnectionUtil.isInWorkEnv():"not in work thread";
		long sum = 0;
		try
		{
			sum = buffer.sendToChannel(channel);
		}catch (IOException ioe){
			isShutdownWrite=true;
		}
		return sum;

	}
	@Override
	public int recvFromChannel() throws IOException
	{
		if(isShutdownRead())
		{
			return -1;
		}
		assert ConnectionUtil.isInWorkEnv():"not in work thread";
		int sum = 0;
		try
		{
			sum = buffer.recvFromChannel(channel);
		}catch (IOException ioe){
			isShutdownRead=true;
			return sum;
		}
		return sum;
	}

	@Override
	public boolean sendOver(){
		return buffer.isWriteBufferEmpty();
	}

	@Override
	public SocketChannel channel()
	{
		return channel;
	}
}





