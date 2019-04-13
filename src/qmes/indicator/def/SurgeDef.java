package qmes.indicator.def;

public class SurgeDef implements Base{
	
	public SurgeDef() {}

	private double surge;
	
	public double getSurge() {return surge;}
	
	public void setSurge(double surge) {this.surge= surge;}
	
	public String toString() {
		return "surge="+String.valueOf(surge);
	}
	
}
