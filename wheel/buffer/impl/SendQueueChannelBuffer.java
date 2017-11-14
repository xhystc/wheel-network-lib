package com.xhystc.wheel.buffer.impl;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.xhystc.wheel.buffer.ChannelBuffer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class SendQueueChannelBuffer implements ChannelBuffer
{
	private int bufferSize = 1024*4;
	private final ByteBuffer channelBuffer;
	final byte[] inputBuffer;
	private final Queue<InputStream> writeQueue;
	private final ByteOutputStream readBuffer;



	int version = 0;
	int lastPeekVersion = 0;
	byte[] lastPeek = null;

	public SendQueueChannelBuffer(int bufferSize){
		this.bufferSize = bufferSize;
		channelBuffer = ByteBuffer.allocateDirect(bufferSize);
		inputBuffer = new byte[bufferSize];
		writeQueue = new LinkedList<>();
		readBuffer = new ByteOutputStream(bufferSize);
	}

	@Override
	public long sendToChannel(WritableByteChannel channel) throws IOException
	{

		synchronized (writeQueue){
			long sum = 0;
			while( writeQueue.peek()!=null){
				InputStream inputStream = writeQueue.peek();
				boolean readover = false;
				while (true){
					inputStream.mark(bufferSize);
					int count = inputStream.read(inputBuffer,0,bufferSize);
					if (count<=0){
						readover = true;
						break;
					}
					inputStream.reset();
					channelBuffer.clear();
					channelBuffer.put(inputBuffer,0,count);
					channelBuffer.flip();
					int temp = 0;

					temp = channel.write(channelBuffer);
					if(temp>0){
						sum+=temp;
						inputStream.skip(temp);
					}
					if(temp<count){
						break;
					}
				}
				if(readover){
					try
					{
						writeQueue.poll().close();
					}catch (IOException ioe){
						ioe.printStackTrace();
					}
				}
				else {
					return sum;
				}
			}
			return sum;
		}
	}

	@Override
	public long recvFromChannel(ReadableByteChannel channel) throws IOException
	{

		synchronized (readBuffer){
			long sum=0;
			version++;
			while(true){
				channelBuffer.clear();
				int temp = 0;
				temp = channel.read(channelBuffer);
				if(temp==-1){
					throw new IOException("remote shutdown write");
				}

				channelBuffer.flip();
				if(temp==0) {
					return sum;
				}
				channelBuffer.get(inputBuffer,0,temp);
				readBuffer.write(inputBuffer,0,temp);
				sum+=temp;
			}
		}
	}

	@Override
	public int readBufferSize()
	{
		synchronized (readBuffer){
			return readBuffer.size();
		}

	}

	@Override
	public boolean write(InputStream inputStream)
	{
		if (!inputStream.markSupported())
		{
			inputStream = new BufferedInputStream(inputStream);
		}
		synchronized (writeQueue){
			writeQueue.add(inputStream);
		}
		return true;
	}

	@Override
	public boolean isReadBufferEmpty()
	{
		return readBuffer.size()==0;
	}

	@Override
	public boolean isWriteBufferEmpty()
	{
		return writeQueue.size()==0;
	}



	private byte[] doPeek(boolean clear)
	{
		synchronized (readBuffer){
			try
			{
				if(version==lastPeekVersion)
				{
					return lastPeek;
				}
				if (readBuffer.size()<=0)
				{
					return new byte[0];
				}
				lastPeek = Arrays.copyOf(readBuffer.getBytes(),readBuffer.size());
				lastPeekVersion=version;
				return lastPeek;
			}finally
			{
				if(clear){
					readBuffer.reset();
					version++;
				}
			}

		}
	}

	@Override
	public byte[] peek(){
		return doPeek(false);
	}
	@Override
	public byte[] read()
	{
		return doPeek(true);
	}



	@Override
	public void clearReadBuffer()
	{
		synchronized (readBuffer){
			readBuffer.reset();
			version++;
		}
	}

	@Override
	public void clearWriteBuffer()
	{
		synchronized (writeQueue){
			writeQueue.clear();
		}
	}
}
