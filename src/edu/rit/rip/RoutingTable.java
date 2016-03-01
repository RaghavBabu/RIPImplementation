package edu.rit.rip;

import java.util.ArrayList;
import java.util.List;

/**
 * class RoutingTable
 * This class manages the final routing table for each router.
 * @author Raghav Babu
 *	@version 5-Oct-2015
 */
public class RoutingTable {
	
	private IntermediateTable interimTable;
	
	public RoutingTable(IntermediateTable interimTable) {
		this.interimTable = interimTable;
	}

	public void generateFinalRoutingTable() {
			
		List<RIPObject> list = new ArrayList<RIPObject>(Router.routersList.size());
		System.out.println("------------------------------- ROUTING TABLE ------------------------------------------------------");
		
		
		for(String router : Router.routersList){
			RIPObject obj = new RIPObject();
			obj.setRouterName(router);
			String[] ip = Router.routersIP.get(router).split("/");
			obj.setNetworkAddress(ip[0]);
			obj.setSubnetMask(ip[1]);
		    obj = interimTable.getNextHopRouterFromInterimTable(router,obj);
		    list.add(obj);
		}
		printRoutingTable(list);
		//System.out.println("----------------------------------------------------------------------------------------------------------");
		 
	}

	/**
	 * Prints the final routing table for each router.
	 * @param list
	 */
	private void printRoutingTable(List<RIPObject> list) {
		
			System.out.printf( "%-30.30s %-30.30s %-30.30s %-30.30s%n", "Network Address","SubNet Mask", "Next Hop Router", "Cost");
			
			for(RIPObject obj : list){
			//System.out.println(obj.routerName+"\t\t"+obj.getNetworkAddress()+"\t\t"+obj.getNexthopRouter()+"\t"+obj.getHopCount());
			String networkAddress  = computeNetworkPrefixFromSubnetMask(obj.getNetworkAddress(),obj.getSubnetMask());
			String[] nextIp = Router.routersIP.get(obj.getNexthopRouter()).split("/");
			
			System.out.printf( "%-30.30s %-30.30s %-30.30s %-30.30s%n",networkAddress, obj.getSubnetMask(),
											nextIp[0], obj.getHopCount());
			}
	}

/**
 * computeNetwork prefix for each network.
 * @param networkAddress
 * @param subnetMask
 * @return
 */
	private String computeNetworkPrefixFromSubnetMask(String networkAddress, String subnetMask) {
		
		String[] ip = networkAddress.split("\\.");
		String[] subnet = subnetMask.split("\\.");
		StringBuilder str = new StringBuilder();
		
		for (int i = 0, j = 0; i < ip.length &&  j < subnet.length; i++, j++){
		    
			int ipVal = Integer.parseInt(ip[i]);
		    
		    int subnetVal = Integer.parseInt(subnet[j]);
		    
		    Integer res = ipVal & subnetVal;
		    
		    str.append(Integer.parseInt(Integer.toBinaryString(res), 2));
		    
		    if(i != ip.length - 1 && j != subnet.length -1)
		    	str.append(".");
		}
		return str.toString();
	}
}
