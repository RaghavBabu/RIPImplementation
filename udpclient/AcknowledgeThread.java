package edu.rit.tcpWithudp.udpclient;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AcknowledgeThread extends Thread{

	DatagramSocket clientAck;
	CongestionWindow window;
	SendThread sendThread;
	byte[] recvData = new byte[1024];
	Map<String, Integer> seqNoAckMap = new HashMap<String, Integer>();

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
				
				if(!seqNoAckMap.containsKey(str)){
					seqNoAckMap.put(str, 0);
				}else{
					seqNoAckMap.replace(str, seqNoAckMap.get(str) + 1);
				}
					
				//Received three dup Ack, so set window size to 1 and initiate fast retransmit.
				if(seqNoAckMap.get(str) == CongestionWindow.dupAck){
					System.out.println("3 dup acks received ----------> "+str);
					sendThread.initiateFastRetransmit(str);
					CongestionWindow.windowSize = 0;
				}
				
				//update sliding window, ack window and increase sliding window size.
				synchronized (window.bufferQueue) {
					//updateAckWindowUponReceival(str);					
					updateBufferQueueUponAckReceival(str);
					updateWindowSizeForEachAck();
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

	private void updateBufferQueueUponAckReceival(String str) {

		int seqNo = new Integer(str.trim());

		while(!window.bufferQueue.isEmpty()) {

			if(window.bufferQueue.peek().getSequenceNumber() < seqNo){
				System.out.println("Packet removed from buffer : " +window.bufferQueue.poll().getSequenceNumber() );
			}else
				break;

		}
		if(window.bufferQueue.peek() != null)
			System.out.println("Removed from buffer Queue , current head is "+window.bufferQueue.peek().getSequenceNumber());
	}

	/**
	 * remove from ackWindow so that it wont be sent again on time out.
	 * @param str
	 */
	/*private void updateAckWindowUponReceival(String str) {

		int seqNo = new Integer(str.trim());

		Iterator<Integer> it = window.ackWindowList.iterator();

		//System.out.println("Before removal : "+window.ackWindowList.size());

		while(it.hasNext()){

			int i = it.next();

			if(i < seqNo)
				it.remove();
			System.out.println("Packet removed from ack List : " +i );
		}
		//System.out.println("Aftr removal from ackWindow : "+window.ackWindowList.size());	
	}*/

	/**
	 * Update congestion window size for each ack received.
	 */
	private void updateWindowSizeForEachAck() {
		CongestionWindow.windowSize += 1;
		System.out.println("Packet Ack received, Window size updated : "+CongestionWindow.windowSize);
	}

}
