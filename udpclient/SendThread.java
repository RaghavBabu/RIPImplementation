package edu.rit.tcpWithudp.udpclient;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;

public class SendThread extends Thread {

	DatagramSocket client;
	CongestionWindow window;
	InetAddress ipAddress;
	ByteArrayOutputStream baos = null;
	UDPClient udpClient;

	public SendThread(DatagramSocket client, CongestionWindow window, InetAddress ipAddress, UDPClient udpClient) {
		this.client = client;
		this.window = window;
		this.ipAddress = ipAddress;
		this.udpClient = udpClient;
	}

	@Override
	public void run() {


		while(true) 
		{	
			TCPPacket packetToSend = null;

			while(!window.urgentSentWindowQueue.isEmpty()){
				packetToSend = window.urgentSentWindowQueue.poll();
				System.out.println("Send Thread  ==== Time Out,so sending urgent Packet : "+packetToSend.getSequenceNumber());

				sentUrgentPacket(packetToSend);
			}

			synchronized (window.bufferQueue) {

				while(window.bufferQueue.size() != CongestionWindow.windowSize ) {

					if(UDPClient.sendListQueue.size() == 0)
						break;

					packetToSend = UDPClient.sendListQueue.poll();

					if(packetToSend != null){
						window.sendQueue.add(packetToSend);
						window.bufferQueue.add(packetToSend);
					}
				}	
			}

			if(!window.bufferQueue.isEmpty() && !window.sendQueue.isEmpty()){
				System.out.println("Send Thread  ==== Buffer queue Size : "+window.bufferQueue.size() );
				sentPacket(window.sendQueue);
				System.out.println("------------------------------------------------------");
			}else if(!window.bufferQueue.isEmpty() ){
				sentPacket(window.bufferQueue);
				System.out.println("------------------------------------------------------");
			}
		}
	}

	public void sentPacket(Queue<TCPPacket> queue) {

		ByteArrayOutputStream baos = null;

		while(!queue.isEmpty()) {

			System.out.println("Send Thread  ==== Sending : "+queue.peek().getSequenceNumber());
			TCPPacket packet = queue.poll();
			try {
				baos = new ByteArrayOutputStream(1500);
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.flush();
				oos.writeObject(packet);
				oos.flush();

			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			byte[] buffer= baos.toByteArray();

			DatagramPacket sndPacket = new DatagramPacket(buffer, buffer.length, ipAddress, 9090);
			try {
				client.send(sndPacket);
				//window.ackWindowList.add(packet.getSequenceNumber() );
				checkForTimeOut(packet);
			} catch (Exception e) {
				System.out.println("Exception while sending packet "+ e);
				e.printStackTrace();
			}
		}
	}


	public void sentUrgentPacket(TCPPacket packet) {

		ByteArrayOutputStream baos = null;

		try {
			baos = new ByteArrayOutputStream(1500);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.flush();
			oos.writeObject(packet);
			oos.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		byte[] buffer= baos.toByteArray();

		DatagramPacket sndPacket = new DatagramPacket(buffer, buffer.length, ipAddress, 9090);
		try {
			client.send(sndPacket);
			//window.ackWindowList.add(packet.getSequenceNumber() );
			checkForTimeOut(packet);
		} catch (Exception e) {
			System.out.println("Exception while sending packet "+ e);
			e.printStackTrace();
		}

	}

	private void checkForTimeOut(TCPPacket packetToSend) {

		try {
			Thread.currentThread().sleep(1*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(window.bufferQueue.contains(packetToSend.getSequenceNumber())){
			window.urgentSentWindowQueue.add(packetToSend);
			CongestionWindow.windowSize = 1;
		}


	}

	public void initiateFastRetransmit(String str) {
		int seqNo = new Integer(str.trim());
		TCPPacket packet = udpClient.seqNoTCPPacketMap.get(seqNo);
		sentUrgentPacket(packet);
		System.out.println("Sending packet "+seqNo+ " since three dupAck's received ");
	}

}
