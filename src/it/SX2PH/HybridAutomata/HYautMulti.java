package it.SX2PH.HybridAutomata;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.SX2PH.HybridAutomata.Parameter.Dynamic;
import it.SX2PH.HybridAutomata.Parameter.Type;
import it.SX2PH.application.Application;
import it.SX2PH.exception.SX2PHValidateException;
import it.SX2PH.xmlreader.Parser;

public class HYautMulti extends HYaut{
	private ArrayList<Location> locations = new ArrayList<Location>();
	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	private Component comp;
	private String id= new String();
	private String as= new String();
	private Boolean emptyLabel=true;
	private Document doc;
	private String initialState="";
	public HYautMulti(String id, String as) {
		this.doc=Parser.getDocument();
		this.id=id;
		this.comp=new Component(id, as);
		this.as=as;
		setLocationAndTransition();
		bindValues();
		setInitially();
		parseLocations();
		parseTransitions();
		try {
			validate();
		} catch (SX2PHValidateException e) {
			e.printStackTrace();
		}
	}


	private void validate() throws SX2PHValidateException {
		for(Transition t : transitions) {
			Boolean findTarget = false;
			Boolean findSource = false;
			for(Location l: locations) {
				if(t.getSoruce().equals(l.getName()))
					findSource = true;
				if(t.getTarget().equals(l.getName()))
					findTarget = true;
			}
			if(!(findTarget && findSource)) {
				throw new SX2PHValidateException("Validation Error: tansitions name and location are not the same");
			}
			findTarget=false;
			findSource=false;
		}
	}

	private void setLocationAndTransition() {
		NodeList nList = doc.getElementsByTagName("component");
		for (int i=0; i<nList.getLength();++i) {
			Node nNode = nList.item(i);
			if(nNode.getNodeType()==Node.ELEMENT_NODE && nNode.getAttributes().getNamedItem("id").getNodeValue().equals(this.id)) {
				NodeList cList = nNode.getChildNodes();
				for (int j=0; j<cList.getLength(); ++j) {
					Node cNode = cList.item(j);
					if (cNode.getNodeName().equals("location")) {
						String id = HYaut.getLocationNameFromId(cNode.getAttributes().getNamedItem("id").getTextContent(),nNode.getAttributes().getNamedItem("id").getTextContent());
						String name = HYaut.checkSystemKeyword(cNode.getAttributes().getNamedItem("name").getTextContent(), "location");
						Element eElement = (Element) cNode;
						String invariant="true";
						if(eElement.getElementsByTagName("invariant").item(0)!=null)
							invariant = eElement.getElementsByTagName("invariant").item(0).getTextContent().replace("&&", "&");
						String flow="";
						if(eElement.getElementsByTagName("flow").item(0)!=null)
							flow = eElement.getElementsByTagName("flow").item(0).getTextContent().replace("&&", "&");
						if(flow.trim().replace("\n", "").equals(""))
							flow="true";
						addLocations(new Location(id,name,invariant,flow));
					}else if(cNode.getNodeName().equals("transition")) {
						Integer nSource = Integer.parseInt(cNode.getAttributes().getNamedItem("source").getTextContent());
						Integer nTarget = Integer.parseInt(cNode.getAttributes().getNamedItem("target").getTextContent());
						String source =
								HYaut.checkSystemKeyword(HYaut.getLocationNameFromId(cNode.getAttributes().getNamedItem("source").getTextContent(), nNode.getAttributes().getNamedItem("id").getTextContent()), "location");
						String target =
								HYaut.checkSystemKeyword(HYaut.getLocationNameFromId(cNode.getAttributes().getNamedItem("target").getTextContent(), nNode.getAttributes().getNamedItem("id").getTextContent()), "location");
						Element eElement = (Element) cNode;
						String label="";
						if(eElement.getElementsByTagName("label").item(0)!=null)
							label = HYaut.checkSystemKeyword(eElement.getElementsByTagName("label").item(0).getTextContent(),"label");
						if(label.equals("")) {
							label=this.getAs()+"_TemporaryLabel";
							if(this.emptyLabel) {
								comp.addParameter(new Parameter(label, Type.LABEL, true, Dynamic.NOTHING, true,as+"_") );
								emptyLabel=false;
							}
						}
						String guard="true";
						if(eElement.getElementsByTagName("guard").item(0)!=null)
							guard = eElement.getElementsByTagName("guard").item(0).getTextContent().replace("&&","&");
						String assignment=null;
						if(eElement.getElementsByTagName("assignment").item(0)!=null && !eElement.getElementsByTagName("assignment").item(0).getTextContent().trim().replace("\n", "").equals("") )
							assignment = eElement.getElementsByTagName("assignment").item(0).getTextContent().replace("&&","&");
						addTransitions(new Transition(source,target,label,guard,assignment,nSource, nTarget));
					}
				}	
			}
		}
	}

	private void bindValues() {
		NodeList nList = doc.getElementsByTagName("bind");
		for (int i=0; i<nList.getLength();++i) {
			Node nNode = nList.item(i);
			if(nNode.getNodeType()==Node.ELEMENT_NODE && nNode.getAttributes().getNamedItem("as").getNodeValue().equals(as)) {
				for(int j=0; j< nNode.getChildNodes().getLength();++j) {
					Node paramNode = nNode.getChildNodes().item(j);			
					String paramName=null;  
					try {
						paramName=paramNode.getAttributes().getNamedItem("key").getNodeValue();
						Double value = Double.parseDouble(paramNode.getTextContent());

						comp.setParameterValue(paramName, value);
					}catch(Throwable t) {
						for(Parameter p: comp.getParameters()) {
							if(p.getName().equals(paramName) && p.isConstant()){
								System.err.println("The constant '" +paramName +"' has not a bind value. I'm trying to search in the .CFG inital state.");
								Integer nameIndex = Parser.getConfig().getInitalState().indexOf(paramName);
								Integer ampIndex = Parser.getConfig().getInitalState().indexOf("&", nameIndex)==-1?
										Parser.getConfig().getInitalState().indexOf(";", nameIndex)
										:Parser.getConfig().getInitalState().indexOf("&", nameIndex);
										Integer eqIndex = Parser.getConfig().getInitalState().indexOf("==", nameIndex);
										if(nameIndex==-1 || eqIndex==-1) {
											System.err.println("The constant: "+ paramName + " has not a float value in the .CFG initial state. I will put null.");
											return;
										}
										try {
											String tmp = Parser.getConfig().getInitalState().substring(eqIndex+2, ampIndex);
											Double value = Double.parseDouble(tmp);
											comp.setParameterValue(paramName, value);
										}catch(Throwable q) {
											return;
										}
							}
						}
					}
				}
			}
		}
	}

	private void addTransitions(Transition trans) {
		transitions.add(trans);
	}

	private void addLocations(Location loc) {
		locations.add(loc);
	}

	public ArrayList<Component> getComponents() {
		ArrayList<Component> tmp = new ArrayList<Component>();
		tmp.add(comp);
		return tmp;
	}

	public Boolean isSystemComponent() {
		return comp.isSystem();
	}


	public ArrayList<Location> getLocations() {
		return locations;
	}


	public ArrayList<Transition> getTransitions() {
		return transitions;
	}

	public String getId() {
		return id;
	}

	public String getAs() {
		return as;
	}


	@Override
	public void setInitially() {
		if(Application.noBindFound) {
			setInitialState(Parser.getConfig().getInitalState().replace(".", "_").replace("(","").replace(")",""));
			return;
		}

		String initially = Parser.getConfig().getInitalState();
		initially = initially.replace("(", "");
		initially = initially.replace(")", "");
		initially = parseInitially(initially);
		if(!initially.trim().isEmpty()) initially+=";";
		initially = initially.replace(comp.getIdentifier()+".", "");
		initially = initially.replace(comp.getAs() +" ==", "");
		initially = initially.replace(comp.getAs()+".", "");
		initially = initially.replace("& ;", "");
		if(initially.trim().isEmpty())
			initially+="true";
		else if(!initially.contains("=="))
			initially+="& true";
		setInitialState(initially);
	}

	private void setInitialState(String initialState) {
		this.initialState=checkForInputVariablesInInitally(initialState);
	}

	@Override
	public void parseTransitions() {
		for(Transition t: getTransitions()) {
			t.setGuard(t.getGuard().replace("\n",""));
			t.setLabel(t.getLabel().replace("\n",""));
			if(t.getAssignment()!=null) {
				String assignment=t.getAssignment();
				if(t.getAssignment().startsWith(" "))
					assignment=t.getAssignment().replaceFirst(" ", "");
			
				if(!t.getAssignment().contains("=="))
					assignment=t.getAssignment().replace("=", "' ==");
				assignment=assignment.replace("<' =="," <=");
				assignment=assignment.replace(">' =="," >=");
				
				//Se ci sono occorrenze di loc.var==val, cambiamo loc.var in loc_var
				//assignment=assignment.replace(".", "_"); 
				assignment=assignment.replace(" '", "'");
				assignment=assignment.replace("  "," ");
				assignment= assignment.replaceAll(":", "");
				assignment=assignment.replace("\n", "");
				if(assignment.contains("& "))
					assignment=assignment.replace("& ", " &\n");
				else 
					assignment=assignment.replace("&", " &\n");
				assignment+=" ";
				t.setAssignment(assignment);
				addSpace(t.getAssignment());
			}
			t.setGuard(addSpace(t.getGuard()));
			t.setGuard(t.getGuard()+" ");
			addSpace(t.getGuard());

			//Qui gestisco le variabili delle transizioni
			for(Parameter p : comp.getParameters()) {
				if(p.getType()!=Type.LABEL && !p.isInput()) {
					if(p.getLocal())
						t.setGuard(t.getGuard().replace(p.getName()+" ",p.getPrefix()+p.getName()+ " "));
					if(t.getAssignment()!=null && p.getLocal()) {
						t.setAssignment(t.getAssignment().replace(p.getName()+ " ",p.getPrefix()+p.getName()+" "));
						t.setAssignment(t.getAssignment().replace(p.getName()+ "' ",p.getPrefix()+p.getName()+"' "));

					}
				}
			}
			if(t.getAssignment()!=null)
			t.setAssignment(t.getAssignment().replace(" ", ""));
		}
	}

	private String addSpace(String x){
		x=x.replace("+", " + ");
		x=x.replace("-", " - ");
		x=x.replace("*", " * ");
		x=x.replace("/", " / ");
		x=x.replace("'", "' ");
		x=x.replace(";", " ;");
		x=x.replace("==", " == ");
		x=x.replace("\n", " \n");
		x=x.replace("&", " & ");
		x=x.replace("<=", " <= ");
		x=x.replace(">=", " >= ");
		if(!x.contains("<="))x=x.replace("<", " < ");
		if(!x.contains(">="))x=x.replace(">"," > ");
		
		return x;
	}


	@Override
	public void parseLocations() {
		for(Location l: getLocations()) {
			if(l.getFlow()!=null) {
				//Questo è molto poco manutenibile
				l.setInvariant(l.getInvariant().replace("\n",""));
				String line=l.getFlow();
				//Se ci sono occorrenze di loc.var==val, cambiamo loc.var in loc_var
				//line=line.replace(".","_");
				if(line.trim().equals("false")) {
					line="";
					for(Parameter p : comp.getParameters()){
						if(p.isControlled())
							line+=p.getName()+"' == 0 & \n";
					}
					line=line.substring(0,line.lastIndexOf("&")-1);
				}
				line=line.replace("\n","");
				if(line.contains("& "))
					line=line.replace("& ", " &\n");
				else 
					line=line.replace("&", " &\n");
				line+=" ";
				addSpace(line);
				l.setFlow(line);
			}
			l.setInvariant(l.getInvariant()+" ");
			l.setInvariant(addSpace(l.getInvariant()));
			if(l.getFlow()!=null)
				l.setFlow(addSpace(l.getFlow()));
			
			for(Parameter p : comp.getParameters()) {
				if(p.getType()!=Type.LABEL  && !p.isInput()) {
					if(p.getLocal())
						l.setInvariant(l.getInvariant().replace(p.getName()+" ",p.getPrefix()+p.getName()+" "));
					if(l.getFlow()!=null && p.getLocal()) {
						l.setFlow(l.getFlow().replace(p.getName()+ " ",p.getPrefix()+p.getName()+ " "));
						l.setFlow(l.getFlow().replace(p.getName()+ "' ",p.getPrefix()+p.getName()+ "' "));
					}
				}
			}
			if(l.getFlow()!=null)
				l.setFlow(l.getFlow().replace(" ", ""));

		}
	}


	private String parseInitially(String init) {
		String outputString="";
		String initSub="";
		Integer firstOccurenceAmp = init.indexOf("&");
		Boolean amp = true;
		if (firstOccurenceAmp!=-1)
			initSub= init.substring(0, firstOccurenceAmp);
		else
			initSub=init;
		if(initSub.contains(getAs())){
			if(initSub.contains(getAs()+".")) {
				String varName=initSub.substring(initSub.indexOf('.')+1,initSub.indexOf('='));
				varName=varName.trim();
				for(Parameter p: comp.getParameters()) {
					if(p.getName().equals(varName))
						if(p.getLocal())
							initSub=initSub.replace(getAs()+".", getAs()+"_");
						else
							initSub=initSub.replace(getAs()+".","");
				}
				
				outputString=initSub;
			}else
				if(initSub.contains(getAs()+" ") || initSub.contains(getAs()+"=")){
					initSub=initSub.replace(getAs() +" ==", "");
					initSub=initSub.replace(getAs() +"==", "");
					outputString+=initSub;
				}else {
					//outputString+=initSub;
					amp=false;
				}
			if(amp)
				outputString+=" & ";
		}
		if (firstOccurenceAmp!=-1)
			return outputString+ parseInitially(init.substring(initSub.length()+1,init.length()));
		else
			return outputString;
	}

	
	public String checkForInputVariablesInInitally(String OutString) {
		for(Parameter p : comp.getParameters()) {
			if(!p.isInput())continue;
			//if(!p.getLocal())
			OutString=OutString.replace(p.getPrefix()+p.getName(), p.getName());
		}
		return OutString;
	}
	
	@Override
	public String getInitialState() {
		return this.initialState;
	}
}
