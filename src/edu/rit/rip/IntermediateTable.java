package edu.rit.rip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class IntermediateTable
 * This class maintains intermediate table with all shortest path costs to reach 
 * all routers from the current router intermediate table.
 * @author Raghav Babu
 * @version 5-Oct-2015
 */
public class IntermediateTable implements Serializable {

	private static final long serialVersionUID = 6358693191595990200L;
	public static String[][] localRouterIntermediateTable = new String[][] { {Router.routerName, "A", "B", "C", "D"}, 
		{"A", "0", "0", "0", "0"},
		{"B", "0", "0", "0", "0"},
		{"C", "0", "0", "0", "0"},
		{"D", "0", "0", "0", "0"} };

	/**
	 * construct  initial Intermediate Table
	 */
	public void constructIntermediateTable(){
		createInitialTable(localRouterIntermediateTable);
	}

	/**
	 * This method creates and fill initial table.
	 * Removing all unrequired rows,columns and non-neighbour nodes row column from local table.
	 */
	public void createInitialTable(String[][] localRouterIntermediateTable) {


		for(int i = 1 ; i < localRouterIntermediateTable.length; i++){

			for(int j = 1 ; j < localRouterIntermediateTable.length; j++){

				//filling row with X. 
				if(localRouterIntermediateTable[i][0] == localRouterIntermediateTable[0][0]){
					localRouterIntermediateTable[i][j] = "-";
				}

				//filling column with X. 
				if(localRouterIntermediateTable[0][j] == localRouterIntermediateTable[0][0]){
					localRouterIntermediateTable[i][j] = "-";
				}
			}
		}	

		//filling column(non-neighbor) with X. 

		List<String> notLinkedRouters = ifNotDirectNeighbour();

		for(String s : notLinkedRouters){

			for(int j = 1 ; j < localRouterIntermediateTable.length; j++){

				if(localRouterIntermediateTable[0][j].equals(s) ){
					int i = 1;
					while(i < localRouterIntermediateTable.length){
						localRouterIntermediateTable[i][j] = "-";
						i++;
					}
				}

			}
		}

		//printTable(table);
	}

	/**
	 * Method to get not directly connected neighbours.
	 * (i.e) list of nodes that are not directly connected.
	 * @return List<String> non neighbour nodes.
	 */
	private List<String> ifNotDirectNeighbour() {

		List<String> reqdList = new ArrayList<String>();
		reqdList.addAll(Router.routersList);

		List<String> lrList = new LinkedList<String>();

		for(LinkRouter lr : Router.linkRouterList)
			lrList.add(lr.getRouterName());

		reqdList.removeAll(lrList);

		return reqdList;
	}

	/**
	 * process table from remote router and update our local router intermediate table.
	 * duplicateTable since I needed value from old local table.
	 * @param remoteInterimTable
	 * @return
	 */
	public String[][] processRemoteAndUpdateLocalInterimTable(String[][] remoteInterimTable) {

		String[][] duplicateTable = localRouterIntermediateTable;

		String tableFromRouter = remoteInterimTable[0][0];
		String destRouter = null;


		for(int i = 1; i < remoteInterimTable.length; i++){

			int minCost = getLowCostFromRow(remoteInterimTable, i);

			if(minCost > 0 && minCost != 0){
				destRouter = remoteInterimTable[i][0];
				duplicateTable = updateLocalInterimTable(duplicateTable, tableFromRouter, destRouter, minCost);
			}
		}
		return duplicateTable;
	}

	/**
	 * update the corresponding rowXcolumn with the minCost value + cost to reach that Remote Router from actual table.
	 * @param duplicateTable
	 * @param tableFromRouter
	 * @param destRouter
	 * @param minCost
	 * @return String[][]
	 */

	private synchronized String[][] updateLocalInterimTable( String[][] duplicateTable, String tableFromRouter, String destRouter, int minCost) {

		// System.out.println( tableFromRouter + " "+ destRouter+ " "+ minCost);
		int reqdCol = 0;
		int reqdRow = 0;

		// to find the row of dest router.
		int row = 0;
		for(int col = 1; col < duplicateTable.length; col++ ){

			if(duplicateTable[row][col].equals(tableFromRouter)){
				reqdCol = col;
				break;
			}
		}

		//to find column of through Router.
		int col = 0;
		for(int r = 1; r < duplicateTable.length; r++ ){

			if(duplicateTable[r][col].equals(destRouter)){
				reqdRow = r;
				break;
			}
		}
		//System.out.println(reqdRow + " "+ reqdCol);
		//if from router to destRouter and via tableFromRouter in table

		if( !(duplicateTable[reqdRow][reqdCol].equals("-"))){

			int cost = Integer.parseInt(localRouterIntermediateTable[reqdRow][reqdCol]);

			if( (cost == 0 || cost > minCost) && minCost != 0) {

				int remoteCost = getMinCostToReachRemoteRouter(reqdCol, tableFromRouter);

				int newCost = remoteCost + minCost;
				duplicateTable[reqdRow][reqdCol] = String.valueOf(newCost);
			}

		}
		return duplicateTable;

	}

	// get cost to reach the Remote Router to compute total cost through it.
	/*private static int getMinCostToReachRemoteRouter(String toRouter) {

		int col = 0;

		for(int row = 1; row < localRouterIntermediateTable.length; row++){

			if(localRouterIntermediateTable[row][col].equals(toRouter)) {
				return getLowCostFromRow(localRouterIntermediateTable, row);
			}
		}
		return 0;
	}*/

	/**
	 * get min cost to reach Remote Router in local table .
	 * @param reqdCol
	 * @param tableFromRouter
	 * @return
	 */
	private int getMinCostToReachRemoteRouter(int reqdCol, String tableFromRouter) {

		int col = 0;

		for(int row = 1; row < localRouterIntermediateTable.length; row++){

			if(localRouterIntermediateTable[row][col].equals(tableFromRouter)) {
				return Integer.parseInt(localRouterIntermediateTable[row][reqdCol] );
			}
		}
		return 0;
	}

	/**
	 * compute lowest cost to reach the dest node(row).
	 * @param table
	 * @param row
	 * @return
	 */
	private synchronized int getLowCostFromRow(String[][] table, int row) {

		int minCost = Integer.MAX_VALUE;

		for(int col = 1;col < table.length; col++){

			if( !(table[row][col].equals("-"))){

				int cost = Integer.parseInt(table[row][col]) ;

				if(cost < minCost && cost != 0)
					minCost = cost;

			}
		}

		if(minCost == Integer.MAX_VALUE)
			minCost = 0;

		//System.out.println("Min cost in row "+row+ " --> "+minCost);
		return minCost;
	}

	/**
	 * perform first iteration process in table.
	 * 
	 */
	public void performIntermediateTableFirstIterationProcess() {


		//fill cost for direct links from local router.
		for(LinkRouter lr : Router.linkRouterList){

			for(int i = 1 ; i < localRouterIntermediateTable.length; i++){
				for(int j = 1 ; j < localRouterIntermediateTable.length; j++){

					if(localRouterIntermediateTable[i][0].equals(lr.getRouterName()) && localRouterIntermediateTable[0][j].equals(lr.getRouterName()))
						localRouterIntermediateTable[i][j] = String.valueOf(lr.getCost());
				}
			}
		}

	}
	
	/**
	 * Method gets a next hop route info from table.
	 * @param Router
	 * @param ripObject
	 * @return
	 */
	public RIPObject getNextHopRouterFromInterimTable(String Router, RIPObject ripObject) {

		String[][] table = localRouterIntermediateTable;

		int col = 0;
		for(int r = 1; r < table.length; r++ ){

			if(table[r][col].equals(Router)){
				getLowCostCorrespondingCol(table, r, ripObject);
				break;
			}
		}
		return ripObject;
	}
	
	/**
	 * get lowest cost in corresponding column of table.
	 * @param table
	 * @param row
	 * @param ripObject
	 * @return
	 */
	private  RIPObject getLowCostCorrespondingCol(String[][] table, int row, RIPObject ripObject) {

		int reqdCol = 0;
		int minCost = Integer.MAX_VALUE;

		for(int col = 1;col < table.length; col++){

			if( !(table[row][col].equals("-"))){

				int cost = Integer.parseInt(table[row][col]) ;

				if(cost < minCost && cost != 0){
					minCost = cost;
					reqdCol = col;
				}

			}
		}
		ripObject.setHopCount(minCost);
		ripObject.setNexthopRouter(table[0][reqdCol]);

		if(minCost == Integer.MAX_VALUE){
			minCost = 0;
			ripObject.setHopCount(minCost);
			ripObject.setNexthopRouter(ripObject.getRouterName());
		}

		//System.out.println("Min cost in row "+row+ " --> "+minCost);
		return ripObject;
	}
	
	/**
	 * print the intermediate table.
	 * @param table
	 */
	public void printIntermediateTable(String[][] table){

		String str = " ";

		for(int i = 0; i < table.length;i++){
			for(int j = 0;j < table.length;j++){
				str += table[i][j] + "\t";
			}

			System.out.println(str );
			str = " ";
		}
	}
}


