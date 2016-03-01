package edu.rit.rip;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Class ClientThread
 * This class sends request to neighbouring routers to get their tables
 *  and update the local routing table with the shortest path values to all networks.
 * @author Raghav Babu
 * @version 5-Oct-2015
 */
public class ClientThread implements Runnable {

	private IntermediateTable interimTable;

	public ClientThread(IntermediateTable interimTable){
		this.interimTable = interimTable;
	}

	public void run(){

		try {
			String sendString;
			String[][] remoteInterimTable = null;
			Thread.currentThread().sleep(5000);
			String[][] duplicateTable = null;
			int count = 0;

			while(true){

				if(count == 0){
					interimTable.performIntermediateTableFirstIterationProcess();
					count++;
					continue;
				}
				else {
					//System.out.println("--------------------------------Iteration "+(count)+"--------------------------------");

					Socket clientSocket = null;

					for(LinkRouter linkedRouter : Router.linkRouterList){

						String[] ip = Router.routersIP.get(linkedRouter.getRouterName()).split("/");

						while(true){

							try{
								clientSocket = new Socket(ip[0], 1234);						
								DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
								sendString = Router.routerName +" to "+linkedRouter.getRouterName() ;
								dos.writeBytes(sendString + '\n');
							}catch (Exception e){
								System.out.println("Couldn't connect to Router : "+ip[0]+", so trying again to connect!!!! ");
								Thread.currentThread().sleep(5000);
								continue;
							}
							break;
						}

						InputStream inputStream = clientSocket.getInputStream();

						if(inputStream != null){

							ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
							remoteInterimTable = (String[][])objectInputStream.readObject();
						}

						/*System.out.println("----------Client Thread Printing Remote Table---------------------");
						interimTable.printIntermediateTable(remoteInterimTable);*/

						//first iteration alone just fill the table with direct link value.
						duplicateTable = interimTable.processRemoteAndUpdateLocalInterimTable(remoteInterimTable);

						clientSocket.close();
					}

					IntermediateTable.localRouterIntermediateTable = duplicateTable;

					/*System.out.println("------------Client Thread Printing  Local Table --------------------");
					interimTable.printIntermediateTable(IntermediateTable.localRouterIntermediateTable);
					System.out.println("----------------------------------------------------------------------------");*/

				}
				count++;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
