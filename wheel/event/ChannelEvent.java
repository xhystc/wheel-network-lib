package com.xhystc.wheel.event;

import com.xhystc.wheel.event.handler.ConnectionHandler;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 *
 * */
public class ChannelEvent
{
	public static final int EV_READ = SelectionKey.OP_READ;
	public static final int EV_WRITE = SelectionKey.OP_WRITE;
	private static final int EV_CONNECT = SelectionKey.OP_CONNECT;
	public static final int EV_ACCEPT = SelectionKey.OP_ACCEPT;
	private static final int EV_CREATE = EV_ACCEPT<<1;

	private volatile int events = 0;


	private boolean isReady(int eventType){
		return (events & eventType) >0;
	}

	public void setEvents(int events){
		this.events = events;
	}

	public void addEvents(int event){
		this.events |= event;
	}

	public boolean isReadable(){
		return isReady(EV_READ);
	}
	public boolean isWriteable(){
		return isReady(EV_WRITE);
	}
	public boolean isAcceptable(){
		return isReady(EV_ACCEPT);
	}
	public boolean isConnection(){
		return isReady(EV_CONNECT);
	}
	public boolean isCreate(){
		return isReady(EV_CREATE);
	}

	public void addReadable(){
		addEvents(EV_READ);
	}
	public void addWriteable(){
		addEvents(EV_WRITE);
	}public void addAcceptable(){
		addEvents(EV_ACCEPT);
	}
	public void addCreate(){
		addEvents(EV_CREATE);
	}
	public void addConnection(){
		addEvents(EV_CONNECT);
	}

}










