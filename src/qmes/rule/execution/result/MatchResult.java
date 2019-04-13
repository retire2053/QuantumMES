package qmes.rule.execution.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qmes.annotation.AnnotationLine;
import qmes.annotation.AnnotationRule;
import qmes.core.Model;
import qmes.rule.def.DefBase;
import qmes.rule.def.DefRule;

public class MatchResult {

	public static String NECESSARY_FOUND = "已匹配必要条件";
	public static String NECESSARY_NOT_FOUND = "未匹配的必要条件";
	public static String UNNECESSARY_FOUND = "已匹配的非必要条件";
	public static String UNNECESSARY_NOT_FOUND = "未匹配的非必要条件";
	
	public static String STATE_MATCHED = "必须条件已匹配";
	public static String STATE_UNMATCHED = "必须条件未匹配";
	
	public static String VALUE = "评估得分";
	
	private HashMap<String, List<DefBase>> map = new HashMap<String, List<DefBase>>();
	
	private Model model = null;
	public MatchResult(Model model) {
		
		this.model = model;
		
		map.put(NECESSARY_FOUND, new ArrayList<DefBase>());
		map.put(NECESSARY_NOT_FOUND, new ArrayList<DefBase>());
		map.put(UNNECESSARY_FOUND, new ArrayList<DefBase>());
		map.put(UNNECESSARY_NOT_FOUND, new ArrayList<DefBase>());
	}
	
	public List<DefBase> getNecessaryFound(){
		return map.get(NECESSARY_FOUND);
	}
	public List<DefBase> getNecessaryNotFound(){
		return map.get(NECESSARY_NOT_FOUND);
	}
	public List<DefBase> getUnnecessaryFound(){
		return map.get(UNNECESSARY_FOUND);
	}
	public List<DefBase> getUnnecessaryNotFound(){
		return map.get(UNNECESSARY_NOT_FOUND);
	}
	
	
	public void addNecessaryFound(DefBase defo) {
		map.get(NECESSARY_FOUND).add(defo);
	}
	public void addNecessaryNotFound(DefBase defo) {
		map.get(NECESSARY_NOT_FOUND).add(defo);
	}
	public void addUnnecessaryFound(DefBase defo) {
		map.get(UNNECESSARY_FOUND).add(defo);
	}
	public void addUnnecessaryNotFound(DefBase defo) {
		map.get(UNNECESSARY_NOT_FOUND).add(defo);
	}
	
	public boolean isMatched() {
		if(map.get(NECESSARY_NOT_FOUND).size()==0 && map.get(UNNECESSARY_NOT_FOUND).size()==0) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean testLineMatch(int index) {
		if(index>=0 && index<rule.getLhs().getObjectList().size()) {
			DefBase dbase = rule.getLhs().getObjectList().get(index);
			if(map.get(NECESSARY_FOUND).indexOf(dbase)>=0 || map.get(UNNECESSARY_FOUND).indexOf(dbase)>=0) {
				return true;
			}
		}
		return false;
	}
	
	
//	private boolean matched = false;
	private int value = 0;
	private int totalscore = 0;
	
//	public boolean isMatched() {return matched;}
//	public void setMatched(boolean matched) {this.matched = matched;}
	
	public int getValue() {return value;}
	public void setValue(int value) {this.value = value;}
	
	public int getTotalscore() {return totalscore;}
	public void setTotalscore(int totalscore) {this.totalscore = totalscore;}
	
	private DefRule rule = null;
	public DefRule getRule() {return rule;}
	public void setRule(DefRule rule) {this.rule = rule;}
	
	public String getState() {
		if(isMatched()) return STATE_MATCHED+" "+VALUE+"="+getValue();
		else return STATE_UNMATCHED;
	}
	
	public String toString() {
		return rule.getName();
	}
	
	public String toFullString(Model model) {
		StringBuffer sb = new StringBuffer();
		append(model, sb,NECESSARY_FOUND,map.get(NECESSARY_FOUND), "blue");
		append(model, sb,NECESSARY_NOT_FOUND,map.get(NECESSARY_NOT_FOUND) ,"red");
		append(model, sb,UNNECESSARY_FOUND,map.get(UNNECESSARY_FOUND) ,"blue");
		append(model, sb,UNNECESSARY_NOT_FOUND,map.get(UNNECESSARY_NOT_FOUND) , "red");
		return sb.toString();
	}
	private void append(Model model, StringBuffer sb, String title,  List<DefBase> bases,  String color) {
		sb.append(title+"<br>");
		String colorhead = "";
		String colorend = "";
		if(color!=null)colorhead =  "<font color=\""+color+ "\">";
		if(color!=null)colorend =  "</font>";
		AnnotationRule ar = model.getAnnotationRule(rule);
		for(int i=0;i<bases.size();i++) {
			DefBase dbase = bases.get(i);
			int index = rule.getLhs().getObjectList().indexOf(dbase);
			if(index>=0) {
				AnnotationLine al = ar.getLines().get(index);
				sb.append("&nbsp;&nbsp;["+ al.getPower()+"]&nbsp;&nbsp;"+ colorhead+ dbase.toString()+ colorend+"<br>");
			}
			else {
				sb.append("&nbsp;&nbsp;[NOT SET]&nbsp;&nbsp;"+ colorhead+ dbase.toString()+ colorend+"<br>");
			}
			
		}
	}
	
}
