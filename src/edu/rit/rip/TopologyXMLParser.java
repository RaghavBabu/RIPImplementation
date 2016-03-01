package edu.rit.rip;

import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * class TopologyXMLParser
 * Class parses the XML file and creates the topology related list and get topology,ip and subnet information.
 * @author Raghav Babu
 * @version 5-Oct-2015
 */
public class TopologyXMLParser {

	/**
	 * parse method DOM Parser
	 * @param routerName
	 * @param routersList
	 * @param list
	 */
	public void parseXML(String routerName, List<String> routersList, List<LinkRouter> list){

		try {	
			File inputFile = new File("topology.xml");
			DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(inputFile);

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList routerList = doc.getElementsByTagName("Router");

			for (int i = 0; i < routerList.getLength(); i++) {

				Node nNode = routerList.item(i);
				//System.out.println("\nRouter Name : " + nNode.getNodeName());
				
				String name = null;
				String ipAddress = null;
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					name = eElement.getAttribute("Name");
					ipAddress = eElement.getAttribute("IPAddress");
					routersList.add(name);
					
					Router.routersIP.put(name,ipAddress);
					Router.ipToRoutersMap.put(ipAddress.split("/")[0], name);
					//System.out.println("Name : "  + eElement.getAttribute("Name"));
				}

				if(name.equals(routerName)) {

					NodeList linkList = nNode.getChildNodes();
					
					for (int j = 0; j < linkList.getLength(); j++) {

						LinkRouter linkRouter = new LinkRouter();
						Node linkNode = linkList.item(j);

						if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
							Element linkElement = (Element) linkNode;
							
							linkRouter.setRouterName(linkElement.getAttribute("Name"));
							linkRouter.setCost(Integer.parseInt( linkElement.getAttribute("cost")));
							list.add(linkRouter);
							
							//System.out.println("Link Name : "  + linkElement.getAttribute("Name"));
							//System.out.println("Cost : "  + linkElement.getAttribute("cost"));
						}
					}

				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

