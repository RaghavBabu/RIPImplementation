package edu.rit.tcpWithudp.udpclient;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class UDPClient {

	public static Queue<TCPPacket> sendListQueue = new ArrayBlockingQueue<TCPPacket>(10000);
	Map<Integer, TCPPacket> seqNoTCPPacketMap = new HashMap<Integer, TCPPacket>();
	
	public static void main(String args[]) throws Exception
	{
		DatagramSocket client = null;
		DatagramSocket clientAck = null;

		UDPClient udpClient = new UDPClient();
		InetAddress ipAddress = InetAddress.getByName("localhost");

		//Queue holds total TcpPackets
		
		CongestionWindow window = new CongestionWindow();

		client = new DatagramSocket();
		clientAck = new DatagramSocket(9091);

		int size = 1024;
		List<TCPSegment> segments = udpClient.readFileAndCreateSegments(size);

		int seqNo = 0;

		for(TCPSegment segment : segments){

			TCPPacket packet = new TCPPacket(seqNo, segment);
			sendListQueue.add(packet);
			udpClient.seqNoTCPPacketMap.put(seqNo, packet);
			seqNo++;
		}

		//start send and receive Threads
		udpClient.startThreads(client,clientAck, ipAddress, window, sendListQueue,udpClient);
	}

	private void startThreads(DatagramSocket client, DatagramSocket clientAck, InetAddress ipAddress, CongestionWindow window, Queue<TCPPacket> sendListQueue, UDPClient udpClient) {
		
		SendThread sendThread = new SendThread(client, window, ipAddress,  udpClient);
		sendThread.start();

		AcknowledgeThread ackThread = new AcknowledgeThread(clientAck, window, sendThread);
		ackThread.start();
	}

	private List<TCPSegment> readFileAndCreateSegments(int size) throws Exception{

		byte[] sndData = null;
		List<TCPSegment> segmentsList = new ArrayList<TCPSegment>();

		//stores file in byte array.
		byte[] bytes = Files.readAllBytes(new File("input.txt").toPath());
		int i = 0;
		int offSet = 1;

		while( i < bytes.length ) {

			int j = 0;
			sndData = new byte[1024];

			while(i < size * offSet ){

				if(i < bytes.length)
					sndData[j] = bytes[i];
				else
					break;

				i += 1;
				j += 1;
			}

			TCPSegment tcpSegment  = new TCPSegment(size *(offSet - 1), sndData);
			offSet += 1;
			segmentsList.add(tcpSegment);
		}
		return segmentsList;
	}


}
