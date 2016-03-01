package edu.rit.tcpWithudp.udpserver;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import edu.rit.tcpWithudp.udpclient.TCPPacket;


public class UDPServer {

	public static Queue<TCPPacket> revdPacketsQueue = new PriorityQueue<TCPPacket>();
	public static Queue<TCPPacket> writeList = new ArrayBlockingQueue<TCPPacket>(100);
	public static int orderCount = 0;
	public static boolean checkOrder = false;
	public static boolean packetLoss = false;

	public static void main(String args[]) throws Exception
	{

		DatagramSocket server = new DatagramSocket(9090);
		List<Integer> sequenceList = new ArrayList<Integer>();

		//File Writer
		FileWriterProcess writer = new FileWriterProcess();
		writer.start();


		//packet loss generator.
		//PacketLossGenerator generator = new PacketLossGenerator();
		//generator.start();

		//packet ordering.
		OrderControllerThread orderThread = new OrderControllerThread();
		orderThread.start();

		byte[] rcvData = new byte[1500];
		byte[] sndData = new byte[50];

		while(true)
		{
			synchronized (revdPacketsQueue) {

				DatagramPacket rcvPacket;

				rcvPacket = new DatagramPacket(rcvData, rcvData.length);
				server.receive(rcvPacket);

				if(!packetLoss){

					ByteArrayInputStream byteArrInptStream = new ByteArrayInputStream(rcvData);
					ObjectInputStream in = new ObjectInputStream(byteArrInptStream);
					TCPPacket packet = (TCPPacket)in.readObject();

					sequenceList.add(packet.getSequenceNumber() );
					revdPacketsQueue.add(packet);

					System.out.println("--------------------"+packet.getSequenceNumber()+"--------------");
					String str = new String( packet.getSegment().getPayload());
					System.out.println("From Client : \n" + str);
				}
				else{
					System.out.println("********Packet Dropped******** ");
					//generator.interrupt();
					packetLoss = false;
					continue;
				}

					checkOrder = true;
					revdPacketsQueue.wait();

					//System.out.println("Order count = " + orderCount);
					int packetTobeReceived = orderCount;

					InetAddress ipAddress = rcvPacket.getAddress();
					//int port = rcvPacket.getPort();

					String reply = String.valueOf(packetTobeReceived);
					sndData = reply.getBytes();


					DatagramPacket sndPacket =  new DatagramPacket(sndData, sndData.length, ipAddress, 9091);
					server.send(sndPacket);
				
			}

		}
	}
}



