package qmes.model;

public class TreatOngoing extends HuskyObject {

	public TreatOngoing() {
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
	private String spec;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
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
	
	public TreatOngoing clone() {
		TreatOngoing tc = this;
		TreatOngoing tc1 = new TreatOngoing();
		tc1.setClazz(tc.getClazz());
		tc1.setEtime(tc.getEtime());
		tc1.setStime(tc.getStime());
		tc1.setName(tc.getName());
		tc1.setState(tc.getState());
		tc1.setSpec(tc.getSpec());
		return tc1;
	}
}
