package qmes.rule.def;

import java.util.ArrayList;
import java.util.List;

public class DefPackage extends DefBase{
	
	private String filepath;
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	private String name;
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
	private List<DefImport> imports = new ArrayList<DefImport>();
	
	public void addImport(DefImport di) {imports.add(di);}
	public List<DefImport> getImportList(){return imports;}
	
	private List<DefGlobal> globals = new ArrayList<DefGlobal>();
	
	public void addGlobal(DefGlobal dg) {globals.add(dg);}
	public List<DefGlobal> getGlobalList(){return globals;}
	
	private List<DefRule> rules = new ArrayList<DefRule>();
	public List<DefRule> getRules(){return rules;}
	public void addRule(DefRule dr) {rules.add(dr);}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("package "+name+"\n\n");
		for(int i=0;i<imports.size();i++) {
			sb.append(imports.get(i).toString()+";\n");
		}
		
		for(int i=0;i<globals.size();i++) {
			sb.append(globals.get(i).toString()+";\n");
		}
		
		for(int i=0;i<rules.size();i++) {
			sb.append(rules.get(i).toString()+"\n");
		}
		
		return sb.toString();
	}

}
