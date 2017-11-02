package com.xhystc.wheel.test;

import com.xhystc.wheel.connection.ChannelConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

public class CloseTest
{
	public static void main(String[] args) throws IOException
	{
		Selector selector = Selector.open();
		ServerSocketChannel ssc = ServerSocketChannel.open();

		ssc.socket().bind (new InetSocketAddress(1234));
		ssc.configureBlocking (false);
		ssc.register(selector,SelectionKey.OP_ACCEPT);
		run r = new run();
		r.channel = ssc;
		r.selector=selector;
		Thread t = new Thread(r);
		t.start();

		int res = selector.select();
		System.out.println("res:"+res);
		Set<SelectionKey> keys = selector.selectedKeys();
		for(SelectionKey key : keys){
			System.out.println("accept:"+key.isAcceptable());
			System.out.println("read:"+key.isReadable());
			System.out.println("write:"+key.isWritable());
		}
	}
	static class run implements Runnable{
		ServerSocketChannel channel;
		Selector selector;
		@Override
		public void run()
		{
			try
			{
				System.out.println("thread start");
				Thread.sleep(1000);
				channel.keyFor(selector).cancel();
				System.out.println("close over");
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}
	}
}
