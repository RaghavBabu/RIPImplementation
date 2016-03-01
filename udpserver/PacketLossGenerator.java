package edu.rit.tcpWithudp.udpserver;

public class PacketLossGenerator extends Thread {


	public void run() {

		while(true){
			try {
				Thread.currentThread().sleep(6000);
				UDPServer.packetLoss = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
