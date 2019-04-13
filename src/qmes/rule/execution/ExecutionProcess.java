package qmes.rule.execution;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.CONST;
import qmes.cases.def.CaseDef;
import qmes.cases.def.CaseHelper;
import qmes.core.Model;
import qmes.indicator.calc.VALUE;
import qmes.indicator.calc.VALUES;
import qmes.indicator.def.IndicatorDef;
import qmes.model.Diagnose;
import qmes.model.HuskyObject;
import qmes.model.IndicatorS;
import qmes.model.IndicatorTS;
import qmes.model.IndicatorV;
import qmes.model.Symptom;
import qmes.model.TreatCandidate;
import qmes.model.TreatHistory;
import qmes.model.TreatOngoing;
import qmes.rule.def.DefRule;
import qmes.rule.execution.result.MatchResult;
import qmes.rule.execution.result.StackedMatchResult;
import qmes.word.def.Word;
import qmes.word.storage.WordStorage;
import qmes.word.storage.WordStorageHelper;

public class ExecutionProcess {
	
	private static final Logger log = LoggerFactory.getLogger(ExecutionProcess.class);
	
	private Model model = null;
	public ExecutionProcess(Model model) {
		this.model = model;
	}
	
	
	//////////////////////////////////////////////////////////////////
	// 该方法用用来根据词语的上下级生成衍生的输出条件
	// 目前仅限于在TreatXXX, Symptom, Diagnose等几个类中
	//////////////////////////////////////////////////////////////////
	
	private HuskyObject clone(HuskyObject ho) {
		if (ho instanceof TreatCandidate)
			return ((TreatCandidate) ho).clone();
		else if (ho instanceof TreatHistory)
			return ((TreatHistory) ho).clone();
		else if (ho instanceof TreatOngoing)
			return ((TreatOngoing) ho).clone();
		else if (ho instanceof Symptom)
			return ((Symptom) ho).clone();
		else if (ho instanceof Diagnose)
			return ((Diagnose) ho).clone();
		else
			return null;
	}
	
	private void generateParent4FN(HuskyObject condition, WordStorage ws, List<HuskyObject> list) {
		HuskyObject ho = clone(condition);
		if(ho!=null) {
			try {
				Method getName = condition.getClass().getMethod("getName", new Class[] {});
				String name = (String) getName.invoke(condition, new Object[] {});
				Word w = ws.findWord(name);
				if (w != null && w.getParent() != null && w.getParent().length() > 0) {
					Method setName = ho.getClass().getMethod("setName", new Class[] { String.class });
					setName.invoke(ho, new Object[] { w.getParent() });
					list.add(ho);
					generateParent4FN(ho, ws, list);	//迭代找到父亲
				}
			}catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}
	}
	
	private void generateParent4FS(HuskyObject condition, WordStorage ws, List<HuskyObject> list) {
		HuskyObject ho = clone(condition);
		if(ho!=null) {
			try {
				Method getState = condition.getClass().getMethod("getState", new Class[] {});
				String state = (String) getState.invoke(condition, new Object[] {});
				Word w = ws.findWord(state);
				if (w != null && w.getParent() != null && w.getParent().length() > 0) {
					Method setState = ho.getClass().getMethod("setState", new Class[] { String.class });
					setState.invoke(ho, new Object[] { w.getParent() });
					list.add(ho);
					generateParent4FS(ho, ws, list);	//迭代找到父亲
				}
			}catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}
	}
	
	public List<HuskyObject> generateParents4FN(List<HuskyObject> conditions, String namespace) {
		WordStorage ws = model.getWordStorage(namespace, CONST.TYPE_FEATURE_NAMES);
		List<HuskyObject> hos = new ArrayList<HuskyObject>();
		if(conditions.size()>0) {
			for(int i=0;i<conditions.size();i++) {
				generateParent4FN(conditions.get(i), ws, hos);
			}
		}
		return hos;
	}
	
	public List<HuskyObject> generateParents4FS(List<HuskyObject> conditions, String namespace) {
		WordStorage ws = model.getWordStorage(namespace, CONST.TYPE_FEATURE_STATES);
		List<HuskyObject> hos = new ArrayList<HuskyObject>();
		if(conditions.size()>0) {
			for(int i=0;i<conditions.size();i++) {
				generateParent4FS(conditions.get(i), ws, hos);
			}
		}
		return hos;
	}
	
	
	
	//////////////////////////////////////////////////////////////////
	// 该方法用来计算TS，在Case查看中会使用到，同时也在run()方法中做预处理
	//////////////////////////////////////////////////////////////////
	public List<HuskyObject> calcTS(List<HuskyObject> conditions){
		List<IndicatorTS> calcTS = new ArrayList<IndicatorTS>();
		List<IndicatorS> calcConditions = new ArrayList<IndicatorS>();
		
		log.info("........CALCULATE TS(timespan)........");

		HashMap<String, List<IndicatorV>> map = new HashMap<String, List<IndicatorV>>();
		if(conditions.size()>0)
			for(int i=0; i< conditions.size();i++) {
				HuskyObject ho = (HuskyObject)conditions.get(i);
				if(ho instanceof IndicatorV) {
					IndicatorV iv = (IndicatorV)ho;
					String name = iv.getName();
					if(map.keySet().contains(name)){
						map.get(name).add(iv);
					}else {
						ArrayList<IndicatorV> l = new ArrayList<IndicatorV>();
						l.add(iv);
						map.put(name, l);
					}
				}
			}
		
		//每个name相同的indicatorV进行合并计算
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()) {
			String name = itr.next();
			List<IndicatorV> list = map.get(name);
			IndicatorDef def = model.getIndicatorDefByName(name);
			if (def == null) {
				log.error("cannot get indicatorV definition for name \"{}\"", name);
				return null;
			}else {
				VALUES valuearray = new VALUES(def, list);
				
				IndicatorTS ts1 = new IndicatorTS(name, valuearray.ALL_TIME_TREND,valuearray.ALL_TIME_SURGE, VALUES.TIMESPAN_ALL_TIME);
				IndicatorTS ts2 = new IndicatorTS(name, valuearray.RECENT_TREND,valuearray.RECENT_SURGE, VALUES.TIMESPAN_RECENT);
				
				ts1.setSurgeValue(valuearray.ALL_TIME_SURGE_VALUE);
				ts2.setSurgeValue(valuearray.RECENT_SURGE_VALUE);
				
				VALUE recentValue = new VALUE(def, valuearray.recent.getValue());
				IndicatorS is = new IndicatorS(name, recentValue.STATE,  valuearray.recent.getTime());
				is.setSuspect(recentValue.ATTACH_PARENT);
				
				calcConditions.add(is);
				calcTS.add(ts1);
				calcTS.add(ts2);
			}
		}
		
		List<HuskyObject> inputs = new ArrayList<HuskyObject>();
		inputs.addAll(calcConditions);
		inputs.addAll(calcTS);
		return inputs;
	}

	public StackedMatchResult run(CaseDef casedef) {
		
		StackedMatchResult smr = new StackedMatchResult();
		
		WordStorageHelper wsh = new WordStorageHelper(model, CONST.NAMESPACE);
		String estr = wsh.checkCaseIntegriy(casedef);
		if(estr!=null && estr.length()>0) {
			smr.setContainsError(true);
			return smr;
		}
		
		CaseHelper helper = new CaseHelper();
		List<HuskyObject> conditions = helper.wrapper2Husky(casedef.getHuskys());
		
		
		List<DefRule> executedResult = new ArrayList<DefRule>();
		List<HuskyObject> generatedObjects = new ArrayList<HuskyObject>();
		
		List<MatchResult> preMatchResult = new ArrayList<MatchResult>();
		
		log.info("original inputs (not include generated ones) count is {}",conditions.size());
		
		if(conditions.size()>0)
			for(int i=0;i<conditions.size();i++) {
				log.info("original input No.{} is {}", (i+1), conditions.get(i).toString());
			}
		log.info("prepare all input objects for execution table");
		
		List<HuskyObject> fnParents = generateParents4FN(conditions, CONST.NAMESPACE);
		List<HuskyObject> fsParents = generateParents4FS(conditions, CONST.NAMESPACE);
		
		List<HuskyObject> calcs = calcTS(conditions);
		
		if (calcs == null) {
			smr.setContainsError(true);
			return smr;
		}
		
		List<HuskyObject> inputs = new ArrayList<HuskyObject>();
		inputs.addAll(conditions);
		inputs.addAll(calcs);
		inputs.addAll(fnParents);
		inputs.addAll(fsParents);
		
		
		log.info("........FIRE RULE ENGINE........");
		
		Object[] ret = model.getExecutor().run(inputs);
		
		executedResult.addAll((List<DefRule>)ret[0]);
		log.info("totally {} rule(s) are fired", executedResult.size());
		
		if(executedResult.size()>0) {
			for(int i=0;i<executedResult.size();i++) {
				log.info("fired rule No.{} {}", (i+1), executedResult.get(i).getName());
			}
		}
		
		KnowledgeBuilderErrors errors = (KnowledgeBuilderErrors)ret[1];
		if(errors!=null && errors.size()>0) {
			log.info("following {} error(s) occur during firing rules", errors.size());
			Iterator<KnowledgeBuilderError> iterator = errors.iterator();
			while(iterator.hasNext()) {
				KnowledgeBuilderError error = iterator.next();
				log.error("knowledge builder error: {}",error);
				smr.setContainsError(true);
			}
		}else {
			log.info("no knowledge builder error is found");
		}
		
		
		List<HuskyObject> facts = (List<HuskyObject>)ret[2];
		if(facts.size()>0) {
			for(int i=0;i<facts.size();i++) {
				if(inputs.indexOf(facts.get(i))<0) {
					generatedObjects.add(facts.get(i));
					log.info("generated HuskyObject \"{}\" is added to input for pre-match detection", facts.get(i));
				}
			}
		}
		
		inputs.addAll(generatedObjects);
		
		log.info("........PREMATCH........");
		
		try {
			if(inputs.size()>0) {
				RulePrematchRunner rm = new RulePrematchRunner();
				List<MatchResult> mrs = rm.detect(model, inputs, model.getRules(), model.createSession());
				preMatchResult.addAll(mrs);
			}
		}catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			smr.setContainsError(true);
		}
		
		smr.addMatchResult(preMatchResult);
		return smr;
	
	}
}
