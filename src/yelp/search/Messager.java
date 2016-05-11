package yelp.search;

import java.net.*;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;


public class Messager extends Thread {
	private ServerSocket socket;
	private String host;
	private int port;
	private Stack<String> recved_msg;
	private Lock msg_lock;
	
	public Messager(int port) throws IOException {
		socket = new ServerSocket(port);
		socket.setSoTimeout(10000);
		host = "localhost";
		msg_lock = new ReentrantLock();
		recved_msg = new Stack<String>();;
		System.out.println("init messager");
	}
	
	public String pop_msg() {
		msg_lock.lock();
		try {
			String ret = (String) recved_msg.pop();
			msg_lock.unlock();
			return ret;
		} catch (EmptyStackException e) {
			e.printStackTrace();
			msg_lock.unlock();
			return "";
		}
	}
	
	public int msg_size() {
		return recved_msg.size();
	}

	public void send(String remoteHost, int remotePort, String msgToClient) {
		try {
			System.out.println("Connecting to " + remoteHost + " on port "
					+ remotePort);
			Socket conn = new Socket(remoteHost, remotePort);
			System.out.println("Just connected to "
					+ conn.getRemoteSocketAddress());
			PrintWriter out = new PrintWriter(conn.getOutputStream(),true);
			out.println(msgToClient);
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("messager running");
		Recver rc;
		try {
			rc = new Recver(socket, msg_lock, recved_msg);
			rc.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			Messager m = new Messager(9000);
			m.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}