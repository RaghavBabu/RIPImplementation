package edu.rit.udp.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.rit.udp.client.TCPPacket;


public class FileWriterProcess extends Thread {

	File file;
	FileWriter fw = null;
	BufferedWriter bw = null;

	public FileWriterProcess() throws IOException {

		file = new File("output.txt");

		try {
			fw = new FileWriter(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void run() {

		while(true){

			while(!UDPServer.writeList.isEmpty()){

				bw = new BufferedWriter(fw);
				try {
					//System.out.println("Writing ********"+packet.getSequenceNumber());
					TCPPacket packet = UDPServer.writeList.poll();
					String payload = new String(packet.getSegment().getPayload());
					bw.write(payload);
					bw.flush();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


}


