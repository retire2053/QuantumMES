package qmes.model;

public abstract class HuskyObject {
	
	public abstract String toString();
	
	private String clazz;
	public String getClazz() {return clazz;}
	public void setClazz(String clazz) {this.clazz = clazz;}
	
	protected String kv(String k, Object v) { return k+"="+String.valueOf(v)+";"; }
	
	protected String kvs(Object[][] kvarray) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<kvarray.length;i++) {
			if(kvarray[i][1]!=null)sb.append( kv((String)kvarray[i][0], kvarray[i][1]));
		}
		return sb.toString();
	}
}
