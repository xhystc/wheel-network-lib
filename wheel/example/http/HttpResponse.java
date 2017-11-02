package com.xhystc.wheel.example.http;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HttpResponse
{
	static public String get404ResponseHead(){
		DateFormat format = new SimpleDateFormat("EEE MMM ddHH:mm:ss 'GMT' yyyy", Locale.US);
		String response = "HTTP/1.1 404 Not Found\r\n" +
					"Content-Type: text/html\r\n" +
					"Connection: keep-alive\r\n"+
					"Content-Length: 0\r\n"+
					"Date: "+format.format(new Date())+"\r\n" +
					"Server: wheel\r\n" +
					"Vary: Accept-Encoding\r\n\r\n";
		return response;
	}
	static public String get200ResponseHead(File file){
		DateFormat format = new SimpleDateFormat("EEE MMM ddHH:mm:ss 'GMT' yyyy", Locale.US);
		String response = "HTTP/1.1 200 OK\r\n" +
				"Content-Type: text/html\r\n" +
				"Connection: keep-alive\r\n"+
				"Content-Length: "+file.length()+"\r\n"+
				"Date: "+format.format(new Date())+"\r\n" +
				"Server: wheel\r\n" +
				"Vary: Accept-Encoding\r\n\r\n";
		return response;
	}
}
