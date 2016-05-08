package yelp.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Recver implements Runnable {
	private ServerSocket socket;
	private Lock msg_lock;
	private Stack<String> recved_msg;
	private Thread t;
	private String threadName;
	
	public Recver(ServerSocket socket, Lock msg_lock, Stack<String> recved_msg) throws IOException {
		this.socket = socket;
		this.msg_lock = msg_lock;
		this.recved_msg = recved_msg;
		threadName = "recver";
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("wait connection");
				Socket clientConn = socket.accept();
				System.out.println("got connection");
				BufferedReader in = new BufferedReader(new InputStreamReader(clientConn.getInputStream()));
				/* recv */
				msg_lock.lock();
				recved_msg.add(in.readLine());
				msg_lock.unlock();
				System.out.println("received: " + recved_msg);
			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		
	}
	
	public void start () {
		System.out.println("Starting " +  threadName );
		if (t == null) {
			t = new Thread (this, threadName);
			t.start ();
		}
	}
}
