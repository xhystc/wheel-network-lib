
## 示例

### 第一步
实现 Proccessor
```
public class XXXProcessor implements ServerMessageProcessor
{
	@Override
	public void onClientAccept(ChannelConnection connection)
	{
        //处理accept事件
        .....
	}

	@Override
	public void beforeConnectionClose(ChannelConnection connection)
	{
         //处理connection close事件
         .....
	}

	@Override
	public void onMessageRecv(ChannelConnection connection)
	{
		     //处理receive事件
         .....
	}

	@Override
	public void onMessageSend(ChannelConnection connection)
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

    //创建Proccessor和eventHandle
    ServerMessageProcessor processor = new XXXProcessor();
    EventHandler handler = new TcpChannelEventHandler(processor);

    //打开channel,selector
		Selector selector = Selector.open();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.socket().bind (new InetSocketAddress(1234));
		ssc.configureBlocking (false);
    
    //创建ACCEPT事件请求
    EventListenRequest seed = new ChannelEventListenRequest(ssc,handler);
		seed.setRegistEvents(SelectionKey.OP_ACCEPT);

		//创建EventLoop和EventRegister
		EventRegister register = new EventRegisterImpl();
		EventLoop eventLoop = new EventLoop(selector,register,seed);

    //创建7个线程
		ExecutorService service = Executors.newFixedThreadPool(7);
		for(int i=0;i<7;i++){
			service.execute( new EventhandleLoop(selector,register) );
		}
    //开始
		eventLoop.run();
	}
}
```
