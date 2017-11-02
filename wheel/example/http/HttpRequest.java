package com.xhystc.wheel.example.http;

public class HttpRequest
{
	HttpHeader header;
	byte[] content;

	public HttpHeader getHeader()
	{
		return header;
	}

	public void setHeader(HttpHeader header)
	{
		this.header = header;
	}

	public byte[] getContent()
	{
		return content;
	}

	public void setContent(byte[] content)
	{
		this.content = content;
	}
}
