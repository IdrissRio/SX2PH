package it.SX2PH.HybridAutomata;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.SX2PH.HybridAutomata.Parameter.Dynamic;
import it.SX2PH.HybridAutomata.Parameter.Type;
import it.SX2PH.xmlreader.Parser;

public class Component {
	private String identifier;
	private ArrayList<Parameter> parameters;
	private Boolean system;
	private String as;
	
	
	public Component(String identifier,String as){
		this.as=as;
		this.identifier = identifier;
		parameters = new ArrayList<Parameter>();
		system = false;
		Document doc = Parser.getDocument();
		NodeList nList = doc.getElementsByTagName("component");
		for (int i=0; i<nList.getLength();++i) {
			Node nNode = nList.item(i);
			if(nNode.getNodeType()==Node.ELEMENT_NODE && nNode.getAttributes().getNamedItem("id").getNodeValue().equals(identifier)) {
				for (int j=0; j<nNode.getChildNodes().getLength(); ++j) {
					try {
					Node paramNode=nNode.getChildNodes().item(j);
					if(!paramNode.getNodeName().equals("param"))
						continue;
					Type type = paramNode.getAttributes().getNamedItem("type").getNodeValue().equals("real") 
								? Type.REAL :Type.LABEL;
					String name="";
					name+= HYaut.checkSystemKeyword(paramNode.getAttributes().getNamedItem("name").getNodeValue().replace(".", "_"),"label");
					Boolean local = paramNode.getAttributes().getNamedItem("local").getNodeValue().equals("true");
					Dynamic dynamics = Dynamic.NOTHING;
					if( null != paramNode.getAttributes().getNamedItem("dynamics")) 
						dynamics  = paramNode.getAttributes().getNamedItem("dynamics").getNodeValue().equals("any") 
									? Dynamic.ANY :Dynamic.CONSTANT;
					Boolean controlled=true;
					if( null != paramNode.getAttributes().getNamedItem("controlled")) {
						controlled=paramNode.getAttributes().getNamedItem("controlled").getNodeValue().equals("true");
					}
					parameters.add( new Parameter(name, type,local, dynamics, controlled, as+"_"));
					
			    }catch(NullPointerException t) {
			    	
			    }
				}
			}	
		}
		
	}
	public void setIsSystem(Boolean b) {
		this.system=b;
	}
	public Boolean isSystem() {
		return system;
	}
	
	public void addParameter(Parameter par) {
		parameters.add(par);
	}
	
	public void removeParameter(Parameter par) {
		parameters.remove(par);
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	
	public void setParameterValue(String name, Double value) {
		for(Parameter p: parameters) {
			if(p.getName().equals(name))
				p.setValue(value);
		}
	}
	
	public String getAs() {
		return as;
	}
	
	public void setAs(String as) {
		this.as=as;
	}
	
}
