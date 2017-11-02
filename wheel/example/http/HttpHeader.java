package com.xhystc.wheel.example.http;

import java.util.HashMap;
import java.util.Map;

public class HttpHeader
{
	Map<String,String> headers = new HashMap<>();
	String method;
	String path;

	public String getPath()
	{
		return path;
	}

	public String getMethod()
	{
		return method;
	}

	public Map<String, String> getHeaders()
	{
		return headers;
	}

	public HttpHeader(String request){
		request = request.trim().toLowerCase();
		String[] strs = request.split("\r\n");
		if(strs[0].startsWith("get")){
			method="get";
		}
		else if(strs[0].startsWith("post")){
			method="post";
		}else {
			throw new RuntimeException("illegal request");
		}
		path = strs[0].substring(strs[0].indexOf('/'),strs[0].indexOf("http")).trim();
		System.out.println("path:"+path);
		if(path.indexOf('?')>=0)
			path = path.substring(0,path.indexOf('?'));
		for(String s : strs){
			if(!s.contains(":")){
				continue;
			}
			String[] nameValue = new String[2];
			nameValue[0] = s.substring(0,s.indexOf(':')).trim();
			nameValue[1] = s.substring(s.indexOf(':')+1,s.length()).trim();
			headers.put(nameValue[0],nameValue[1]);
		}
	}
}
