package qmes.nlp.translate.def;

import java.util.ArrayList;
import java.util.List;

public class Matchable{
	
	public static String KEY_NAME = "name";
	public static String KEY_STATE = "state";
	public static String KEY_TIME = "time";
	public static String KEY_TIMESPAN = "timespan";
	public static String KEY_UNIT = "unit";
	
	public static int INDEX_NAME = 0;
	public static int INDEX_STATE = 1;
	public static int INDEX_TIME = 2;
	public static int INDEX_TIMESPAN = 3;
	public static int INDEX_UNIT = 4;
	
	
	public Matchable() {
		
		DefaultValues dvname = new DefaultValues();
		dvname.setKey(KEY_NAME);
		
		DefaultValues dvstate = new DefaultValues();
		dvstate.setKey(KEY_STATE);
		
		DefaultValues dvtime = new DefaultValues();
		dvstate.setKey(KEY_TIME);
		
		DefaultValues dvtimespan = new DefaultValues();
		dvstate.setKey(KEY_TIMESPAN);
		
		DefaultValues u = new DefaultValues();
		dvstate.setKey(KEY_UNIT);
		
		defaults.add(dvname);
		defaults.add(dvstate);
		defaults.add(dvtime);
		defaults.add(dvtimespan);
		defaults.add(u);
	}
	
	private String name;
	private Class clazz;
	private Expression expr;
	private List<DefaultValues> defaults = new ArrayList<DefaultValues>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	public Expression getExpr() {
		return expr;
	}
	public void setExpr(Expression expr) {
		this.expr = expr;
	}
	public List<DefaultValues> getDefaults() {
		return defaults;
	}
	public void setDefaults(List<DefaultValues> defaults) {
		this.defaults = defaults;
	}
	
	public String toString() {
		return name;
	}
	
	
}
