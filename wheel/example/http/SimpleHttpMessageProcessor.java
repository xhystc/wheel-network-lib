package com.xhystc.wheel.example.http;

import com.xhystc.wheel.Processor.ServerMessageProcessor;
import com.xhystc.wheel.connection.ChannelConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class SimpleHttpMessageProcessor implements ServerMessageProcessor
{
	@Override
	public void onClientAccept(ChannelConnection connection)
	{

	}

	@Override
	public void beforeConnectionClose(ChannelConnection connection)
	{

	}

	@Override
	public void onMessageRecv(ChannelConnection connection)
	{
		try
		{
			if(connection.getData()==null){
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
								connection.setData(null);
							}else {
								connection.send(HttpResponse.get200ResponseHead(file).getBytes("utf-8"));
								connection.send(new FileInputStream(file));
								connection.setData(null);
							}

						}else {
							connection.send(HttpResponse.get404ResponseHead().getBytes("utf-8"));
							connection.setData(null);
						}
						Map<String,String> headers = header.getHeaders();
						for(Map.Entry<String,String> en : headers.entrySet()){
							System.out.println(en.getKey()+"="+en.getValue());
						}


				}
			}
			else {
				throw new IOException("illegal state");
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			connection.setData(null);
			connection.shutdown();
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageSend(ChannelConnection connection)
	{
	}
}











