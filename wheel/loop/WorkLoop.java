package com.xhystc.wheel.loop;

import com.xhystc.wheel.connection.SocketChannelConnection;
import com.xhystc.wheel.connection.ConnectionUtil;
import com.xhystc.wheel.event.manager.EventManager;


public class WorkLoop implements Runnable
{
	private EventManager manager;
	private boolean quit = false;

	public WorkLoop(EventManager manager){
		this.manager=manager;
	}



	public EventManager getManager()
	{
		return manager;
	}

	public void setManager(EventManager manager)
	{
		this.manager = manager;
	}

	@Override
	public void run()
	{
		System.out.println("thread"+Thread.currentThread().toString()+" start");
		ConnectionUtil.inWorkEnv();
		while (!quit){
			SocketChannelConnection connection;
			connection = manager.takeReady();
			if(connection!=null){
				connection.handler().handle(connection,false);
			}else {
				break;
			}
		}
		System.out.println("thread"+Thread.currentThread().toString()+" over");
	}
}










