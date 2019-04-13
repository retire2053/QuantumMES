package qmes.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.annotation.AnnotationRule;
import qmes.annotation.storage.AnnotationStorage;
import qmes.base.CONST;
import qmes.base.LogUtil;
import qmes.cases.def.CaseDef;
import qmes.cases.storage.CaseStorage;
import qmes.config.storage.NConfigStorage;
import qmes.indicator.def.IndicatorDef;
import qmes.indicator.storage.IndicatorStorage;
import qmes.model.HuskyObject;
import qmes.nlp.storage.MatchableStorage;
import qmes.rule.def.DefRule;
import qmes.rule.execution.RuleMatchRunner;
import qmes.rule.execution.emulate.ConsequenceEmulator;
import qmes.rule.search.RuleIndex;
import qmes.rule.search.RuleSearch;
import qmes.rule.storage.RuleStorage;
import qmes.word.search.Searcher;
import qmes.word.search.TrinityIndexer;
import qmes.word.search.WordIndexer;
import qmes.word.storage.FeatureNameStorage;
import qmes.word.storage.FeatureStateStorage;
import qmes.word.storage.WordStorage;

public class Model implements CONST {
	
	private static final Logger log = LoggerFactory.getLogger(Model.class);
	
	public Model(String basedir) {
		try {
			log.info("construct Model object, which storge all entries of internal objects.");
			
			this.basedir = basedir;
			
			init();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public String getBaseDir() {
		return basedir;
	}
	
	private LogUtil logUtil = null;
	
	public LogUtil getLogUtil() {
		return logUtil;
	}
	
	private String basedir = null;
	
	public String getIndicatorDefSubPath() {return basedir +java.io.File.separator +INDICATOR_DEF_SUB_PATH;}
	public String getRuleAnnotationSubPath() {return basedir +java.io.File.separator +RULE_ANNOTATION_SUB_PATH;}
	public String getRuleFileSubPath() {return basedir +java.io.File.separator +RULE_FILE_SUB_PATH;}
	public String getRuleTempSubPath() {return basedir +java.io.File.separator +RULE_TEMP_SUB_PATH;}
	public String getNamespaceStorageSubPath() {return basedir +java.io.File.separator +NAMESPACE_STORAGE_SUB_PATH;}
	public String getConsequenceEmulatorSubPath() {return basedir +java.io.File.separator +CONSEQUENCE_EMULATOR_TEMP_SUB_PATH;}
	public String getCaseSubPath() {return basedir +java.io.File.separator +CASE_SUB_PATH;}
	public String getSearchTempSubPath() { return basedir +java.io.File.separator +SEARCH_TEMP_SUB_PATH;}
	public String getSearchSingleWordTempSubPath() { return basedir +java.io.File.separator +SEARCH_SINGLEWORD_SUB_PATH;}
	public String getRuleSearchTempSubPath() { return basedir +java.io.File.separator +RULE_SEARCH_TEMP_SUB_PATH;}
	public String getNLPSubPath() {return basedir + java.io.File.separator + NLP_SUB_PATH;}
	
	
	
	
	private Searcher searcher = null;
	private TrinityIndexer indexer = null;
	private WordIndexer wordIndexer = null;
	
	private RuleSearch rSearcher = null;
	private RuleIndex rIndex = null;
	
	private MatchableStorage ms = null;
	public MatchableStorage getMatchableStorage() {
		return ms;
	}
	
	private NConfigStorage ns = null;
	public NConfigStorage getNConfigStorage() {
		return ns;
	}
	
	private CaseStorage cases = null;
	
	public CaseStorage getCaseStorage() {
		return cases;
	}
	
	private Map<String, WordStorage> wordStorages = new HashMap<String, WordStorage>();
	
	public WordStorage getWordStorage(String namespace, String word_storage_type) {
		String key = namespace+"."+word_storage_type;
		if(wordStorages.containsKey(key)) {
			return wordStorages.get(key);
		}
		return null;
	}

	private AnnotationStorage as =  null;
	
	public AnnotationRule getAnnotationRule(DefRule rule) {
		return as.get(rule);
	}
	
	private ConsequenceEmulator emulator = null;
	private RuleMatchRunner executor = null;
	private RuleStorage rulestorage = null;
	
	private List<DefRule> rules = new ArrayList<DefRule>();
	private DefRule currentRule = null;
	private IndicatorStorage indicatorstorage = null;
	private HashMap<String, String> ruleknowledge = new HashMap<String,String>();
	
	public StatefulKnowledgeSession createSession() {
		return rulestorage.createSession();
	}
	
	public String getKnowledge(String rulename) {
		String k = ruleknowledge.get(rulename);
		if(k==null)return "";
		else return k;
	}
	
	public List<DefRule> getRules(){
		return rules;
	}
	
	public DefRule getRuleByName(String name) {
		for(int i=0;i<rules.size();i++) {
			if(rules.get(i).getName().equals(name)) {
				return rules.get(i);
			}
		}
		return null;
	}
	
	public void removeIndicatorDef(IndicatorDef indicatordef) {
		indicatorstorage.getIndicatorDefs().remove(indicatordef);
		indicatorstorage.deleteStorage(indicatordef.getName());
		
	}
	
	public void addIndicatorDef(IndicatorDef indicatordef) {
		indicatorstorage.getIndicatorDefs().add(indicatordef);
	}
	
	public List<IndicatorDef> getIndicatorDefs(){
		return indicatorstorage.getIndicatorDefs();
	}
	
	public IndicatorDef getIndicatorDefByName(String name) {
		return indicatorstorage.find(name);
	}
	
	public IndicatorDef getIndicatorDefByName(String name, String unit) {
		return indicatorstorage.find(name, unit);
	}
	
	public void storeIndicatorDefs()throws Exception {
		indicatorstorage.saveStorage();
	}
	
	public void storeRuleDefs(){
		as.saveStorage();
	}
	
	public void setCurrentRule(DefRule rule) {
		this.currentRule = rule;
	}
	public DefRule getCurrentRule() {
		return currentRule;
	
	}

	//存储患者案例
	public void addCaseDefs(CaseDef conditions)throws Exception {
		cases.getCases().add(conditions);
	}
		
	//存储患者案例
	public void storeCaseDefs()throws Exception {
		cases.saveStorage();
	}
	//患者案例列表
	public List<CaseDef> getCaseList(){
		return cases.getCases();
	}
	
	//根据案例名称返回一条数据
	public CaseDef getConditionByName(String name) {
		for(int i=0;i<getCaseList().size();i++) {
			if(getCaseList().get(i).getName().equals(name)) {
				return getCaseList().get(i);
			}
		}
		return null;
	}
	public RuleMatchRunner getExecutor() {
		return executor;
	}
	
	public ConsequenceEmulator getEmulator() {
		return emulator;
	}

	private void init() throws Exception {
		
		logUtil = new LogUtil();
		
		log.info("const definition: namespace path={}",getNamespaceStorageSubPath());
		log.info("const definition: rule temp path={}" ,getRuleTempSubPath());
		log.info("const definition: indicator def path={}", getIndicatorDefSubPath());
		log.info("const definition: rule annotation path={}",getRuleAnnotationSubPath());
		log.info("const definition: rule file path={}",getRuleFileSubPath());
		log.info("const definition: consequence emulator temp path={}",getConsequenceEmulatorSubPath());
		log.info("const definition: case path={}",getCaseSubPath());
		
		
		log.info("initialize case storage");
		
		cases = new CaseStorage(getCaseSubPath());
		
		log.info("initialize namespace storage");
		
		log.info("initialize word storage");
		FeatureNameStorage featureNames = new FeatureNameStorage(basedir + java.io.File.separator + WORD_STORAGE_SUB_PATH + java.io.File.separator + CONST.NAMESPACE + java.io.File.separator+ TYPE_FEATURE_NAMES );
		FeatureStateStorage featureStates = new FeatureStateStorage(basedir + java.io.File.separator + WORD_STORAGE_SUB_PATH + java.io.File.separator + CONST.NAMESPACE + java.io.File.separator+ TYPE_FEATURE_STATES );
		
		wordStorages.put(CONST.NAMESPACE+"."+TYPE_FEATURE_NAMES,  featureNames);
		wordStorages.put(CONST.NAMESPACE+"."+TYPE_FEATURE_STATES,  featureStates);
		
		log.info("const definition: feature names path={} with namespace = {}",featureNames.getBaseDir(), CONST.NAMESPACE);
		log.info("const definition: feature names path={} with namespace = {}",featureStates.getBaseDir(), CONST.NAMESPACE);

		log.info("initialize executor");
		executor = new RuleMatchRunner(this);
		
		log.info("initialize consequence emulator");

		emulator = new ConsequenceEmulator(getConsequenceEmulatorSubPath());
		
		log.info("initialize annotation storage");
		
		as = new AnnotationStorage(getRuleAnnotationSubPath());
		
		log.info("initialize Matchable & NConfig Stroage");
		
		ms = new MatchableStorage(getNLPSubPath());
		ns = new NConfigStorage(getNLPSubPath());
		
		log.info("initialize rule storage");
		
		//加载规则
		loadRuleList();
		log.info("initialize indicator definition storage");
		
		indicatorstorage = new IndicatorStorage(getIndicatorDefSubPath());
		
		log.info("initialize search engine for wordnet");
		
		indexer = new TrinityIndexer(this, getSearchTempSubPath());
		if(indexer!=null) {
			indexer.deleteAll();
			indexer.createIndex();
		}
		
		wordIndexer = new WordIndexer(this, getSearchSingleWordTempSubPath());
		if(wordIndexer!=null) {
			wordIndexer.deleteAll();
			wordIndexer.createIndex();
			
		}
		
		log.info("initialize search engine for rule");
		rIndex = new RuleIndex(this, getRuleSearchTempSubPath());
		
		if(rIndex!=null) {
			rIndex.deleteAll();
			rIndex.createIndex();
		}

		initializeTimer();
		
	}
	public void loadRuleList() {
		rulestorage = new RuleStorage(getRuleFileSubPath(), getRuleTempSubPath());
		rulestorage.loadStorage();
		rules = rulestorage.getRules();
		/*if(rules.size()>0) {
			log.info("after assembling DefRules, start to set emulated RHS object for each rule");
			
			for(int i=0;i<rules.size();i++) {
				DefRule rule = rules.get(i);
				List<HuskyObject> emulation = emulator.emulate(rule);
				if(emulation!=null) {
					rule.getRhsObject().addAll(emulation);
				}
			}
		}*/

		log.info("generate temporary partly .drl file");
		
		try {
			rulestorage.generateTempDrlOnPath(rules);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	public List<HuskyObject> getRhsObject(DefRule rule) {
		List<HuskyObject> emulation = emulator.emulate(rule);
		if(emulation!=null) {
			return emulation;
		}
		return null;
	}
	
	public void saveAll() {
		
		log.info("save all storages at a batch");
		
		Iterator<WordStorage> wsitr = wordStorages.values().iterator();
		while(wsitr.hasNext()) {
			WordStorage ws = wsitr.next();
			if(ws!=null) {
				ws.saveStorage();
			}
		}
		
		indicatorstorage.saveStorage();
		rulestorage.saveStorage();
		as.saveStorage();
		cases.saveStorage();
		
	}
	public void saveAnnotationRule() {
		as.saveStorage();
	}
	
	private void initializeTimer() {
		
		log.info("schedule a 5-minute task to save all storages");
		
		Timer timer = new Timer();
		long fiveminute = 5*60*1000;
		timer.schedule(new TimerTask() {

			public void run() {
				try {
					Model.this.saveAll();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		},fiveminute ,  fiveminute );
	}
}
