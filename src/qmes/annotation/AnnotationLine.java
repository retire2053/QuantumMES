package qmes.annotation;

import java.io.Serializable;

public class AnnotationLine implements Serializable {

	private AnnotationRule parent;
	
	private boolean necessary = false;
	private int power = 10;
	private String explanation = "";
	
	
	public AnnotationLine() {
	}
	
	public AnnotationLine(boolean necessary, int power, String explanation) {
		this.necessary = necessary;
		this.power = power;
		this.explanation = explanation;
	}

	public AnnotationRule getParent() {
		return parent;
	}

	public void setParent(AnnotationRule parent) {
		this.parent = parent;
	}

	public boolean isNecessary() {
		return necessary;
	}

	public void setNecessary(boolean necessary) {
		this.necessary = necessary;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	
	
}
