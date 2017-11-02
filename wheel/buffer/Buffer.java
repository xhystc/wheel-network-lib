package com.xhystc.wheel.buffer;

import java.io.File;
import java.io.InputStream;

public interface Buffer
{
	boolean write(InputStream inputStream);
	boolean isReadBufferEmpty();
	boolean isWriteBufferEmpty();
	byte[] peek();
	byte[] read();
	void clearReadBuffer();
	void clearWriteBuffer();
}
