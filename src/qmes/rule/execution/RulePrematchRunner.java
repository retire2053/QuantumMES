package qmes.rule.execution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.annotation.AnnotationLine;
import qmes.config.storage.NConfigStorage;
import qmes.core.Model;
import qmes.model.HuskyObject;
import qmes.rule.def.DefBase;
import qmes.rule.def.DefNot;
import qmes.rule.def.DefObject;
import qmes.rule.def.DefOr;
import qmes.rule.def.DefRule;
import qmes.rule.execution.result.MatchResult;

public class RulePrematchRunner {
	
	private static final Logger log = LoggerFactory.getLogger(RulePrematchRunner.class);
	
	private double THRESHOLD = 0.5d;

	public List<MatchResult> detect(Model model, List<HuskyObject> conditions, List<DefRule> rules, StatefulKnowledgeSession session)throws Exception {
		
		log.info(" INTO core-code in pre-match method");
		log.info("clean all facts in temporary session, and then insert and fire");

		Iterator<FactHandle> tempitr = session.getFactHandles().iterator();
		while(tempitr.hasNext()) {
			FactHandle fh = tempitr.next();
			session.retract(fh);
		}
		
		for(int i=0;i<conditions.size();i++) session.insert(conditions.get(i));
		session.fireAllRules();
		
		log.info("find out how many temp partly rule is executed");

		Map<String, Integer> stringresults = new HashMap<String, Integer>();
		Iterator<FactHandle> itr = session.getFactHandles().iterator();
		while(itr.hasNext()) {
			FactHandle fh = itr.next();
			Object fact = session.getObject(fh);
			if(fact instanceof String) {
				String key = (String)fact;
				if(stringresults.containsKey(key)) {
					int v = stringresults.get(key);
					stringresults.put(key, v+1);
				}else {
					stringresults.put(key, 1);
				}
			}
		}
		
		Iterator<String> debugitr = stringresults.keySet().iterator();
		while(debugitr.hasNext()) {
			String key = debugitr.next();
			log.info("temp partly rule\"{}\" is executed for {} time(s)",key,stringresults.get(key));
		}
		
		log.info("start to generate all pre-match result");
		
		List<MatchResult> matchresults = new ArrayList<MatchResult>();
		for(int i=0;i<rules.size();i++) {
			DefRule rule = rules.get(i);
			
			log.info("start to generate pre-match result for \"{}\"", rule.getName());
			
			MatchResult mr = new MatchResult(model);
			mr.setRule(rule);
			
			for(int t=0;t<rule.getLhs().getObjectList().size();t++) {
				DefBase dbase = rule.getLhs().getObjectList().get(t);
				
				AnnotationLine al = model.getAnnotationRule(rule).getLines().get(t);
				mr.setTotalscore(mr.getTotalscore()+ al.getPower());
				
				String filename_rulename = rule.getName()+".temp."+(t+1)+".drl";
				if(dbase instanceof DefNot) {
					boolean local_matched = false;
					if(stringresults.keySet().contains(filename_rulename)) {
						mr.setValue(mr.getValue() + al.getPower());
						if (al.isNecessary())
							mr.addNecessaryFound(dbase);
						else
							mr.addUnnecessaryFound(dbase);
						local_matched = true;
					}
					if(!local_matched) {
						if(al.isNecessary()) {
							mr.addNecessaryNotFound(dbase);
						}
						else mr.addUnnecessaryNotFound(dbase);
					}
				}else if(dbase instanceof DefObject || dbase instanceof DefOr) {
					if(stringresults.keySet().contains(filename_rulename)) {
						mr.setValue(mr.getValue()+al.getPower());
						if (al.isNecessary())
							mr.addNecessaryFound(dbase);
						else
							mr.addUnnecessaryFound(dbase);
					}else {
						if(al.isNecessary()) {
							mr.addNecessaryNotFound(dbase);
						}
						else mr.addUnnecessaryNotFound(dbase);
					}
				}else{
					log.error("not implemented yet while in pre-match method, def base type={}", dbase.getClass().getSimpleName());
					throw new Exception("Not implemented yet");
				}
			}
			
			//根据Setting来调整行为
			boolean notShowNecessaryUnmatch = true;
			try {
				String v1 = model.getNConfigStorage().getSetting(NConfigStorage.KEY_NOT_SHOW_NECESSARY_UNMATCH);
				notShowNecessaryUnmatch = Boolean.valueOf(v1).booleanValue();
			}catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
			
			if(notShowNecessaryUnmatch && mr.getNecessaryNotFound().size()>0)continue;
			
			if(mr.getValue()>=0) {
				int minscore = 100;	//正常情况下是有相应的值的
				try {
					String v2 = model.getNConfigStorage().getSetting(NConfigStorage.KEY_MIN_SCORE);
					minscore = Integer.valueOf(v2);
				}catch(Exception ex) {
					ex.printStackTrace();
					log.error(ex.getMessage());
				}
				if(mr.getValue()>=minscore) {
					matchresults.add(mr);
				}
			}
			
//			if(mr.getTotalscore()>0) {
//				double d = (double)mr.getValue()/(double)mr.getTotalscore();
//				if(d>=THRESHOLD) {
//					matchresults.add(mr);
//				}
//			}
		}
		
		log.info("sort match results");
		
		matchresults.sort(new Comparator<MatchResult>() {
			public int compare(MatchResult o1, MatchResult o2) {
				if(o1.isMatched() && o2.isMatched()) {
					return o2.getValue()-o1.getValue();
				}else if(o1.isMatched() && !o2.isMatched()) {
					return -1;
				}else if(!o1.isMatched() && o2.isMatched()) {
					return 1;
				}else {
					return o2.getValue()-o1.getValue();
				}
			}
			
		});
		
		log.info("RETURN core-code in pre-match method");
		
		return matchresults;
		
	}




}
