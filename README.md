## NIO

通道(Cannel) 用于源节点与目标节点的连接，在Java NIO中负责缓冲区中数据的传输，Channel本身不存储数据，因此需要配合缓冲区进行传输

### 通道的主要实现类

java.nio.channels.Channel接口：
    FileChannel
    SocketChannel
    ServerSocketChannel
    DatagramChannel

###  获取通道

 java针对支持通道的类提供了getChannel()方法

	本地IO：
       FileInputStream/FileOutputStream
       RandomAccessFile

	网络IO:
		Socket
		ServerSocket
		DatagramSocket
    

   在JDK1.7中的NIO.2针对各个通道提供了静态方法open()
   在JDK1.7中的NIO.2的Files工具类的newByteChannel()

   通道之间的数据传输
   transferFrom()
   transferTo()

### 分散(Scatter)与聚集(Gather)

分散读取(Scatter Reads) 将通道中的数据分散到多个缓冲区中
聚集写入(Gathering Writes) 将多个缓冲区中的数据聚集到通道中

### 字符集 Charset

编码： 字符串->字节数组
解码： 字节数组->字符串

### 使用NIO完成网络通信的三个核心

1.通道(Channel) 负责连接

```java
	java.nio.channels.Channel 接口
      |--SelectableChannel
        |--SocketChannel
        |--ServerSocketChannel
        |--DatagramChannel

        |--Pipe.SinkChannel
        |--Pipe.SourceChannel
```
 2.缓冲区(Buffer) 负责数据的存储

 3.选择器(Selector) 是SelectableChannel的多路复用器，用于监控SelectableChannel的IO状况，

利用 Selector 可使一个单独的线程管理多个 Channel。Selector 是非阻塞 IO 的核心。

当调用 register(Selector sel, int ops) 将通道注册选择器时，选择器

对通道的监听事件，需要通过第二个参数 ops 指定

可以监听的事件类型（**可使用** **SelectionKey** **的四个常量表示**）：

 读 : SelectionKey.OP_READ （1） 

 写 : SelectionKey.OP_WRITE （4） 

 连接 : SelectionKey.OP_CONNECT （8） 

 接收 : SelectionKey.OP_ACCEPT （16） 

```java
SelectionKey类常用方法
    int interestOps() 获取感兴趣事件集合
    int readyOps() 获取通道已经准备就绪的操作的集合
    SelectableChannel channel() 获取注册通道
    Selector selector() 返回选择器
    boolean isReadable() 检测 Channal 中读事件是否就绪
    boolean isWritable() 检测 Channal 中写事件是否就绪
    boolean isConnectable() 检测 Channel 中连接是否就绪
    boolean isAcceptable() 检测 Channel 中接收是否就绪
```
