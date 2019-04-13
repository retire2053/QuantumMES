package qmes.model;

public class IndicatorS extends HuskyObject{
	private String name;
	private String state;
	private double time;
	private double suspect=1.0;	//默认，都是100%
	
	public IndicatorS(String name, String state, double time) {
		this.name = name;
		this.state = state;
		this.time = time;
	}
	
	public double getSuspect() {return suspect;}
	public void setSuspect(double suspect) {this.suspect = suspect;}
	

	
	public IndicatorS() {
		this.time = 0;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	
	public String toString() {
		Object[][] kvarray = {
				{"classname", getClass().getSimpleName()},
				{"name", name},
				{"state", state},
				{"time", time},
			};
		return kvs(kvarray);
	}
}
