package qmes.rule.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qmes.model.HuskyObject;

public class DefRule extends DefBase {

	private String name;
	private Map<String, DefAttribute> attributes = new HashMap<String, DefAttribute>();

	private DefLhs lhs;
	private Object consequence;

	private String salience;
	
	private List<HuskyObject> rhsObject = new ArrayList<HuskyObject>();
	
	public List<HuskyObject> getRhsObject() {
		return rhsObject;
	}

	public void setRhsObject(List<HuskyObject> rhsObject) {
		this.rhsObject = rhsObject;
	}

	public String getSalience() {
		return salience;
	}

	public void setSalience(String salience) {
		this.salience = salience;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, DefAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, DefAttribute> attributes) {
		this.attributes = attributes;
	}

	public DefLhs getLhs() {
		return lhs;
	}

	public void setLhs(DefLhs lhs) {
		this.lhs = lhs;
	}

	public Object getConsequence() {
		return consequence;
	}

	public void setConsequence(Object consequence) {
		this.consequence = consequence;
	}

//	public void addRightObject(DefObject ho) {
//		rhsObjects.add(ho);
//	}
//	
//	public List<DefObject> getRhsObjects(){
//		return this.rhsObjects;
//	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("rule \"" + name + "\"\n");
		if (salience != null)
			sb.append("\tsalience " + salience);
		sb.append(lhs.toString());
		sb.append("\tthen\n\t\t");
		sb.append(consequence.toString());
		sb.append("\nend\n");
//		for (int i = 0; i < rhsObjects.size(); i++) {
//			sb.append("[target object=" + rhsObjects.get(i).toString() + "]\n");
//		}
		return sb.toString();
	}

}
