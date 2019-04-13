package qmes.rule.def;


public class DefExists extends DefBase{
	private DefBase inner;
	
	public void setInner(DefBase inner) {
		this.inner = inner;
	}
	
	public DefBase getInner() {
		return this.inner;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" exists "+inner.toString()+"");
		return sb.toString();
	}
}
