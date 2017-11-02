package com.xhystc.wheel.event.request;

import com.xhystc.wheel.event.handler.EventHandler;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 *
 * */
public class ChannelEventListenRequest extends EventListenRequest
{
	public static final int EV_READ = SelectionKey.OP_READ;
	public static final int EV_WRITE = SelectionKey.OP_WRITE;
	public static final int EV_CONNECT = SelectionKey.OP_CONNECT;
	public static final int EV_ACCEPT = SelectionKey.OP_ACCEPT;
	public static final int EV_TOUCH = 64;
	SelectableChannel channel;

	/**
	 * */
	public ChannelEventListenRequest(SelectableChannel channel,EventHandler handler){
		super(handler);
		this.channel = channel;
	}

	public SelectableChannel channel()
	{
		return channel;
	}

	public void setChannel(SelectableChannel channel)
	{
		this.channel = channel;
	}

	public void registReadEvent(){
		registEvents(EV_READ);
	}
	public void registWriteEvent(){
		registEvents(EV_WRITE);
	}
	public void registConnectEvent(){
		registEvents(EV_CONNECT);
	}
	public void registAcceptEvent(){
		registEvents(EV_ACCEPT);
	}
	public void registTouchEvent(){
		registEvents(EV_TOUCH);
	}

	public boolean isReadable(){
		return isReady(EV_READ);
	}
	public boolean isWritable(){
		return isReady(EV_WRITE);
	}
	public boolean isAcceptable(){
		return isReady(EV_ACCEPT);
	}
	public boolean isTouchable(){
		return isReady(EV_TOUCH);
	}


}










