package com.xhystc.wheel.poller;

import com.xhystc.wheel.connection.ChannelConnection;
import com.xhystc.wheel.connection.ServerSocketChannelConnection;
import com.xhystc.wheel.connection.SocketChannelConnection;
import com.xhystc.wheel.event.ChannelEvent;

import java.io.IOException;
import java.nio.channels.*;
import java.util.*;

public class SelectorPoller implements Poller
{
	private Selector selector;
	private Map<Channel,ChannelConnection> connectionMap = new HashMap<>();


	public SelectorPoller(Selector selector){
		this.selector = selector;
	}


	@Override
	public List<ChannelConnection> poll(long mill) throws IOException
	{

		int nready = selector.select(mill);
		List<ChannelConnection> ret = new ArrayList<>(nready);

		if(nready>0){
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keys.iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				iterator.remove();
				SelectableChannel channel =key.channel();
				ChannelConnection connection = connectionMap.get(channel);
				assert connection!=null;
				addEvent(connection);
				ret.add(connection);
				if(channel instanceof ServerSocketChannel){
					continue;
				}
				channel.register(selector,0);
			}
		}
		return ret;
	}

	@Override
	public List<ChannelConnection> poll() throws IOException
	{
		return poll(0);
	}


	@Override
	public void regist(ChannelConnection cc){
		int i = 0;
		cc.event().setEvents(0);
		if(cc instanceof ServerSocketChannelConnection){
			i |= ChannelEvent.EV_ACCEPT;
		}else {
			SocketChannelConnection connection = (SocketChannelConnection) cc;
			if(!connection.isShutdownRead()){
				i |= ChannelEvent.EV_READ;
			}
			if(!connection.isShutdownWrite() && !connection.sendOver())	{
				i |= ChannelEvent.EV_WRITE;
			}
		}
		if(i==0 || cc.isShutdown()){
			connectionMap.remove(cc.channel());
			SelectionKey key = ((SelectableChannel) cc.channel()).keyFor(selector);
			if(key!=null){
				key.cancel();
			}
			System.out.println("connection remove:"+connectionMap.size());
		}else {
			connectionMap.put(cc.channel(),cc);
			doregist(cc,i);
		}
	}

	@Override
	public void regist(List<? extends ChannelConnection> connections)
	{
		for(ChannelConnection connection : connections){
			regist(connection);
		}
	}

	private void doregist(ChannelConnection connection, int events)
	{
		assert events>0;
		SelectableChannel channel = (SelectableChannel) connection.channel();
		try
		{
			int i = channel.keyFor(selector)==null?0:channel.keyFor(selector).interestOps();
			channel.register(selector, i|events);
		}catch (IOException ioe){
			connectionMap.remove(channel);
		}
	}

	private void addEvent(ChannelConnection connection){
		ChannelEvent event = connection.event();
		SelectableChannel channel = (SelectableChannel) connection.channel();
		SelectionKey key = channel.keyFor(selector);
		if(key.isAcceptable()){
			event.addAcceptable();
		}
		if(key.isWritable()){
			event.addWriteable();
		}
		if(key.isReadable()){
			event.addReadable();
		}
		if(key.isConnectable()){
			event.addConnection();
		}
	}
}
















