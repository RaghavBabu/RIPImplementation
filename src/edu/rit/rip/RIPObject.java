package edu.rit.rip;

import java.io.Serializable;

/**
 * Class RIPObject
 * Class holds information of each RIP object.
 * @author Raghav Babu
 * @version 5-Oct-2015
 */
public class RIPObject implements Serializable {

	private static final long serialVersionUID = -3792460402228241014L;

	String routerName;
	String networkAddress;
	String subnetMask;
	String nexthopRouter;
	int hopCount;

	public RIPObject() {
		
	}
		
	public String getNetworkAddress() {
		return networkAddress;
	}
	public void setNetworkAddress(String destinationIPAddress) {
		this.networkAddress = destinationIPAddress;
	}
	public String getSubnetMask() {
		return subnetMask;
	}
	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}
	public String getNexthopRouter() {
		return nexthopRouter;
	}
	public void setNexthopRouter(String nexthopRouter) {
		this.nexthopRouter = nexthopRouter;
	}
	public int getHopCount() {
		return hopCount;
	}
	public void setHopCount(int hopCount) {
		this.hopCount = hopCount;
	}

	public String getRouterName() {
		return routerName;
	}

	public void setRouterName(String routerName) {
		this.routerName = routerName;
	}
}
