package edu.rit.udp.client;

import java.util.Queue;

import edu.rit.udp.client.CongestionWindow;

public class SlidingWindowControlThread extends Thread {

	CongestionWindow window;
	Queue<TCPPacket> sendListQueue;
	boolean run = true;

	public SlidingWindowControlThread(CongestionWindow window, Queue<TCPPacket> sendListQueue){
		this.window = window;
		this.sendListQueue = sendListQueue;
	}

	public void run(){

		while(run){

			synchronized (window.sendWindowQueue) {

				if(CongestionWindow.windowSize == CongestionWindow.sshThreshold){
					CongestionWindow.windowSize = CongestionWindow.windowSize + (1 / CongestionWindow.windowSize);
					CongestionWindow.sshThreshold = CongestionWindow.windowSize / 2;
				}
				
				if(window.sendWindowQueue.size() != CongestionWindow.windowSize){
					
					System.out.println("SW Thread  ==== Window Size : "+CongestionWindow.windowSize );

					System.out.println("SW Thread  ==== Send List Size : "+sendListQueue.size() );
					
					if(sendListQueue.size() != 0){
						removeForSendListAndAddtoSendQueue(sendListQueue, window);
						System.out.println("SW Thread  ==== SendWindowQueue size after updating = "+ window.sendWindowQueue.size() +" "+
							", Queue head is :"+window.sendWindowQueue.peek().getSequenceNumber());
					}
					else{
						run = false;
					}
					window.sendWindowQueue.notify();
					System.out.println("--------------------------------------------------------");
				}

			}
		}


	}

	private void removeForSendListAndAddtoSendQueue(Queue<TCPPacket> sendListQueue, CongestionWindow window) {

		int n = sendListQueue.size();


		if(sendListQueue.isEmpty() ){
			//return;
			System.exit(0);
		}

		if( n < CongestionWindow.windowSize ){
			/*System.out.println("---------REMOVING all as list < window size----------- "
					+ CongestionWindow.windowSize+" "+sendListQueue.size());*/
			window.sendWindowQueue.addAll(sendListQueue);
			sendListQueue.clear();
		}
		else {	
			int c = 1;

			while(c <= CongestionWindow.windowSize){
				//System.out.println("---------REMOVING----------- "+ CongestionWindow.windowSize+" "+sendListQueue.size());
				window.sendWindowQueue.add(sendListQueue.poll());
				c++;
			}
		}

	}

}
