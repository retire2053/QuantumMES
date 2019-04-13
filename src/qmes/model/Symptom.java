package qmes.model;

public class Symptom extends HuskyObject{
	public Symptom() {}
	
	public Symptom(String name) {
		this.name = name;
	}
	public Symptom(String name, int time) {
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

	public Symptom clone() {
		Symptom tc = this;
		Symptom tc1 = new Symptom();
		tc1.setClazz(tc.getClazz());
		tc1.setTime(tc.getTime());
		tc1.setName(tc.getName());
		tc1.setState(tc.getState());
		return tc1;
	}
}
