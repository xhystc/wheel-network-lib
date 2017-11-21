package com.xhystc.wheel.example.http;

import com.xhystc.wheel.connection.SocketChannelConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Sender implements Runnable
{
	SocketChannelConnection connection;
	long nread;
	String httpStr;

	public Sender(SocketChannelConnection connection,long nread,String httpStr)
	{
		this.connection = connection;
		this.nread = nread;
		this.httpStr = httpStr;
	}

	@Override
	public void run()
	{
		try
		{
			HttpRequest httpRequest = new HttpRequest();
			String[] div = httpStr.split("\r\n\r\n");
			HttpHeader header = new HttpHeader(div[0]);
			String contentLength = header.getHeaders().get("content-length");
			if(header.getMethod().equals("get") || contentLength.equals("0") ){
				String path =header.getPath();
				String filePath ="./dota/"+ header.getPath().substring(1, header.getPath().length());
				if(path.equals("/")){
					filePath ="./dota/index.html";
				}

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
		}catch (IOException ioe){
			ioe.printStackTrace();
			connection.shutdown();
			ioe.printStackTrace();
		}
	}
}
