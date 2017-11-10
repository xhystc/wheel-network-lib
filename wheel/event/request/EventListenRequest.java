package com.xhystc.wheel.event.request;

import com.xhystc.wheel.event.handler.EventHandler;
import com.xhystc.wheel.event.register.EventRegister;

import java.nio.channels.SelectionKey;

public class EventListenRequest
{

	protected int registEvents = 0;
	protected int readyEvents=0;
	EventHandler handler;
	Object data=null;
	EventRegister register;

	public EventRegister register(){
		return register;
	}
	public void attach(Object obj){
		data=obj;
	}
	public Object attach(){
		return data;
	}

	protected EventListenRequest(EventHandler handler,EventRegister register){
		this.handler = handler;
		this.register = register;
	}

	public EventHandler getHandler()
	{
		return handler;
	}

	protected boolean isReady(int eventType){
		return (readyEvents & eventType) >0;
	}

	protected void registEvents(int eventType){
		registEvents |= eventType;
	}
	public int getRegistEvents(){
		return registEvents;
	}

	public void setRegistEvents(int registEvents)
	{
		this.registEvents = registEvents;
	}

	public void setReadyEvents(int ops){
		readyEvents=ops;
	}

}





