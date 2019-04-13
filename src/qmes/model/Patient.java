package qmes.model;

public class Patient extends HuskyObject {
	
	public Patient(){}

	private String name = null;
	
	private String state = null;
	
	private double time;
	
	public String getName() {return name;}
	
	public void setName(String name) { this.name = name;}
	
	public String getState() { return state;}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public double getTime() { return time;}
	public void setTime(double time) {this.time = time;}

	public String toString() {
		Object[][] kvarray = {
				{"classname", getClass().getSimpleName()},
				{"name", name},
				{"state", state},
				{"time", time}
			};
		return kvs(kvarray);
	}
}
