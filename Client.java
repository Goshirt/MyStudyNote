/**
 * 2018 All rights Reserved by Wharf T&T Java Development Team
 * 
 * Project		: csbsCRM_jsf20
 * Package		: com.wtt.csbs.crm.nio
 * File			: Client.java
 * Creation TS	: Dec 25, 2018 3:29:19 PM
 * 
 * ==============================================
 */
package com.wtt.csbs.crm.nio;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Helmet Zhou Yong Zhi D003613
 */
public class Client {
	
	private static int DEFAULT_PORT = 10002;
	private static String HOST="192.168.190.43";
	private static ClientHandle clientHandle = null;
	public static synchronized void start(int port) {
		
		if(clientHandle != null){
			clientHandle.stop();
		}
		clientHandle = new ClientHandle(HOST,port);
		new Thread(clientHandle,"client").start();
		
	}
	
	public static boolean sendMsg(String msg) throws IOException{
		if ("q".equals(msg)) {
			return false;
		}
		clientHandle.sendMsg(msg);
		return true;
	}
	public static void main(String[] args) {
		Client.start(DEFAULT_PORT);
		System.out.println("客户端已经启动");
		String msg = new Scanner(System.in).nextLine();
		try {
			while(sendMsg(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
