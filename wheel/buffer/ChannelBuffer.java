package com.xhystc.wheel.buffer;


import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public interface ChannelBuffer extends Buffer
{
	long sendToChannel(WritableByteChannel channel) throws IOException;
	int recvFromChannel(ReadableByteChannel channel) throws IOException;
	int readBufferSize();
}
