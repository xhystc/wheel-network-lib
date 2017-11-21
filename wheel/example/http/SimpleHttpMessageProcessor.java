package com.xhystc.wheel.example.http;

import com.xhystc.wheel.Processor.ServerMessageProcessor;
import com.xhystc.wheel.connection.SocketChannelConnection;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class SimpleHttpMessageProcessor implements ServerMessageProcessor
{
	ExecutorService service;

	public SimpleHttpMessageProcessor(ExecutorService service)
	{
		this.service = service;
	}
	@Override
	public void onClientAccept(SocketChannelConnection connection)
	{

	}

	@Override
	public boolean onConnectionTimeout(SocketChannelConnection connection)
	{
		System.out.println("time out");
		return false;
	}

	@Override
	public void onMessageRecv(SocketChannelConnection connection,long nread)
	{
		System.out.println("nread:"+nread);
		if(nread<0 || connection.isShutdownWrite()){
			connection.shutdown();
			return;
		}
		System.out.println("get message");
		String httpStr = connection.peekAsString("utf-8");
		if(httpStr.contains("\r\n\r\n")){
			connection.clearRecvBuffer();
			service.execute(new Sender(connection,nread,httpStr));
		}
	}

	@Override
	public void onMessageSend(SocketChannelConnection connection,long nwrite)
	{
		System.out.println("send over");
	}

}











