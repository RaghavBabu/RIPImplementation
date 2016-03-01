package edu.rit.udp.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;

public class AcknowledgeThread extends Thread{

	DatagramSocket clientAck;
	CongestionWindow window;
	SendThread sendThread;
	byte[] recvData = new byte[1024];
	
	public AcknowledgeThread(DatagramSocket clientAck, CongestionWindow window, SendThread sendThread) {
		this.clientAck = clientAck;
		this.window = window;
		this.sendThread = sendThread;
	}

	@Override
	public void run() {
		
		while(true){
			
			DatagramPacket replyPacket = new DatagramPacket(recvData, recvData.length);
			try {
				clientAck.receive(replyPacket);
				
				byte[] receivedData = replyPacket.getData();
				String str = new String(receivedData);
				System.out.println("Ack Thread  ==== From Server, Expecting "+str);
				
				
				
				//update sliding window, ack window and increase sliding window size.
				updateAckWindowUponReceival(str);
				
				synchronized (window.sendWindowQueue) {
					updateSlidingWindowUponAckReceival(str);
					updateWindowSizeForEachAck();
					window.sendWindowQueue.notify();
				}
				
							
				
			} catch (Exception e) {
				System.out.println("Exception while receiving acknowledgement "+ e);
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * remove from sendWindowQueue after receiving the acknowledgment.
	 * else dont poll it from sendWindowQueue.
	 * @param str
	 */

	private void updateSlidingWindowUponAckReceival(String str) {
		
		int seqNo = new Integer(str.trim());
		
		while(!window.sendWindowQueue.isEmpty()) {
			
			if(window.sendWindowQueue.peek().getSequenceNumber() < seqNo){
				window.sendWindowQueue.poll();
			}else
				break;
			
		}
		if(window.sendWindowQueue.peek() != null)
			System.out.println("Removed from sliding window , current head is "+window.sendWindowQueue.peek().getSequenceNumber());
	}

	/**
	 * remove from ackWindow so that it wont be sent again on time out.
	 * @param str
	 */
	private void updateAckWindowUponReceival(String str) {
		
		int seqNo = new Integer(str.trim());
		
		Iterator<Integer> it = window.ackWindowList.iterator();
		
		//System.out.println("Before removal : "+window.ackWindowList.size());
		
		while(it.hasNext()){
			
			int i = it.next();
			
			if(i < seqNo)
				it.remove();
		}
		//System.out.println("Aftr removal from ackWindow : "+window.ackWindowList.size());	
	}

	/**
	 * Update congestion window size for each ack received.
	 */
	private void updateWindowSizeForEachAck() {
		CongestionWindow.windowSize += 1;
		System.out.println("Window size updated upon packet receival : "+CongestionWindow.windowSize);
	}

}
