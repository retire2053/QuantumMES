package qmes.model;

public class NeedMoreIndicator extends HuskyObject {
	
	private String name;
	private String state;
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	private double time;
	
	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public NeedMoreIndicator() {}
	
	public NeedMoreIndicator(String name, int time) {
		this.name = name;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		Object[][] kvarray = {
				{"classname", getClass().getSimpleName()},
				{"name", name},{"state", state},
				{"time", time},
			};
		return kvs(kvarray);
	}

}
