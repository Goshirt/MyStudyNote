## 通道Channel
    既可以从通道中读取数据，又可以写数据到通道。但流的读写通常是单向的。
    通道可以异步地读写。
    通道中的数据总是要先读到一个 Buffer，或者总是要从一个 Buffer 中写入。
     
### 分类：
* FileChannel 从文件中读写数据。
* DatagramChannel 能通过 UDP 读写网络中的数据。
* SocketChannel 能通过 TCP 读写网络中的数据。
* ServerSocketChannel 可以监听新进来的 TCP 连接，像 Web 服务器那样。对每一个新进来的连接都会创建一个 SocketChannel。
      
### FileChannel：
    打开 FileChannel：无法直接打开一个 FileChannel，需要通过使用一个 InputStream、OutputStream 或 RandomAccessFile 
                    来获取一个 FileChannel 实例
        RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
        FileChannel inChannel = aFile.getChannel();
    从 FileChannel 读取数据：
        ByteBuffer buf = ByteBuffer.allocate(48);
        int bytesRead = inChannel.read(buf);// int 值表示了有多少字节被读到了 Buffer 中。如果返回 - 1，表示到了文件末尾
    向 FileChannel 写数据:
        String newData = "New String to write to file..." + System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.clear();
        buf.put(newData.getBytes());
        buf.flip();
        while(buf.hasRemaining()) {// 确保Buffer 中已经没有尚未写入通道的字节
            channel.write(buf);
        }
    关闭 FileChannel：
        channel.close();
### SocketChannel：
    打开 SocketChannel：
          SocketChannel socketChannel = SocketChannel.open();
          socketChannel.connect(new InetSocketAddress("http://jenkov.com", 80));
     读写跟fileChannel一样
     区别是非阻塞模式
          SocketChannel 为非阻塞模式（non-blocking mode）. 设置之后，就可以在异步模式下调用 connect()， read() 和 write() 了
          如果 SocketChannel 在非阻塞模式下，此时调用 connect()，该方法可能在连接建立之前就返回了。为了确定连接是否建立，
          可以调用 finishConnect() 的方法：
                socketChannel.configureBlocking(false);
                socketChannel.connect(new InetSocketAddress("http://jenkov.com", 80));
                while(! socketChannel.finishConnect() ){
                    //wait, or do something else...
                }
### ServerSocketChannel：
    阻塞模式：
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();//打开
    serverSocketChannel.socket().bind(new InetSocketAddress(9999));//监听端口
    while(true){
        SocketChannel socketChannel = serverSocketChannel.accept();//阻塞
        //do something with socketChannel...
    }
    非阻塞模式：
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.socket().bind(new InetSocketAddress(9999));
    serverSocketChannel.configureBlocking(false);
    while(true){
        SocketChannel socketChannel = serverSocketChannel.accept();//accept()会立即返回，导致返回值有可能是null
        if(socketChannel != null){
            //do something with socketChannel...
        }
    }
### DatagramChannel:
    打开 DatagramChannel:
          DatagramChannel channel = DatagramChannel.open();
          channel.socket().bind(new InetSocketAddress(9999));
    接收数据:
          ByteBuffer buf = ByteBuffer.allocate(48);
          buf.clear();
          channel.receive(buf);//接收到的数据包内容复制到指定的 Buffer. 如果 Buffer 容不下收到的数据，多出的数据将被丢弃
    发送数据:
          //这个例子发送一串字符到”jenkov.com” 服务器的 UDP 端口 80。 因为服务端并没有监控这个端口，所以什么也不会发生。
          //也不会通知你发出的数据包是否已收到，因为 UDP 在数据传送方面没有任何保证。
          String newData = "New String to write to file..." + System.currentTimeMillis();
          ByteBuffer buf = ByteBuffer.allocate(48);
          buf.clear();
          buf.put(newData.getBytes());
          buf.flip();
          int bytesSent = channel.send(buf, new InetSocketAddress("jenkov.com", 80));     
### 使用 FileChannel 读取数据到 Buffer 中的示例：
    RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");     
    FileChannel inChannel = aFile.getChannel();  
    ByteBuffer buf = ByteBuffer.allocate(48);
    int bytesRead = inChannel.read(buf);
    while (bytesRead != -1) {
        System.out.println(&quot;Read &quot; + bytesRead);
        buf.flip();
        while(buf.hasRemaining()){
            System.out.print((char) buf.get());
        }
        buf.clear();
        bytesRead = inChannel.read(buf);
    }
    aFile.close();
            
     

## 管道Pipe
    管道是 2 个线程之间的单向数据连接。Pipe有一个 source 通道和一个 sink 通道。数据会被写到 sink 通道，从 source 通道读取
    创建管道：
    Pipe pipe = Pipe.open();
### 向管道写数据：
    Pipe.SinkChannel sinkChannel = pipe.sink();
    String newData = "New String to write to file..." + System.currentTimeMillis();
    ByteBuffer buf = ByteBuffer.allocate(48);
    buf.clear();
    buf.put(newData.getBytes());
    buf.flip();
    while(buf.hasRemaining()) {
        sinkChannel.write(buf);
    }
### 从管道读取数据：
    Pipe.SourceChannel sourceChannel = pipe.source();
    ByteBuffer buf = ByteBuffer.allocate(48);
    int bytesRead = sourceChannel.read(buf);



## 缓冲区Buffer
  > 缓冲区本质上是一块可以写入数据，然后可以从中读取数据的内存。这块内存被包装成 NIO Buffer 对象，并提供了一组方法，用来方便的访问该块内存
### 使用 Buffer 读写数据一般遵循以下四个步骤：
   * 写入数据到 Buffer
   * 调用flip()方法
   * 从 Buffer 中读取数据
   * 调用clear()方法或者compact()方法
   > 当向 buffer 写入数据时，buffer 会记录下写了多少数据。一旦要读取数据，需要通过 flip() 方法将 Buffer 从写模式切换到读模式。在读模式下，可以读取之前写入到 buffer 的所有数据。旦读完了所有的数据，就需要清空缓冲区，让它可以再次被写入。有两种方式能清空缓冲区：调用 clear() 或 compact() 方法:
   * clear() 方法会清空整个缓冲区。
   * compact() 方法只会清除已经读过的数据。任何未读的数据都被移到缓冲区的起始处，新写入的数据将放到缓冲区未读数据的后面。
      
### Buffer的三个基本属性：
  * capacity
  * position
  * limit
            
1. capacity：
> 为一个内存块，Buffer 有一个固定的大小值，也叫 “capacity”。 你只能往里写 capacity 个 byte、long，char 等类型。一旦 Buffer 满了，需要将其清空（通过读数据或者清除数据）才能继续写数据往里写数据

2. position：
> 当你写数据到 Buffer 中时，position 表示当前的位置。初始的 position 值为 0. 当一个 byte、long 等数据写到 Buffer 后,position 会向前移动到下一个可插入数据的 Buffer 单元。position 最大可为 capacity – 1。当读取数据时，也是从某个特定位置读。当将 Buffer 从写模式切换到读模式，position 会被重置为 0。当从 Buffer 的 position 处读取数据时，position 向前移动到下一个可读的位置。

3. limit：
> 在写模式下，Buffer 的 limit 表示你最多能往 Buffer 里写多少数据。 写模式下，limit 等于 Buffer 的 capacity。当切换 Buffer 到读模式时，limit 表示你最多能读到多少数据。因此，当切换 Buffer 到读模式时，limit 会被设置成写模式下的 position 值。换句话说，你能读到之前写入的所有数据（limit 被设置成已写数据的数量，这个值在写模式下就是 position）
### Java NIO 有以下 Buffer 类型
 * ByteBuffer
 * MappedByteBuffer
 * CharBuffer
 * DoubleBuffer
 * FloatBuffer
 * IntBuffer
 * LongBuffer
 * ShortBuffer




## 选择器 Selector：
> 能够检测一到多个 NIO 通道，并能够知晓通道是否为诸如读写事件做好准备的组件。这样，一个单独的线程可以管理多个 channel，从而管理多个网络连接
      
### Selector 的创建:
    通过调用 Selector.open() 方法创建一个 Selector
    Selector selector = Selector.open();
          
### 向 Selector 注册通道:
     为了将 Channel 和 Selector 配合使用，必须将 channel 注册到 selector 上
     channel.configureBlocking(false);
     SelectionKey key = channel.register(selector,Selectionkey.OP_READ);
    与 Selector 一起使用时，Channel 必须处于非阻塞模式下。这意味着不能将 FileChannel 与 Selector 一起使用，因为 FileChannel 不能切换到非阻塞模式。而套接字通道都可以注意 register() 方法的第二个参数。这是一个 “interest 集合”，意思是在通过 Selector 监听 Channel 时对什么事件感兴趣。可以监听四种不同类型的事件：
      Connect
      Accept
      Read
      Write
    通道触发了一个事件意思是该事件已经就绪。所以，某个 channel 成功连接到另一个服务器称为 “连接就绪”。一个 server socket channel 准备好接收新进入的连接称为 “接收就绪”。一个有数据可读的通道可以说是 “读就绪”。等待写数据的通道可以说是 “写就绪”。这四种事件用 SelectionKey 的四个常量来表示：
      SelectionKey.OP_CONNECT
      SelectionKey.OP_ACCEPT
      SelectionKey.OP_READ
      SelectionKey.OP_WRITE
    如果你对不止一种事件感兴趣，那么可以用 “位或” 操作符将常量连接起来，如下：
      int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
          
### SelectionKey:
> 当向 Selector 注册 Channel 时，register() 方法会返回一个 SelectionKey 对象。这个对象包含了一些你感兴趣的属性：
* interest 集合
* ready 集合
* Channel
* Selector
* 附加的对象（可选）
          
1. interest 集合
> 就像向 Selector 注册通道一节中所描述的，interest 集合是你所选择的感兴趣的事件集合。可以通过 SelectionKey 读写 interest 集合，像这样：
      int interestSet = selectionKey.interestOps();
      boolean isInterestedInAccept  = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT；
      boolean isInterestedInConnect = interestSet & SelectionKey.OP_CONNECT;
      boolean isInterestedInRead    = interestSet & SelectionKey.OP_READ;
      boolean isInterestedInWrite   = interestSet & SelectionKey.OP_WRITE;
      可以看到，用 “位与” 操作 interest 集合和给定的 SelectionKey 常量，可以确定某个确定的事件是否在 interest 集合中。

2. ready 集合
> ready 集合是通道已经准备就绪的操作的集合。在一次选择 (Selection) 之后，你会首先访问这个 ready set。可以这样访问 ready 集合：
      int readySet = selectionKey.readyOps();
      可以用像检测 interest 集合那样的方法，来检测 channel 中什么事件或操作已经就绪。但是，也可以使用以下四个方法，它们都会返回一个布尔类型：
       selectionKey.isAcceptable();
       selectionKey.isConnectable();
       selectionKey.isReadable();
       selectionKey.isWritable();

3. Channel + Selector
> 从 SelectionKey 访问 Channel 和 Selector 很简单。如下：
    Channel  channel  = selectionKey.channel();
    Selector selector = selectionKey.selector();
    还可以在用 register() 方法向 Selector 注册 Channel 的时候附加对象。如：
    SelectionKey key = channel.register(selector, SelectionKey.OP_READ, theObject);

4. Selector 选择通道
> 一旦向 Selector 注册了一或多个通道，就可以调用几个重载的 select() 方法。这些方法返回你所感兴趣的事件（如连接、接受、读或写）已经准备就绪的那些通道。下面是 select() 方法：
    int select() //阻塞到至少有一个通道在你注册的事件上就绪了
    int select(long timeout) //阻塞 timeout 毫秒 (参数)。
    int selectNow()  //不会阻塞，不管什么通道就绪都立刻返回,如果自从前一次选择操作后，没有通道变成可选择的，则此方法直接返回零
    
* select() 方法返回的 int 值表示有多少通道已经就绪。亦即，自上次调用 select() 方法后有多少通道变成就绪状态。如果调用 select() 方法，
    因为有一个通道变成就绪状态，返回了 1，若再次调用 select() 方法，如果另一个通道就绪了，它会再次返回 1。如果对第一个就绪的 channel 没有做任何操作，现在就有两个就绪的通道，但在每次 select() 方法调用之间，只有一个通道就绪了。selectedKeys()一旦调用了 select() 方法，并且返回值表明有一个或更多个通道就绪了，然后可以通过调用 selector 的 selectedKeys() 方法，访问 “已选择键集selected key set）” 中的就绪通道。如下所示：
    et selectedKeys = selector.selectedKeys();
当像 Selector 注册 Channel 时，Channel.register() 方法会返回一个 SelectionKey 对象。这个对象代表了注册到该 Selector 的通道。可以通过 SelectionKey 的 selectedKeySet() 方法访问这些对象。

* wakeUp()
    某个线程调用 select() 方法后阻塞了，即使没有通道已经就绪，也有办法让其从 select() 方法返回。只要让其它线程在第一个线程调用 select() 方法的那个对象上调用 Selector.wakeup() 方法即可。阻塞在 select() 方法上的线程会立马返回。如果有其它线程调用了 wakeup() 方法，但当前没有线程阻塞在 select() 方法上，下个调用 select() 方法的线程会立即 “醒来（wake up）”。

* close()
用完 Selector 后调用其 close() 方法会关闭该 Selector，且使注册到该 Selector 上的所有 SelectionKey 实例无效。通道本身并不会关闭。

## 完整的示例:
      Selector selector = Selector.open(); //打开一个selector
      channel.configureBlocking(false);
      SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
      while(true) {
        int readyChannels = selector.select();
        if(readyChannels == 0) continue;
        Set selectedKeys = selector.selectedKeys();
        Iterator keyIterator = selectedKeys.iterator();
        while(keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          if(key.isAcceptable()) {
              // a connection was accepted by a ServerSocketChannel.
          } else if (key.isConnectable()) {
              // a connection was established with a remote server.
          } else if (key.isReadable()) {
              // a channel is ready for reading
          } else if (key.isWritable()) {
              // a channel is ready for writing
          }
          keyIterator.remove();
        }
      }
   注意每次迭代末尾的 keyIterator.remove() 调用。Selector 不会自己从已选择键集中移除 SelectionKey 实例。必须在处理完通道时自己移除。
 下次该通道变成就绪时，Selector 会再次将其放入已选择键集中。
    
## 实例：使用Selector创建一个非阻塞的服务器
    public class EchoServer {
    public static void main(String[] args) throws IOException{
        int ports[]={8001,8002,8003};
        Selector selector = Selector.open();//打开一个选择器
        for(int i=0;i<ports.length;i++){
            ServerSocketChannel serverSocketChannel= ServerSocketChannel.open();//打开服务器套接字通道
            serverSocketChannel.configureBlocking(false);//服务器配置为非阻塞
            ServerSocket serverSocket = serverSocketChannel.socket();//检索与此通道关联的服务器套接字
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ports[i]);//实例化监听地址
            serverSocket.bind(inetSocketAddress);//绑定地址
            //注册选择器，相当于使用accept()方法接收
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器在{0}端口监听"+ports[i]);
        }
        
        while(selector.select()>0){
            Set<SelectionKey> readyKeys = selector.selectedKeys(); //获得selected-keys集合
            Iterator<SelectionKey> it = readyKeys.iterator();
            while(it.hasNext()){
                SelectionKey key =it.next();//取出一个SelectionKey
                if(key.isAcceptable()){
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();  //接收新连接
                    client.configureBlocking(false);
                    ByteBuffer outBuffer = ByteBuffer.allocate(1024);//开辟缓冲区
                    outBuffer.put(("当前时间为: "+new Date()).getBytes());
                    outBuffer.flip();
                    client.write(outBuffer);
                    client.close();
                }
            }//end while
            readyKeys.clear();
        }//end while
     }
   }
