package qmes.nlp.translate.def;

public class Expression {

	private String template;
	
	public Expression() {}
	
	public Expression(String template) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String toString() {
		return template;
	}
}
