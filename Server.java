/**
 * 2018 All rights Reserved by Wharf T&T Java Development Team
 * 
 * Project		: csbsCRM_jsf20
 * Package		: com.wtt.csbs.crm.nio
 * File			: Server.java
 * Creation TS	: Dec 25, 2018 11:59:27 AM
 * 
 * ==============================================
 */
package com.wtt.csbs.crm.nio;


/**
 *
 * @author Helmet Zhou Yong Zhi D003613
 */
public class Server {
	private static int DEFAULT_PORT = 10002;
	private static ServerHandel serverHandel = null;
	public static synchronized void start(int port) {
		
		if(serverHandel != null){
			serverHandel.stop();
		}
		serverHandel = new ServerHandel(port);
		new Thread(serverHandel,"server").start();
		
	}
	public static void main(String[] args) {
		Server.start(DEFAULT_PORT);
		System.out.println("sssss");
	}
}
