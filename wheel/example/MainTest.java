package com.xhystc.wheel.example;

import com.xhystc.wheel.Processor.ServerMessageProcessor;
import com.xhystc.wheel.event.handler.TcpChannelEventHandler;
import com.xhystc.wheel.event.request.ChannelEventListenRequest;
import com.xhystc.wheel.event.request.EventListenRequest;
import com.xhystc.wheel.event.register.EventRegister;
import com.xhystc.wheel.event.register.EventRegisterImpl;
import com.xhystc.wheel.event.handler.EventHandler;
import com.xhystc.wheel.loop.EventLoop;
import com.xhystc.wheel.loop.EventhandleLoop;
import com.xhystc.wheel.example.http.SimpleHttpMessageProcessor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainTest
{
	static public void main(String[] args) throws IOException
	{

		Selector selector = Selector.open();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.socket().bind (new InetSocketAddress(1234));
		ssc.configureBlocking (false);
		ServerMessageProcessor processor = new SimpleHttpMessageProcessor();
		EventHandler handler = new TcpChannelEventHandler(processor);
		EventListenRequest seed = new ChannelEventListenRequest(ssc,handler);
		seed.setRegistEvents(SelectionKey.OP_ACCEPT);
		EventRegister register = new EventRegisterImpl();
		EventLoop eventLoop = new EventLoop(selector,register,seed);
		ExecutorService service = Executors.newFixedThreadPool(7);
		for(int i=0;i<7;i++){
			service.execute( new EventhandleLoop(selector,register) );
		}
		eventLoop.run();
	}
}








