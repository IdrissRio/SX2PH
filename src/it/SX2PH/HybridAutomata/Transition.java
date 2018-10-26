package it.SX2PH.HybridAutomata;

public class Transition {
	private String source;
	private String target;
	private String label;
	private String guard;
	private String assignment;
	private Integer nSource;
	private Integer nTarget;
	
	public Transition(String source, String target, String label,
			String guard, String assignment, Integer nSource, Integer nTarget){
		this.source= source;
		this.target=target;
		this.label=label;
		this.guard=guard;
		this.assignment=assignment;
		this.nSource=nSource;
		this.nTarget=nTarget;
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

	public void setAssignment(String assignment) {
		this.assignment = assignment;
	}

	public String getSoruce() {
		return source;
	}
	
	
	public String getTarget() {
		return target;
	}
	
	
	public String getLabel() {
		return label;
	}
	
	
	public String getGuard() {
		return guard;
	}
	
	public String getAssignment() {
		return assignment;
	}
	
	public Integer getNSource() {
		return nSource;
	}
	
	public Integer getNTarget() {
		return nTarget;
	}
	
	
	
}
