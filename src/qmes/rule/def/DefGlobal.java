package qmes.rule.def;

public class DefGlobal extends DefBase{
	
	private String identifier;
	private String type;
	
	public DefGlobal(String type, String identifier) {
		this.type = type;
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String toString() {
		return type+" "+identifier;
	}
	
	
}
