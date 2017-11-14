
## 示例

### 第一步
实现 Proccessor
```
public class XXXProcessor implements ServerMessageProcessor
{
	@Override
	public void onClientAccept(SocketChannelConnection connection)
	{
        //处理accept事件
        .....
	}

	@Override
	public boolean onConnectionTimeout(SocketChannelConnection connection)
	{
        //处理connection close事件
        .....
	}

	@Override
	public void onMessageRecv(SocketChannelConnection connection,long nread)
	{
	       //处理receive事件
         .....
	}

	@Override
	public void onMessageSend(SocketChannelConnection connection,long nwrite)
	{
        //处理send事件
        .....
	}
}


```


### 第二步
创建相关对象，启动线程
```
public class Demo
{
	static public void main(String[] args) throws IOException
	{
		//打开selector，创建eventmanager
		Selector selector = Selector.open();
		EventLoopThreadFactory factory = new EventLoopThreadFactory(8);
		EventManager manager = new EventManagerImpl(selector,factory);

		//创建proccessor、eventhandler
		ServerMessageProcessor processor = new SimpleHttpMessageProcessor();
		ConnectionHandler handler = new ServerChannelConnectionHandler(processor);

		//打开ServerSocketChannel，创建poller、EventLoop
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.socket().bind (new InetSocketAddress(1234));
		ssc.configureBlocking (false);
		ChannelConnection seed = new ServerSocketChannelConnectionImpl(ssc,handler,manager);
		Poller poller = new SelectorPoller(selector);
		EventLoop eventLoop = new EventLoop(poller,manager,seed);

		//创建线程池、启动work线程
		ExecutorService pool = new ThreadPoolExecutor(8,8,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(),factory);
		WorkLoop workLoop = new WorkLoop(manager);
		for(int i=0;i<8;i++){
			pool.execute(workLoop);
		}

		//启动事件循环
		eventLoop.run();

	}
}
```
