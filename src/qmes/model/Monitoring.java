package qmes.model;

public class Monitoring extends HuskyObject {
	
	public Monitoring() {
		
	}

	public double getStime() {
		return stime;
	}

	public void setStime(double stime) {
		this.stime = stime;
	}

	public double getEtime() {
		return etime;
	}

	public void setEtime(double etime) {
		this.etime = etime;
	}

	private String name;
	private double stime;
	private double etime;
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

	public String toString() {
		Object[][] kvarray = { { "classname", getClass().getSimpleName() }, { "name", name }, { "state", state },
				{ "stime", stime }, { "etime", etime }, };
		return kvs(kvarray);
	}
}
