package com.xhystc.wheel.event.handler;

import com.xhystc.wheel.Processor.ServerMessageProcessor;
import com.xhystc.wheel.connection.SocketChannelConnection;


public class ServerChannelConnectionHandler implements ConnectionHandler
{
	private ServerMessageProcessor processor;

	public ServerChannelConnectionHandler(ServerMessageProcessor processor)
	{
		this.processor = processor;
	}
	public ServerMessageProcessor getProcessor()
	{
		return processor;
	}
	public void setProcessor(ServerMessageProcessor processor)
	{
		this.processor = processor;
	}

	@Override
	public void handle(SocketChannelConnection connection, boolean create)
	{
		if (connection.event().isCreate()){
			processor.onClientAccept(connection);
		}else {
			long t;
			if(connection.event().isWriteable() && !connection.isShutdownWrite() && ( t =  connection.sendToChannel())!=0){
				processor.onMessageSend(connection,t);
			}

			if(connection.event().isReadable() && !connection.isShutdownRead() && (t =  connection.recvFromChannel())!=0){

				processor.onMessageRecv(connection,t);
			}
		}
		connection.manager().regist(connection);
	}

}













