package edu.rit.udp.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendThread extends Thread {

	DatagramSocket client;
	CongestionWindow window;
	InetAddress ipAddress;
	ByteArrayOutputStream baos = null;
	
	public SendThread(DatagramSocket client, CongestionWindow window, InetAddress ipAddress) {
		this.client = client;
		this.window = window;
		this.ipAddress = ipAddress;
	}

	@Override
	public void run() {


		while(true) 
		{	
			TCPPacket packetToSend = null;
			
			while(!window.urgentSentWindowQueue.isEmpty()){
				packetToSend = window.urgentSentWindowQueue.poll();
				System.out.println("Send Thread  ==== Time Out,so sending urgent Packet : "+packetToSend.getSequenceNumber());
				sentPacket(packetToSend);
			}

				synchronized (window.sendWindowQueue) {

					while(!window.sendWindowQueue.isEmpty()) {
						
						packetToSend = window.sendWindowQueue.peek();
					
						if(!window.ackWindowList.contains(packetToSend)){
							System.out.println("Send Thread  ==== Sending : "+packetToSend.getSequenceNumber());
							sentPacket(packetToSend);
						}
						
						try {
							window.sendWindowQueue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
			}
		}
	}

	public void sentPacket(TCPPacket packetToSend) {
		
		ByteArrayOutputStream baos = null;

		try {
			baos = new ByteArrayOutputStream(1500);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.flush();
			oos.writeObject(packetToSend);
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
			window.ackWindowList.add(packetToSend.getSequenceNumber() );
			checkForTimeOut(packetToSend);
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
		
		if(window.ackWindowList.contains(packetToSend.getSequenceNumber())){
			window.urgentSentWindowQueue.add(packetToSend);
			CongestionWindow.windowSize = 1;
		}
		
		
	}

}
