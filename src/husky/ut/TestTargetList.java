package husky.ut;

public class TestTargetList {

	private String classname;
	private Object[][] kvlist;
	
	public TestTargetList(String clz, Object[][] kvlist) {
		this.classname = clz;
		this.kvlist = kvlist;
	}
	
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
	public int getKVCount() {
		return kvlist.length;
	}
	
	public String getPropertyname(int index) {
		return (String)kvlist[index][0];
	}
	
	public Object getTargetvalue(int index) {
		return kvlist[index][1];
	}

}
