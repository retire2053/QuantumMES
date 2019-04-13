package qmes.indicator.def;

public class RegionDef implements Base{
	
	public RegionDef() {}

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	private String state;
	
	private double lower;
	
	private double upper;
	
	private String type;
	
	private RegionDef parent;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getLower() {
		return lower;
	}

	public void setLower(double lower) {
		this.lower = lower;
	}

	public double getUpper() {
		return upper;
	}

	public void setUpper(double upper) {
		this.upper = upper;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public RegionDef getParent() {
		return parent;
	}

	public void setParent(RegionDef parent) {
		this.parent = parent;
	}
	

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("name="+name+";");
		sb.append("state="+state+";");
		sb.append("upper="+upper+";");
		sb.append("lower="+lower+";");
		sb.append("type="+type+";");
		if(parent!=null)sb.append("parent name="+parent.getName()+";");
		return sb.toString();
	}
	
	
}
