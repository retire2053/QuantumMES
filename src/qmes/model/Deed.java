package qmes.model;

public class Deed extends HuskyObject {

	private String name;
	private String state;
	private double time;

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
		Object[][] kvarray = { { "classname", getClass().getSimpleName() }, { "name", name }, { "state", state },
				{ "time", time } };
		return kvs(kvarray);
	}
}
