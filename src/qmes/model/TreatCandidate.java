package qmes.model;

public class TreatCandidate extends HuskyObject{
	
	public TreatCandidate() {
	}

	public TreatCandidate(String name) {
		this.name = name;
		this.stime = 0;
		this.etime = -1;
		this.priority = 0;
	}
	
	public TreatCandidate(String name, double stime) {
		this.name = name;
		this.stime = stime;
		this.etime = -1;
		this.priority = 0;
	}
	public TreatCandidate(String name, double stime, double etime) {
		this.name = name;
		this.stime = stime;
		this.etime = etime ;
		this.priority = 0;
	}

	private String name;
	private double stime;
	private double etime;		//-1代表不详
	private int priority;
	private String state;
	
	
	
	private String spec;
	
	public String getSpec() {return spec;}
	public void setSpec(String spec) {this.spec = spec;}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
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
	
	private int adviselevel;

	public int getAdviselevel() {
		return adviselevel;
	}

	public void setAdviselevel(int adviselevel) {
		this.adviselevel = adviselevel;
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

	public String toString() {
		Object[][] kvarray = {
				{"classname", getClass().getSimpleName()},
				{"name", name},
				{"state", state},
				{"stime", stime},
				{"etime", etime},
				{"priority", priority},
				{"adviselevel", adviselevel},
			};
		return kvs(kvarray);
	}
	
	public TreatCandidate clone() {
		TreatCandidate tc = this;
		TreatCandidate tc1 = new TreatCandidate();
		tc1.setClazz(tc.getClazz());
		tc1.setEtime(tc.getEtime());
		tc1.setStime(tc.getStime());
		tc1.setName(tc.getName());
		tc1.setState(tc.getState());
		tc1.setPriority(tc.getPriority());
		tc1.setAdviselevel(tc.getAdviselevel());
		tc1.setSpec(tc.getSpec());
		return tc1;
	}
}
