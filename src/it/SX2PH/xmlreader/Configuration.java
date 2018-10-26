package it.SX2PH.xmlreader;

/** Property reader from the .cfg file.
* 
* @author  Riouak Idriss
* @version 0.1
*/

public class Configuration

{	//Principal component. For every .xml file there exist a <component id=systemID> </component>
	public String systemID;
	
	public String initalState;
	public String forbiddenState;
	
	
	public String getSystemID() {
		return systemID;
	}
	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}
	
	public String getInitalState() {
		return initalState;
	}
	public void setInitalState(String initalState) {
		this.initalState = initalState;
	}
	public String getForbiddenState() {
		return forbiddenState;
	}
	public void setForbiddenState(String forbiddenState) {
		this.forbiddenState = forbiddenState;
	}
	
	
	
}
