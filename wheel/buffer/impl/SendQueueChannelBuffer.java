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

public class SendQueueChannelBuffer implements ChannelBuffer
{
	private int bufferSize = 1024*4;
	private ByteBuffer channelBuffer;
	byte[] inputBuffer;
	private Queue<InputStream> writeQueue;
	private ByteOutputStream readBuffer;

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
		long sum = 0;
		boolean first = true;
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
				}else if(first){
					throw new IOException("remote shutdown read");
				}
				if(temp<count){
					break;
				}
				if (first){
					first = false;
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

	@Override
	public int recvFromChannel(ReadableByteChannel channel) throws IOException
	{

		int sum=0;
		boolean first = true;
		version++;
		while(true){
			channelBuffer.clear();
			int temp = 0;
			temp = channel.read(channelBuffer);
			if(temp==-1|| first&&temp==0){
				throw new IOException("remote shutdown write");
			}
			if(first) {
				first = false;
			}

			channelBuffer.flip();
			if(temp<=0) {
				return sum;
			}
			channelBuffer.get(inputBuffer,0,temp);
			readBuffer.write(inputBuffer,0,temp);
			sum+=temp;
		}
	}

	@Override
	public int readBufferSize()
	{
		return readBuffer.size();
	}

	@Override
	public boolean write(InputStream inputStream)
	{
		if (!inputStream.markSupported())
		{
			inputStream = new BufferedInputStream(inputStream);
		}
		writeQueue.add(inputStream);
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


	@Override
	public byte[] peek()
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
	}

	@Override
	public byte[] read()
	{
		byte[] ret = peek();
		clearReadBuffer();
		return ret;
	}



	@Override
	public void clearReadBuffer()
	{
		readBuffer = new ByteOutputStream(bufferSize);
		version++;
	}

	@Override
	public void clearWriteBuffer()
	{
		writeQueue.clear();
	}
}
