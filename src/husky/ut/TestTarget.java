package husky.ut;

public class TestTarget {
	private String classname;
	private String propertyname;
	private Object targetvalue;
	
	public TestTarget(String clz, String propertyname, Object target) {
		this.classname = clz;
		this.propertyname = propertyname;
		this.targetvalue = target;
	}
	
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getPropertyname() {
		return propertyname;
	}
	public void setPropertyname(String propertyname) {
		this.propertyname = propertyname;
	}
	public Object getTargetvalue() {
		return targetvalue;
	}
	public void setTargetvalue(Object targetvalue) {
		this.targetvalue = targetvalue;
	}
}
