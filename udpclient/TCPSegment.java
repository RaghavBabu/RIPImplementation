package edu.rit.tcpWithudp.udpclient;

import java.io.Serializable;

public class TCPSegment implements Serializable{

	private static final long serialVersionUID = 861849121380376716L;
	
	int offset;
	byte[] payload;
	
	public TCPSegment(int segmentNumber, byte[] payload) {
		this.offset = segmentNumber;
		this.payload = payload;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int segmentNumber) {
		this.offset = segmentNumber;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	
}
