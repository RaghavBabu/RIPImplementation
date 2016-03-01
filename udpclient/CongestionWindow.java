package edu.rit.tcpWithudp.udpclient;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


public class CongestionWindow {
	
	public static volatile int windowSize = 1;
	public static final int dupAck = 3;
	public static final int TimeOut = 1;
	public static int sshThreshold = 30;
	Queue<TCPPacket> bufferQueue = new PriorityQueue<TCPPacket>();
	Queue<TCPPacket> sendQueue = new PriorityQueue<TCPPacket>();
	Queue<TCPPacket> urgentSentWindowQueue = new ArrayBlockingQueue<TCPPacket>(10000);
	List<Integer> ackWindowList = new ArrayList<Integer>();
	
	
}
