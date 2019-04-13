package qmes.nlp.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.config.def.NConfig;
import qmes.config.def.NConfigItem;
import qmes.config.storage.NConfigStorage;
import qmes.core.Model;
import qmes.model.HuskyObject;
import qmes.model.IndicatorTS;
import qmes.model.IndicatorV;
import qmes.model.Monitoring;
import qmes.model.TreatCandidate;
import qmes.model.TreatHistory;
import qmes.model.TreatOngoing;
import qmes.nlp.translate.def.DefaultValues;
import qmes.nlp.translate.def.Matchable;
import qmes.rule.def.DefBase;
import qmes.rule.def.DefConstraint;
import qmes.rule.def.DefExists;
import qmes.rule.def.DefNot;
import qmes.rule.def.DefObject;
import qmes.rule.def.DefOr;
import qmes.rule.def.lhs.LhsLine;
import qmes.rule.def.lhs.SPO;
import qmes.rule.execution.result.ExplainUnmatch;

public class MatchableHelper {
	
	private static final Logger log = LoggerFactory.getLogger(MatchableHelper.class);
	
	private Model model = null;
	public MatchableHelper(Model model) {
		this.model = model;
	}
	
	public String translateHuskyObject(HuskyObject husky) {
		LhsLine line = new LhsLine();
		line.setHusky(husky);
		return translateHuskyObject(line);
	}
	
	/**
	 * 当Case中的husky被输入的时候，line中的SPO chain是空指针
	 * 当Rule的RHS husky被输入的时候，line中间的SPO chain是空指针
	 * 当RUle的LHS husky被输入的时候，line中间的SPO chain不是空指针
	 * @param line
	 * @return
	 */
	public String translateHuskyObject(LhsLine line) {
		HuskyObject husky = line.getHusky();
		Class c = husky.getClass();
		
		if(c==IndicatorTS.class) {
			try {
				Method getName = c.getMethod("getName", new Class[] {});
				Method getSurge = c.getMethod("getSurge", new Class[] {});
				Method getTimespan = c.getMethod("getTimespan", new Class[] {});
				Method getTrend = c.getMethod("getTrend", new Class[] {});
				
				String name = (String)getName.invoke(husky, new Object[] {});
				String surge = (String)getSurge.invoke(husky, new Object[] {});
				String trend = (String)getTrend.invoke(husky, new Object[] {});
				String timespan = (String)getTimespan.invoke(husky, new Object[] {});
				
				String expr2 = "患者"+name+"在"+timespan+"内，"+(trend!=null?trend:"")+(surge!=null?surge:""); 
				return expr2;
			}catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				return "ERROR";
			}
		}else {
			Matchable matchable = model.getMatchableStorage().getMatchable(c);
			if(matchable!=null) {
				
				String expr = matchable.getExpr().getTemplate();
				
				String name = null;
				String state = null;
				double value = 0;
				double time = 0;
				double stime = 0;
				double etime = 0;
				String unit = null;
				try {
					if(c==IndicatorV.class) {
						name = getStringValue(c, husky, "getName");
						value = getDoubleValue(c, husky, "getValue");
						unit = getStringValue(c, husky, "getUnit");
						time = getDoubleValue(c, husky, "getTime");
					 	
						SPO valuespo = line.findSPOInChain("value");
						SPO timespo = line.findSPOInChain("time");
						
					 	String s = expr.replace("${fn}", name).replace("${fs}", valueSemantic(String.valueOf(value),  valuespo!=null?valuespo.predicate:null));
						s = timeProcessByValue(s, String.valueOf(time), timespo!=null?timespo.predicate:null);
						s = unitProcessByValue(s, unit);
						return s;
					}else {
						name = getStringValue(c, husky, "getName");
						state = getStringValue(c, husky, "getState");
						
						if (c == Monitoring.class || c == TreatCandidate.class || c == TreatHistory.class || c == TreatOngoing.class) {
							stime = getDoubleValue(c, husky, "getStime");
							etime = getDoubleValue(c, husky, "getEtime");
						}else {
							time = getDoubleValue(c, husky, "getTime");
						}
					 	
					 	String s = expr.replace("${fn}", name);
					 	if(state!=null)s = s.replace("${fs}",state);
					 	
					 	if (c == Monitoring.class || c == TreatCandidate.class || c == TreatHistory.class || c == TreatOngoing.class) {
					 		
					 		SPO stimespo = line.findSPOInChain("stime");
					 		SPO etimespo = line.findSPOInChain("etime");
					 		
					 		s  = timespanProcessByValue(s, String.valueOf(stime), String.valueOf(etime), stimespo!=null?stimespo.predicate:null , etimespo!=null?etimespo.predicate:null);
					 		
					 	}else {
					 		SPO timespo = line.findSPOInChain("time");
					 		s = timeProcessByValue(s, String.valueOf(time), timespo!=null?timespo.predicate:"");
					 	}
						s = extraReplace(s);
						return s;
					}
					
				}catch(Exception ex) {
					ex.printStackTrace();
					log.error(ex.getMessage());
				}
				
			}
			return "ERROR";
		}
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
	
	
	/**
	 * 只有一个input的时候的模糊匹配，会把input作为FN或者FS进行匹配
	 * @param input
	 * @param matchable
	 * @return
	 */
	public List<String> match(String input, Matchable matchable) {
		
		List<String> result = new ArrayList<String>();
		String expr = matchable.getExpr().getTemplate();
		List<DefaultValues> dvs = matchable.getDefaults();
		
		try {
			for(int i=0;i<dvs.get(1).getValues().size();i++) {
				String v = dvs.get(1).getValues().get(i);
				String s = expr.replace("${fn}", input).replace("${fs}",v);
				s = timeProcessByDefault(s, matchable);
				result.add(s);
				
			}
			
			for(int i=0;i<dvs.get(0).getValues().size();i++) {
				String v = dvs.get(0).getValues().get(i);
				String s=expr.replace("${fs}", input).replace("${fn}",v);
				s = timeProcessByDefault(s, matchable);
				result.add(s);
			}

		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
		
		return result;
	}

	/**
	 * 翻译Drl LHS的条件为汉语可以表达的文字
	 * @param dbase 每个具体的DefBase，最终是转化为DefObject可以被处理
	 * @return
	 */
	public String translateLHS(DefBase dbase, ExplainUnmatch eu) {
		if(dbase instanceof DefObject)
			return translateDefObject((DefObject)dbase, eu);
		else if(dbase instanceof DefNot) {
			return translateDefNot((DefNot)dbase, eu);
		}else if(dbase instanceof DefOr) {
			return translateDefOr((DefOr)dbase, eu);
		}
		else return "ERROR";
	}
	
	private String translateDefObject(DefObject dbase, ExplainUnmatch eu) {
		String classname = dbase.getObjectType();
		
		List<LhsLine> lines = eu.parseDefObject(dbase);
		
		StringBuffer sb = new StringBuffer();
		if(lines!=null && lines.size()>0) {
			for(int p=0;p<lines.size();p++) {
				if(p>0)sb.append("，或");
				sb.append(translateHuskyObject(lines.get(p)));
			}
		}else {
			sb.append("ERROR");
		}
		return sb.toString();
	}
	
	
	
	private String translateDefOr(DefOr dbase, ExplainUnmatch eu) {
		List<DefBase> dbases = dbase.getObjects();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<dbases.size();i++) {
			if(i>0)sb.append("，或");
			sb.append(translateLHS(dbases.get(i), eu));
		}
		return sb.toString();
	}
	
	private String translateDefNot(DefNot dbase, ExplainUnmatch eu) {
		if(dbase.getInner()!=null && dbase.getInner() instanceof DefExists) {
			DefExists de = (DefExists)dbase.getInner();
			if(de.getInner()!=null && de.getInner() instanceof DefObject) {
				return "患者未提及"+translateLHS((DefObject)de.getInner(), eu);
			}
		}
		return "ERROR";
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	// 翻译中出现的辅助性的替代方法
	/////////////////////////////////////////////////////////////////////////

	private String extraReplace(String s) {
		NConfig semanticConfig = model.getNConfigStorage().getConfig(NConfigStorage.CONFIG_SEMANTIC_TRANSFER);
		List<NConfigItem> items = semanticConfig.getList();
		if(items.size()>0) {
			for(int i=0;i<items.size();i++) {
				NConfigItem item = items.get(i);
				String name = item.getName();
				String value = item.getValue();
				s = s.replace(name, value);
			}
		}
		return s;
	}
	
	private String valueSemantic(String value, String predicate) {

		if(predicate!=null) {	//LHS生成的husky
			if (predicate.equals(SPO.PREDICATE_EQUALS)) {
				return value;
			} else if (predicate.equals(SPO.PREDICATE_LARGER)) {
				return "大于" + value;
			} else if (predicate.equals(SPO.PREDICATE_LARGER_OR_EQUALS)) {
				return "不小于" + value;
			} else if (predicate.equals(SPO.PREDICATE_LESSER)) {
				return "小于" + value;
			} else if (predicate.equals(SPO.PREDICATE_LESSER_OR_EQUALS)) {
				return "不大于" + value;
			} else if (predicate.equals(SPO.PREDICATE_NOT_EQUALS)) {
				return "不等于" + value;
			} else if (predicate.equals(SPO.PREDICATE_NOT_EQUALS_2)) {
				return "不等于" + value;
			} else {
				return value;
			}			
		}else {	//RHS生成的husky或者Case的husky
			return value;
		}

	}	
	
	private String timeSemantic(String time, String predicate) {

		if(predicate!=null) {//LHS生成的husky
			if (predicate.equals(SPO.PREDICATE_EQUALS)) {
				return getTime(time);
			} else if (predicate.equals(SPO.PREDICATE_LARGER)) {
				return "晚于" + getTime(time);
			} else if (predicate.equals(SPO.PREDICATE_LARGER_OR_EQUALS)) {
				return "不早于" + getTime(time);
			} else if (predicate.equals(SPO.PREDICATE_LESSER)) {
				return "早于" + getTime(time);
			} else if (predicate.equals(SPO.PREDICATE_LESSER_OR_EQUALS)) {
				return "不晚于" + getTime(time);
			} else if (predicate.equals(SPO.PREDICATE_NOT_EQUALS)) {
				return "不是" + getTime(time);
			} else if (predicate.equals(SPO.PREDICATE_NOT_EQUALS_2)) {
				return "不是" + getTime(time);
			} else {
				return getTime(time);
			}
		}else { //RHS生成的husky或者Case的husky
			return getTime(time);
		}

	}
	
	private String timeProcessByValue(String input, String time, String predicate) {
		if (input.indexOf("${time}") >= 0 && time!=null) {
			return input.replace("${time}", timeSemantic(time, predicate));
		}
		return input;
	}
	
	private String timespanProcessByValue(String input, String stime, String etime, String stimepredicate,
			String etimepredicate) {
		if (input.indexOf("${timespan}") >= 0) {
			if (stime.equals(etime)) {
				return input.replace("${timespan}", timeSemantic(stime, SPO.PREDICATE_EQUALS));
			} else {
				String s1 = timeSemantic(stime, stimepredicate);
				String s2 = timeSemantic(etime, etimepredicate);
				return input.replace("${timespan}", "自" + s1 + "到" + s2);
			}
		} else
			return input;
	}
	
	private String unitProcessByValue(String input, String unit) {
		if (input.indexOf("${u}") >= 0 && unit != null) {
			return input.replace("${u}", unit);
		}
		return input;
	}
		
	private String timeProcessByDefault(String input, Matchable matchable) {
		List<DefaultValues> dvs = matchable.getDefaults();

		String s = input;
		if(s.indexOf("${time}")>=0) {
			if(dvs.get(Matchable.INDEX_TIME).getValues().size()>0) {
				String time = dvs.get(Matchable.INDEX_TIME).getValues().get(0);
				s = s.replace("${time}", getTime(time));	
			}else {
				log.info("no ${time} default value defined");
				return "ERROR";
			}
		}
		
		if(s.indexOf("${timespan}")>=0) {
			if(dvs.get(Matchable.INDEX_TIMESPAN).getValues().size()>1) {
				String stime = dvs.get(Matchable.INDEX_TIMESPAN).getValues().get(0);
				String etime = dvs.get(Matchable.INDEX_TIMESPAN).getValues().get(1);
				s = s.replace("${timespan}", getTimeSpan(stime, etime));
			}else {
				log.info("no ${timespan} default value defined");
				return "ERROR";
			}
		}
		
		if(s.indexOf("${u}")>=0) {
			if(dvs.get(Matchable.INDEX_UNIT).getValues().size()>0) {
				String unit = dvs.get(Matchable.INDEX_UNIT).getValues().get(0);
				s = s.replace("${u}", unit);
			}else {
				log.info("no ${u} default value defined");
				return "ERROR";
			}
		}
		return s;
	}
	
	private String getTime(String s) {
		try {
			
			double v = Double.parseDouble(s);
			if (v > 0) {
				return "" + v + "个月后";
			} else if (v == 0)
				return "当前";
			else {
				return "" + (-1 * v) + "个月前";
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			return "ERROR";
		}
	}
	
	private String getTimeSpan(String s1, String s2) {
		String s3 = getTime(s1);
		String s4 = getTime(s2);
		return "自"+s3+"到"+s4;
	}
}
