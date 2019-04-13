package qmes.word.search;

public class WordSequence {
	
	//在索引对象中，真正参与搜索匹配的是fnword和fsword，本质上是featurename/featurestate的同义词
	//但是输出的结果中，应当是用的是主词
	
	private String namespace = null;
	private String clazz = null;
	private String fn = null;
	private String fs = null;
	private String fnword = null;
	private String fsword = null;
	private String group = null;
	
	WordSequence(String namespace, String clazz, String fn, String fs, String fnword, String fsword, String group){
		this.namespace = namespace;
		this.clazz = clazz;
		this.fn = fn;
		this.fs = fs;
		this.fnword = fnword;
		this.fsword = fsword;
		this.group = group;
		
	}
	
	public String getNamespace() {return namespace;}
	public String getClazz() {return clazz;}
	public String getFeatureName() {return fn;}
	public String getFeatureState() {return fs;}
	public String getFnword() {return fnword;}
	public String getFsword() {return fsword;}
	public String getGroup() {return group;}
	
	
	public String toSearchString() {
		return fnword+fsword;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("clazz=" + clazz + ";");
		sb.append("namespace=" + namespace + ";");
		sb.append("feature name=" + fn + ";");
		sb.append("feature state=" + fs+";");
		sb.append("feature name word="+ fnword+";");
		sb.append("feature state word="+ fsword+";");
		sb.append("group="+group+";");
		return sb.toString();
	}
	
}