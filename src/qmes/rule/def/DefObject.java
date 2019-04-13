package qmes.rule.def;

import java.util.ArrayList;
import java.util.List;

public class DefObject extends DefBase {

	// pattern descr

	private String objectType;
	private String identifier;

	public DefObject() {}

	public DefObject(String objectType, String expression) { // 当defObject作为查询条件的时候，用这个构造函数
		this.objectType = objectType;
		DefConstraint dc = new DefConstraint();
		dc.setExpression(expression);
		constraints.add(dc);
	}

	private List<DefConstraint> constraints = new ArrayList<DefConstraint>();

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public List<DefConstraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<DefConstraint> constraints) {
		this.constraints = constraints;
	}

	public void addConstraint(DefConstraint dc) {
		this.constraints.add(dc);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (identifier != null)
			sb.append(identifier + " : ");
		sb.append(objectType);
		sb.append("(");
		for (int i = 0; i < constraints.size(); i++) {
			sb.append(constraints.get(i).toString());
			if (i < constraints.size() - 1)
				sb.append(" , ");
		}
		sb.append(")");
		return sb.toString();
	}

}
