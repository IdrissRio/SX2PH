package it.SX2PH.HybridAutomata;

public class Parameter implements Comparable<Parameter> {
	private String name;
	private Type type;
	private Boolean local;
	private Dynamic dynamics;
	private Boolean controlled;
	private Double value;
	private String prefix;
	
	public enum Type{
		REAL, LABEL;
	}

	public enum Dynamic{
		NOTHING,ANY, CONSTANT;
	}
		
	public Parameter(String name, Type type, Boolean local, Dynamic dynamics, Boolean controlled, String prefix) {
		super();
		this.name = name;
		this.type = type;
		this.local = local;
		this.dynamics = dynamics;
		this.controlled = controlled;
		this.prefix=prefix;
		
	}
	

	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Boolean getLocal() {
		return local;
	}

	public void setLocal(Boolean local) {
		this.local = local;
	}

	public Dynamic getDynamics() {
		return dynamics;
	}
	
	public void setDynamics(Dynamic dynamics) {
		this.dynamics = dynamics;
	}

	public Boolean isControlled() {
		return controlled && type==Type.REAL;
	}

	public void setControlled(Boolean controlled) {
		this.controlled = controlled;
	}
	
	public Boolean isConstant() {
		return dynamics.equals(Dynamic.CONSTANT);
	}
	
	public Boolean isInput() {
		return !isControlled() && getType()!=Type.LABEL &&
				!isConstant();
	}
	
    @Override
    public int compareTo(Parameter o) {
    	if(name.contains(o.name))return -1;
    	if(o.name.contains(name))return 1;
    	return 0;

    }

    @Override
    public String toString() {
        return this.name;
    }
	
	public Double getValue() {
		return this.value;
	}
	
	public void setValue(Double value) {
		this.value=value;
	}

}
