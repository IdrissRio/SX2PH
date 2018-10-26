package it.SX2PH.HybridAutomata;

public class Location {
	private String identifier;
	private String name;
	private String invariant;
	private String flow;
	
	
	public Location(String identifier, String name, String invariant, String flow) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.invariant = invariant;
		this.flow = flow;
	}


	public String getIdentifier() {
		return identifier;
	}


	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getInvariant() {
		return invariant;
	}


	public void setInvariant(String invariant) {
		this.invariant = invariant;
	}


	public String getFlow() {
		return flow;
	}


	public void setFlow(String flow) {
		this.flow = flow;
	}
}
