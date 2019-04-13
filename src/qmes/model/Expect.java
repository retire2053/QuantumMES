package qmes.model;

public class Expect extends HuskyObject{

	public Expect() {}
	
	public Expect(String expectindex, float highvalue, float lowvalue, int time) {
		this.expectindex = expectindex;
		this.highvalue = highvalue;
		this.lowvalue = lowvalue;
		this.time = time;
	}
	
	public Expect(String expectindex, String predicate, int time) {
		this.expectindex = expectindex;
		this.predicate = predicate;
		this.time = time;
	}

	private String name;
	private String expectindex;
	private float indexvalue;
	private String predicate;
	private String unit;
	
	private String state;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getName() { return name;}
	public void setName(String name) {this.name = name;}
	
	public float getIndexvalue() {return indexvalue;}

	public void setIndexvalue(float indexvalue) {this.indexvalue = indexvalue;}

	public String getPredicate() {return predicate;}

	public void setPredicate(String predicate) {this.predicate = predicate;}
	
	public void setUnit(String unit) {this.unit = unit;}
	public String getUnit() {return unit;}

	private float lowvalue;	//possibility high
	private float highvalue;	//possibility low
	private double time;

	public String getExpectindex() {
		return expectindex;
	}

	public void setExpectindex(String expectindex) {
		this.expectindex = expectindex;
	}

	public float getHighvalue() {return highvalue;}
	public float getLowvalue() { return lowvalue;}
	public void setHighvalue(float highvalue) {this.highvalue = highvalue;}
	public void setLowvalue(float lowvalue) {this.lowvalue = lowvalue;}

	public void setTime(double time) {this.time = time;}
	public double getTime() {return time;}

	public String toString() {
		Object[][] kvarray = {
				{"classname", getClass().getSimpleName()},
				{"name", name},
				{"state", state},
				{"expectindex", expectindex},
				{"predicate", predicate},
				{"indexvalue", indexvalue},
				{"highvalue", highvalue},
				{"lowvalue", lowvalue},
				{"time", time}
			};
		return kvs(kvarray);
	}
}
