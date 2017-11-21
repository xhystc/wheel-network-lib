package com.xhystc.wheel.example;

import com.xhystc.wheel.Processor.ServerMessageProcessor;
import com.xhystc.wheel.connection.ChannelConnection;
import com.xhystc.wheel.connection.ServerSocketChannelConnection;
import com.xhystc.wheel.connection.SocketChannelConnection;
import com.xhystc.wheel.connection.impl.ServerSocketChannelConnectionImpl;
import com.xhystc.wheel.connection.impl.SocketChannelConnectionImpl;
import com.xhystc.wheel.event.handler.ServerChannelConnectionHandler;
import com.xhystc.wheel.event.manager.EventManager;
import com.xhystc.wheel.event.manager.EventManagerImpl;
import com.xhystc.wheel.event.handler.ConnectionHandler;
import com.xhystc.wheel.loop.EventLoop;
import com.xhystc.wheel.example.http.SimpleHttpMessageProcessor;
import com.xhystc.wheel.loop.EventLoopThreadFactory;
import com.xhystc.wheel.loop.WorkLoop;
import com.xhystc.wheel.poller.Poller;
import com.xhystc.wheel.poller.SelectorPoller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.*;

public class Demo
{
	static public void main(String[] args) throws IOException
	{

		Selector selector = Selector.open();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.socket().bind (new InetSocketAddress(1234));
		ssc.configureBlocking (false);

		ExecutorService service = Executors.newCachedThreadPool();

		ServerMessageProcessor processor = new SimpleHttpMessageProcessor(service);
		EventLoopThreadFactory factory = new EventLoopThreadFactory(8);

		ExecutorService pool = new ThreadPoolExecutor(8,10,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(),factory);
		EventManager register = new EventManagerImpl(selector,factory);
		BlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();
		WorkLoop workLoop = new WorkLoop(register);
		for(int i=0;i<8;i++){

			pool.execute(workLoop);
		}

		ConnectionHandler handler = new ServerChannelConnectionHandler(processor);
		ChannelConnection seed = new ServerSocketChannelConnectionImpl(ssc,handler,register);

		Poller poller = new SelectorPoller(selector);

		EventLoop eventLoop = new EventLoop(poller,register,seed);



		eventLoop.run();
	}
}








