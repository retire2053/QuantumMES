package qmes.rule.def;

public class DefConstraint extends DefBase{
	private String expression = null;
	
	public DefConstraint() {
		
	}
	public DefConstraint(String expression) {
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String toString() {
		return expression;
	}
	
	
}
