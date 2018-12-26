/**
 * 2018 All rights Reserved by Wharf T&T Java Development Team
 * 
 * Project		: csbsCRM_jsf20
 * Package		: com.wtt.csbs.crm.nio
 * File			: ServerHandel.java
 * Creation TS	: Dec 25, 2018 11:59:53 AM
 * 
 * ==============================================
 */
package com.wtt.csbs.crm.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * @author Helmet Zhou Yong Zhi D003613
 */
public class ServerHandel implements Runnable{
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private volatile boolean flag;
	
	
	
	/**
	 * 
	 */
	public ServerHandel() {
		super();
	}


	/**
	 * 构造器，启动服务器
	 */
	public ServerHandel(int port) {
		try {
			//创建一个选择器
			selector = Selector.open();
			//打开通道
			serverSocketChannel = ServerSocketChannel.open();
			//设置为非阻塞模式，当为true时为阻塞模式
			serverSocketChannel.configureBlocking(false);
			//port：监听的端口    1024：请求传入连接队列的最大长度
			serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
			//监听客户端链接请求
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			//标志服务器已经启动
			flag = true;
			System.out.println("服务器已经启动，端口号为："+port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void stop(){
		flag = false;
	}
	
	
	@Override
	public void run() {
		//当服务器是启动的，循环遍历selector
		while(flag){
			try {
				//不管是否有数据写入，每隔1秒唤醒一次selector
				selector.select(1000);
				//这种写法是阻塞的，只有当最少一个注册的时间发生时，才会继续执行下去
				//selector.select();
				//获取Selector中注册的所有key
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				SelectionKey  key = null;
				while (iterator.hasNext()) {
					key = iterator.next();
					iterator.remove();
					try {
						handleInput(key);
					}
					//当处理客户端输入的消息有异常抛出时，关闭必要的连接资源
					catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//关闭selector
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 处理客户端发送的消息
	 * @param key
	 * @throws IOException
	 */
	private void handleInput(SelectionKey key)throws IOException{
		//判断此密钥是否有效。密钥在创建时有效并保持不变，直到它被取消，其通道关闭或其选择器关闭。
		if (key.isValid()) {
			//处理接入的请求的消息
			if (key.isAcceptable()) {
				//通过key获取ServerSocketChannel
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				//通过ServerSocketChannel获取SocketChannel，当该步骤完成之后就会完成TCP三次握手，建立连接
				SocketChannel sc = ssc.accept();
				//设置为非阻塞模式
				sc.configureBlocking(false);
				//注册为读模式
				sc.register(selector, SelectionKey.OP_READ);
			}
			//读消息
			if (key.isReadable()) {
				//通过key获取与客户端连接SocketChannel通道
				SocketChannel sc = (SocketChannel) key.channel();
				//创建指定大小的字节缓冲区
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				//从通道中读取数据到缓冲区，并返回读取的字节数
				int count = sc.read(buffer);
				if (count > 0) {
					//把缓冲区当前的limit的position设置为0，用于后续的读取
					buffer.flip();
					//根据缓冲区中可读的字节数创建字节数组
					byte[] content = new byte[buffer.remaining()];
					//把缓冲区中的数据读取到新建的数组中
					buffer.get(content);
					System.out.println("client send ： "+new String(content, "utf-8"));
					//返回消息给客户端
					doWrite(sc, "行啦，我收到你消息了");
					
				}else if (count == 0) {
					System.out.println("服务器没有收到消息");
				}
				else {
					key.cancel();
					sc.close();
				}
			}
		}
	}
	
	/**
	 * 服务器响应消息返回客户端
	 * @param sChannel
	 * @param respContent
	 * @throws IOException
	 */
	private void doWrite(SocketChannel sChannel, String respContent)throws IOException{
		//把字符串转为字节数组
		byte[] content = respContent.getBytes();
		//根据数组的大小创建ByteBuffer缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(content.length);
		//把字节数组的内容送到缓冲区
		buffer.put(content);
		//把缓冲区当前的limit的position设置为0，用于后续的写
		buffer.flip();
		//把缓冲区的内容写到通道中
		sChannel.write(buffer);
	}
	
	
	

}
