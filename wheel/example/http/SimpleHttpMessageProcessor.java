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

public class SimpleHttpMessageProcessor implements ServerMessageProcessor
{

	@Override
	public void onClientAccept(SocketChannelConnection connection)
	{
		System.out.println("on acccept");
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			SocketChannelConnection c = connection;
			@Override
			public void run()
			{
				try
				{
					System.out.println("send xixi");
					connection.send("xixi".getBytes("utf-8"));
				} catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}
		},5000);
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
		try
		{
			String httpStr = connection.peekAsString("utf-8");
			if(httpStr.contains("\r\n\r\n")){
				HttpRequest httpRequest = new HttpRequest();
				String[] div = httpStr.split("\r\n\r\n");
				HttpHeader header = new HttpHeader(div[0]);
				String contentLength = header.getHeaders().get("content-length");
				if(header.getMethod().equals("get") || contentLength.equals("0") ){
					connection.recvAsString("utf-8");
					String filePath ="./dota/"+ header.getPath().substring(1, header.getPath().length());
					File file = new File(filePath);
					if(file.length()==0){
						connection.send(HttpResponse.get404ResponseHead().getBytes("utf-8"));
						System.out.println("send file size:"+file.length());

					}else {
						connection.send(HttpResponse.get200ResponseHead(file).getBytes("utf-8"));
						connection.send(new FileInputStream(file));

						System.out.println("send file size:"+file.length());
					}
				}else {
					connection.send(HttpResponse.get404ResponseHead().getBytes("utf-8"));

				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			connection.shutdown();
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageSend(SocketChannelConnection connection,long nwrite)
	{
		System.out.println("send over");
	}

}











