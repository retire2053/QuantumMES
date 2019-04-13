package qmes.model;

public class Question extends HuskyObject {

	public Question() {}
	
	public Question(String name) {
		this.name = name;
	}
	public Question(String name, int time) {
		this.name = name;
		this.time = time;
	}

	private String name;
	private double time;
	private String state;


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setTime(double time) {this.time = time;}
	public double getTime() {return time;}
	
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
