package qmes.indicator.def;

public class UnitDef implements Base {
	
	public UnitDef() {}

	private String unit;
	
	public String getUnit() {return unit;}
	
	public void setUnit(String unit) {this.unit= unit;}
	
	public String toString() {
		return "unit="+unit;
	}
}
