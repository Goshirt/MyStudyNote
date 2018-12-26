/**
 * 2018 All rights Reserved by Wharf T&T Java Development Team
 * 
 * Project		: csbsCRM_jsf20
 * Package		: com.wtt.csbs.crm.nio
 * File			: ClientHandle.java
 * Creation TS	: Dec 25, 2018 3:29:33 PM
 * 
 * ==============================================
 */
package com.wtt.csbs.crm.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * @author Helmet Zhou Yong Zhi D003613
 */
public class ClientHandle implements Runnable{
	private int port;
	private String ip;
	private SocketChannel socketChannel;
	private Selector selector;
	private volatile boolean flag;
	
	
	public ClientHandle(String ip,int port){
		this.ip = ip;
		this.port = port;
		try {
			
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			flag = true;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		try {
			doConnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (flag) {
			try {
				selector.select(1000);
				Set<SelectionKey> keys = selector.selectedKeys();
				SelectionKey selectionKey = null;
				Iterator<SelectionKey> iterator = keys.iterator();
				while(iterator.hasNext()){
					selectionKey = iterator.next();
					iterator.remove();
					try {
						handleInput(selectionKey);
					} catch (Exception e) {
						if (selectionKey != null) {
							selectionKey.cancel();
							if (selectionKey.channel() != null) {
								selectionKey.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleInput(SelectionKey selectionKey) throws IOException{
		if (selectionKey.isValid()) {
			SocketChannel sc = (SocketChannel) selectionKey.channel();
			if (selectionKey.isConnectable()) {
				if (sc.finishConnect()) {
				}else {
					System.exit(1);
				}
			}
			if (selectionKey.isReadable()) {
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				int count = sc.read(buffer);
				if (count > 0) {
					buffer.flip();
					byte[] content = new byte[buffer.remaining()];
					buffer.get(content);
					System.out.println("客户端收到消息："+new String(content,"utf-8"));
				}
				else if (count == 0) {
					System.out.println("客户端没有收到消息");
				}else {
					selectionKey.cancel();
					sc.close();
				}
			}
		}
	}
	private void doConnect()throws IOException{
		if (socketChannel.connect(new InetSocketAddress(ip, port)));
		else {
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}
	
	private void doWrite(SocketChannel socketChannel, String respContent) throws IOException{
		byte[] content = respContent.getBytes();
		ByteBuffer byteBuffer = ByteBuffer.allocate(content.length);
		byteBuffer.put(content);
		byteBuffer.flip();
		socketChannel.write(byteBuffer);
	}

	public void sendMsg(String msg)throws IOException {
		socketChannel.register(selector, SelectionKey.OP_READ);
		doWrite(socketChannel, msg);
	}
	public void stop(){
		flag = false;
	}
}
