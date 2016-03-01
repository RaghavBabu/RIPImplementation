package edu.rit.rip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class Router
 * Router which initiates client thread and server thread, initiates parsing for each Router,
 * prints final Routing table for every 1 second.
 * @author Raghav Babu
 *	@version 5-Oct-2015
 */

public class Router {

	public static String routerName = "B";
	public static List<String> routersList = new ArrayList<String>();
	public static List<LinkRouter> linkRouterList = new ArrayList<LinkRouter>();
	public static Map<String,String> routersIP = new HashMap<String,String>();
	public static Map<String,String> ipToRoutersMap = new HashMap<String, String>();

	public static void main(String[] args) {
		
		new TopologyXMLParser().parseXML(routerName, routersList, linkRouterList);
		IntermediateTable interimTable = new IntermediateTable();
		RoutingTable  routingTable= new RoutingTable(interimTable);
		
		interimTable.constructIntermediateTable();
		
		ServerThread server = new ServerThread();
		Thread sendThread = new  Thread(server);
		sendThread.start();

		ClientThread client = new ClientThread(interimTable);
		Thread listenThread = new  Thread(client);
		listenThread.start();
		
		while(true){
			
			routingTable.generateFinalRoutingTable();
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}

/* glados 129.21.22.196 
queeg: 129.21.30.37
comet: 129.21.34.80
rhea: 129.21.37.49*/
