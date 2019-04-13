package qmes.rule.execution.result;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.CONST;
import qmes.cases.def.CaseDef;
import qmes.model.HuskyObject;
import qmes.model.IndicatorTS;
import qmes.model.IndicatorV;
import qmes.model.Monitoring;
import qmes.model.TreatCandidate;
import qmes.model.TreatHistory;
import qmes.model.TreatOngoing;
import qmes.rule.def.DefConstraint;
import qmes.rule.def.DefObject;
import qmes.rule.def.DefRule;
import qmes.rule.def.lhs.LhsLine;
import qmes.rule.def.lhs.LineMatchLevel;
import qmes.rule.def.lhs.SPO;

public class ExplainUnmatch {
	
	private static final Logger log = LoggerFactory.getLogger(ExplainUnmatch.class);
	
	
	public List<LhsLine> parseDefObject(DefObject dbase) {
		String classname = dbase.getObjectType();
		List<DefConstraint> constraints = dbase.getConstraints();
		
		List<List<SPO>> allcombination = new ArrayList();
		travel(allcombination, constraints);
		List<LhsLine> lines = new ArrayList<LhsLine>();
		for(int p=0;p<allcombination.size();p++) {
			lines.add(handleAChain(classname, allcombination.get(p)));
		}
		return lines;
	}
	
	private void travel(List<List<SPO>> resultlist, List<DefConstraint> constraints) {
		if(constraints.size()==0){
			return;
		}else {
			List<SPO> cuts = SPO.cut(constraints.get(0).getExpression());
			
			//如果刚开始进行递归的话
			if(resultlist.size()==0) {
				for(SPO cut: cuts) {
					List<SPO> alist = new ArrayList<SPO>();
					alist.add(cut);
					resultlist.add(alist);
				}
				
			}else{
				if(cuts.size()==1) {
					for(List<SPO> achain: resultlist) {
						achain.add(cuts.get(0));
					}
				}else {
					List<List<SPO>> temp = new ArrayList();
					for (List<SPO> achain : resultlist) {
						for (SPO cut : cuts) {
							List<SPO> t = new ArrayList<SPO>();
							t.addAll(achain);
							t.add(cut);
							temp.add(t);
						}
					}
					resultlist.clear();
					resultlist.addAll(temp);
				}
			}
			
			if(constraints.size()>1) {
				travel(resultlist, constraints.subList(1, constraints.size()));
			}
		}
		
	}
	
	/**
	 * 因为一个DefConstraint中可能包含多种条件的或，比如 state=="一年"||=="两年"
	 * 所以一个DefObject会形成多种条件组合，
	 * 如果一个DefObject有n个DefConstaint，每个DefConstraint的有m1 m2 ... mn个或条件
	 * 为了表示这些条件，我们设计SPO对象，来表示一个DefConstraint中的一种条件
	 * 
	 * 那么DefObject本质上，会有m1*m2*....*mn，这么多种不同的组合，或者把这个组合称之为链(chain)
	 * travel()，把所有的chain列出来，一个chain是一个List<SPO>对象
	 * handleAChain()，则是处理单个链条
	 * 
	 * @param classname
	 * @param chain
	 * @return
	 */
	private LhsLine handleAChain(String classname, List<SPO> chain) {
		try {
			
			Object husky = Class.forName("qmes.model."+classname).newInstance();
			
			LhsLine line = new LhsLine();
			line.setHusky((HuskyObject)husky);
			line.setSpolist(chain);
			
			if(IndicatorV.class.getSimpleName().equals(classname)) {
				Class c = IndicatorV.class;

				setValue(line, "name", "setName");
				setValueDouble(line, "value", "setValue");
				setValue(line, "unit", "setUnit");
				setValueDouble(line, "time", "setTime");
				
			}else if(IndicatorTS.class.getSimpleName().equals(classname)) {
				Class c = IndicatorTS.class;
				
				setValue(line, "name", "setName");
				setValue(line, "surge", "setSurge");
				setValue(line, "trend", "setTrend");
				setValue(line, "timespan", "setTimespan");
				
			}else {
				String fullclassname = "qmes.model."+classname;
				Class c = Class.forName(fullclassname);
				
				setValue(line, "name", "setName");
				setValue(line, "state", "setState");
				if (c == Monitoring.class || c == TreatCandidate.class || c == TreatHistory.class || c == TreatOngoing.class) {
					setValueDouble(line, "stime", "setStime");
					setValueDouble(line, "etime", "setEtime");
				}else {
					setValueDouble(line, "time", "setTime");
				}
			}
			return line;
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
		return null;
	}
	
	private void setValue(LhsLine line, String key, String methodname)throws Exception{
		SPO spo = line.findSPOInChain(key);
		if (spo != null) {
			Method method = line.getHusky().getClass().getMethod(methodname, new Class[] { String.class });
			method.invoke(line.getHusky(), new Object[] { spo.object });
		}
	}
	
	private void setValueDouble(LhsLine line, String key, String methodname)throws Exception{
		SPO spo = line.findSPOInChain(key);
		if (spo != null) {
			Method method = line.getHusky().getClass().getMethod(methodname, new Class[] { double.class });
			method.invoke(line.getHusky(), new Object[] { Double.valueOf(spo.object).doubleValue() });
		}
	}
	
	/**
	 * 对于每个不完全匹配的rule的所有LhsLine，进行input huskyobject的匹配
	 * @param inputs
	 * @param lines
	 * @return
	 */
	
	public List<LineMatchLevel> matchAllInputForAllLine(List<HuskyObject> inputs, List<LhsLine> lines){
		
		if(lines!=null && lines.size()>0 && inputs!=null && inputs.size()>0) {
			List<LineMatchLevel> levels = new ArrayList<LineMatchLevel>();
			for(int i=0;i<lines.size();i++) {
				levels.add(matchAllInputForALine(inputs, lines.get(i)));
			}
			return levels;
		}
		return new ArrayList<LineMatchLevel>();
	}

	private LineMatchLevel matchAllInputForALine(List<HuskyObject> inputs, LhsLine line) {
		
		if(inputs!=null && inputs.size()>0) {
			List<LineMatchLevel> results = new ArrayList<LineMatchLevel>();
			for(int i=0;i<inputs.size();i++) {
				results.add(matchAInputForALine(inputs.get(i), line));
			}
			LineMatchLevel.sort(results);
			//这里只返回一个最高值，但实际上有可能有多个达到同样的值
			//TODO FIX IT IN THE FUTURE
			return results.get(0);
		}
		return null;
	}
	
	private LineMatchLevel matchAInputForALine(HuskyObject input, LhsLine line) {
		try {
			LineMatchLevel l = new LineMatchLevel();
			l.setInput(input);
			l.setLevel(LineMatchLevel.LEVEL_NO_MATCH);
			HuskyObject linehusky = line.getHusky();
			
			if(linehusky.getClass()== input.getClass()) {
				l.setLevel(LineMatchLevel.LEVEL_MATCH_CLASS);
				
				String linename = getStringValue(linehusky.getClass(), linehusky, "getName");
				String inputname = getStringValue(input.getClass(), input, "getName");
				
				if(linename.equals(inputname)) {
					l.setLevel(LineMatchLevel.LEVEL_MATCH_CLASS_FN);
					
					if(linehusky.getClass()==IndicatorTS.class) {
						String linesurge = getStringValue(linehusky.getClass(), linehusky, "getSurge");
						String inputsurge = getStringValue(input.getClass(), input, "getSurge");
						
						String linetrend = getStringValue(linehusky.getClass(), linehusky, "getTrend");
						String inputtrend = getStringValue(input.getClass(), input, "getTrend");
						
						String linets = getStringValue(linehusky.getClass(), linehusky, "getTimespan");
						String inputts = getStringValue(input.getClass(), input, "getTimespan");
						
						if(stringEquals(linesurge, inputsurge) && stringEquals(linetrend, inputtrend) && stringEquals(linets, inputts)) {
							l.setLevel(LineMatchLevel.LEVEL_MATCH_CLASS_FN_FS_TIME);
						}
						
					}else if(linehusky.getClass()==IndicatorV.class) {
						String lineunit = getStringValue(linehusky.getClass(), linehusky, "getUnit");
						String inputunit = getStringValue(input.getClass(), input, "getUnit");
						
						double linevalue = getDoubleValue(linehusky.getClass(), linehusky, "getValue");
						double inputvalue = getDoubleValue(input.getClass(), input, "getValue");
						
						SPO valuespo = line.findSPOInChain( "value");
						if(matchValue(valuespo, inputvalue, linevalue) && stringEquals(lineunit, inputunit)) {
							l.setLevel(LineMatchLevel.LEVEL_MATCH_CLASS_FN_FS);
							
							double linetime = getDoubleValue(linehusky.getClass(), linehusky, "getTime");
							double inputtime = getDoubleValue(input.getClass(), input, "getTime");
							SPO timespo = line.findSPOInChain("time");
							if(matchValue(timespo, inputtime, linetime)) {
								l.setLevel(LineMatchLevel.LEVEL_MATCH_CLASS_FN_FS_TIME);
							}
						}
						
					}else {
						String linestate = getStringValue(linehusky.getClass(), linehusky, "getState");
						String inputstate = getStringValue(input.getClass(), input, "getState");
						
						if(stringEquals(linestate, inputstate)) {
							l.setLevel(LineMatchLevel.LEVEL_MATCH_CLASS_FN_FS);
							
							if(isTimespanClass(linehusky.getClass())) {
								double linestime = getDoubleValue(linehusky.getClass(), linehusky, "getStime");
								double inputstime = getDoubleValue(input.getClass(), input, "getStime");
								SPO stimespo = line.findSPOInChain("stime");
								
								double lineetime = getDoubleValue(linehusky.getClass(), linehusky, "getEtime");
								double inputetime = getDoubleValue(input.getClass(), input, "getEtime");
								SPO etimespo = line.findSPOInChain("etime");
								
								if(matchValue(stimespo, inputstime, linestime) && matchValue(etimespo, inputetime, lineetime)) {
									l.setLevel(LineMatchLevel.LEVEL_MATCH_CLASS_FN_FS_TIME);
								}
								
							}else {
								double linetime = getDoubleValue(linehusky.getClass(), linehusky, "getTime");
								double inputtime = getDoubleValue(input.getClass(), input, "getTime");
								SPO timespo = line.findSPOInChain("time");
								if(matchValue(timespo, inputtime, linetime)) {
									l.setLevel(LineMatchLevel.LEVEL_MATCH_CLASS_FN_FS_TIME);
								}
							}
						}
					}
				}
			}
			return l;
		}catch(Exception ex) {
			ex.getMessage();
			log.error(ex.getMessage());
		}
		return null;
	}
	
	private boolean matchValue(SPO spo, double input, double condition) {
		if(spo.predicate.equals(SPO.PREDICATE_EQUALS))return input==condition;
		else if(spo.predicate.equals(SPO.PREDICATE_LARGER))return input>condition;
		else if(spo.predicate.equals(SPO.PREDICATE_LARGER_OR_EQUALS))return input>=condition;
		else if(spo.predicate.equals(SPO.PREDICATE_LESSER))return input<condition;
		else if(spo.predicate.equals(SPO.PREDICATE_LESSER_OR_EQUALS))return input<=condition;
		else if(spo.predicate.equals(SPO.PREDICATE_NOT_EQUALS))return input!=condition;
		else if(spo.predicate.equals(SPO.PREDICATE_NOT_EQUALS_2))return input!=condition;
		else return false;
	}

	
	private double getDoubleValue(Class c, Object o, String methodname)throws Exception {
		Method method = c.getMethod(methodname, new Class[] {});
		double value = (double)method.invoke(o, new Object[] {});
		return value;
	}
	
	private String getStringValue(Class c, Object o, String methodname)throws Exception {
		Method method = c.getMethod(methodname, new Class[] {});
		String value = (String)method.invoke(o, new Object[] {});
		return value;
	}
	
	private boolean isTimespanClass(Class c) {
		for(int i=0;i<CONST.timespanClasses.length;i++) {
			if(CONST.timespanClasses[i]==c)return true;
		}
		return false;
	}
	
	private boolean stringEquals(String str1, String str2) {
		if(str1==null && str2==null)return true;
		else if(str1!=null && str2!=null && str1.equals(str2))return true;
		return false;
		
	}
	

}
