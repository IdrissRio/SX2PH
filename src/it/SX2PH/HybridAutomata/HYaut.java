package it.SX2PH.HybridAutomata;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.SX2PH.xmlreader.Parser;

public abstract class HYaut {
	public static String toolPrefix ="SX2PH";
	
	public static String checkSystemKeyword(String string, String place) {
		switch(string.trim()) {
		case "wait":
		case "do":
		case "goto":
		case "while":
		case "sync":
		case "automaton":
			System.err.println("The "+place+" named '"+string+"' is a reserved keyword for PhavER. It's replaced by:"+HYaut.toolPrefix+"_"+string );
			return HYaut.toolPrefix+"_"+string;
		default:
			return string;
		}
	}
	
	
	public static  String getLocationNameFromId(String id, String compId) {
		NodeList nList = Parser.getDocument().getElementsByTagName("component");
		for (int i=0; i<nList.getLength();++i) {
			Node nNode = nList.item(i);
			if(nNode.getNodeType()==Node.ELEMENT_NODE && nNode.getAttributes().getNamedItem("id").getNodeValue().equals(compId)) {
				NodeList cList = nNode.getChildNodes();
				for (int j=0; j<cList.getLength(); ++j) {
					Node cNode = cList.item(j);
					if (cNode.getNodeName().equals("location") && cNode.getAttributes().getNamedItem("id").getTextContent().equals(id)) {
						return cNode.getAttributes().getNamedItem("name").getTextContent();
					}
				}
			}
		}
		return null;
	}
	
	protected abstract void setInitially();
	public abstract void parseTransitions();
	public abstract void parseLocations();
	public abstract String getInitialState();
	public abstract ArrayList<Location> getLocations();
	public abstract ArrayList<Transition> getTransitions();
	public abstract String getId();
	public abstract ArrayList<Component> getComponents();
	public abstract String getAs();
	
	
}
