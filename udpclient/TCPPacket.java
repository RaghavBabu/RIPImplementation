package edu.rit.tcpWithudp.udpclient;

import java.io.Serializable;

public class TCPPacket implements Comparable<TCPPacket>,Serializable {
	
	private static final long serialVersionUID = 7381917238742113081L;
	
	private int sequenceNumber;
	private TCPSegment segment;
	
	public TCPPacket(int sequenceNumber, TCPSegment segment) {
		this.sequenceNumber = sequenceNumber;
		this.segment = segment;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public TCPSegment getSegment() {
		return segment;
	}

	public void setSegment(TCPSegment segment) {
		this.segment = segment;
	}

	@Override
	public int compareTo(TCPPacket o) {
		return Integer.compare(this.sequenceNumber, o.sequenceNumber);
	}

	


}
