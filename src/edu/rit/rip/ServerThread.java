package edu.rit.rip;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class ServerThread
 * This class runs as a thread and sends the routing table to all link routers 
 * whenever it get requests from client thread for input.
 * @author Raghav Babu
 * @version 5-Oct-2015
 *
 */
public class ServerThread implements Runnable {

	private InetSocketAddress boundPort = null;
	private final int port = 1234;
	private ServerSocket serverSocket;
	
	
	public void run() {

		try {
			
			initServerSocket();
			String inputString;

			while(true) {
				
				Socket connectionSocket = serverSocket.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

				if(br != null ) {
					
					inputString = br.readLine();
					//System.out.println(" Server Thread  - From Router : " + inputString);
					OutputStream oStream = connectionSocket.getOutputStream();
	                ObjectOutputStream ooStream = new ObjectOutputStream(oStream);
	                ooStream.writeObject(IntermediateTable.localRouterIntermediateTable);  
	                ooStream.close();
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * method which initialized and bounds a server socket to a port.
	 * @return void.
	 */
	 private void initServerSocket()
	    {
	        boundPort = new InetSocketAddress(port);
	        try
	        {
	        	serverSocket = new ServerSocket(port);
	            
	        	if (serverSocket.isBound())
	            {
	                System.out.println("Router Sever bound to data port " + serverSocket.getLocalPort() + " and is ready...");
	            }
	        }
	        catch (Exception e)
	        {
	            System.out.println("Unable to initiate socket.");
	        }
	        
	    }
}


