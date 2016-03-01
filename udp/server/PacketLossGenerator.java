package edu.rit.udp.server;

public class PacketLossGenerator extends Thread {

	public void run() {
		
		try {
			Thread.currentThread().sleep(10000);
			UDPServer.packetLoss = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
