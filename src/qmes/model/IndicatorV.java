package qmes.model;

public class IndicatorV extends HuskyObject{

	private String name;
	private double time;
	private double value;
	private String unit;
	
	public IndicatorV(String name, double value, String unit, double time) {
		this.name = name;
		this.value = value;
		this.unit = unit;
		this.time = time;
	}
	
	public IndicatorV() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public String toString() {
		Object[][] kvarray = {
				{"classname", getClass().getSimpleName()},
				{"name", name},
				{"time", time},
				{"value", value},
				{"unit", unit},
			};
		return kvs(kvarray);
	}
}
