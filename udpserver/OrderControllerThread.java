package edu.rit.tcpWithudp.udpserver;

import edu.rit.tcpWithudp.udpclient.TCPPacket;


public class OrderControllerThread extends Thread {
	
	public OrderControllerThread() {
	}

	public void run(){

		while(true){

			synchronized (UDPServer.revdPacketsQueue){
				
				if(UDPServer.checkOrder){
					checkForMissingPacket();
				}
				UDPServer.revdPacketsQueue.notify();
			}
			
		}
	}

	private void checkForMissingPacket() {

		while(!UDPServer.revdPacketsQueue.isEmpty()){
			
			int seqNo = UDPServer.revdPacketsQueue.peek().getSequenceNumber();

			if(seqNo == UDPServer.orderCount){
				
				TCPPacket packet = UDPServer.revdPacketsQueue.poll();
				UDPServer.writeList.add(packet);
				
				UDPServer.orderCount = UDPServer.orderCount + 1;
				continue;
			}
			else{
				break;
			}
				
		}
		UDPServer.checkOrder = false;
		
	}

}
