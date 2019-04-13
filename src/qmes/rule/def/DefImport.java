package qmes.rule.def;

public class DefImport extends DefBase{
	private String target;
	
	public DefImport(String target) {this.target = target;}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public String toString() {
		return "import "+target;
	}
	
}
