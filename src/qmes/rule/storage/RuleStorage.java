package qmes.rule.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.StorageBase;
import qmes.rule.def.DefGlobal;
import qmes.rule.def.DefImport;
import qmes.rule.def.DefPackage;
import qmes.rule.def.DefRule;

public class RuleStorage extends StorageBase {

	private static final Logger log = LoggerFactory.getLogger(RuleStorage.class);
	
	private String ruleTempPath = null;
	
	public RuleStorage(String rulefilepath, String ruleTempPath) {
		
		log.info("construct RuleStorage object");
		
		this.ruleTempPath = ruleTempPath;
		setBaseDir(rulefilepath);
		
		init();
	}
	
	public void init() {
		
		log.info("prepare const map for detectting RHS objects");
		
		loadStorage();
	}
	
	public List<DefRule> getRules(){
		List<DefRule> rules = new ArrayList<DefRule>();
		for (int i = 0; i < packages.size(); i++) {
			rules.addAll(packages.get(i).getRules());
		}
		return rules;
	}
	
	List<DefPackage> packages = new ArrayList<DefPackage>();
	Loader li = new Loader();
	
	public void loadStorage() {
		packages.clear();
		traverse(new File(getBaseDir()), getPattern());

		try {
			for (int i = 0; i < packages.size(); i++) {
				log.info("after traversing, package named \"{}\" with path {} rule loadded", packages.get(i).getName(),packages.get(i).getFilepath());
				List<DefRule> rules = packages.get(i).getRules();
				for (int k = 0; k < rules.size(); k++) {
					log.info("there is a rule named \"{}\" in package \"{}\"", rules.get(k).getName(),packages.get(i).getName());
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	protected void processFile(File file) {
		try {
			DefPackage dp = li.generatePackage(file.getAbsolutePath());
			dp.setFilepath(file.getAbsolutePath());
			packages.add(dp);
			log.info("while travering, load {}", file.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}

	protected String getPattern() {return ".drl";}
	public void saveStorage() {}

	public void traverseFolder(String path, List<DefPackage> packages) throws Exception {
		Loader li = new Loader();
		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						traverseFolder(file2.getAbsolutePath(), packages);
					} else {
						
					}
				}
			}
		}
	}

	private KnowledgeBase kbase = null;
	
	//用来生成一个rule的拆分结果
	public void generateTempDrlOnPath(List<DefRule> rules)throws Exception {
		
		log.info("start to generate temporary partly .drl files for prematch analysis");
		
		File f= new File(ruleTempPath);
		if (f.exists() && f.isDirectory()) {
			File[] fs = f.listFiles();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].isFile())
					fs[i].delete();
			}
			log.info("clean files in {}",ruleTempPath);
		} else {
			throw new Exception("Error: " + ruleTempPath + " is not a valid directory.");
		}
		
		
		log.info("start to create internal knowledge builder");
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		log.info("finish creating internal knowledge builder, now start to generate temp partly .drl");
		
		for(int t=0;t<rules.size();t++) {
			DefRule rule = rules.get(t);
			DefPackage pkg = (DefPackage)rule.getParent();
			
			log.info("start to generate temporary partly .drl for rule \"{}\"",rule.getName());
			
			StringBuffer common = new StringBuffer();
			common.append("package temp."+pkg.getName()+";\n");
			if(pkg.getImportList().size()>0)
				for(int i=0;i<pkg.getImportList().size();i++) {
					DefImport di = pkg.getImportList().get(i);
					common.append(di.toString()+";\n");
				}
			
			if(pkg.getGlobalList().size()>0)
				for(int i=0;i<pkg.getGlobalList().size();i++) {
					DefGlobal dg = pkg.getGlobalList().get(i);
					common.append(dg.toString()+";\n");
				}
			
			
			for(int i=0;i<rule.getLhs().getObjectList().size();i++) {
				
				log.info("process No.{} line in rule \"{}\",step.1 write temp drl to persistence",(i+1) ,rule.getName());
				//这个字符串在RuleMatcher中会用到，作为检查
				String filename_rulename = rule.getName()+".temp."+(i+1)+".drl";
				
				StringBuffer sb = new StringBuffer();
				sb.append(common.toString());
				sb.append("rule \""+filename_rulename+"\"\n");
				sb.append("\twhen\n");
				sb.append("\t\t"+rule.getLhs().getObjectList().get(i).toString()+"\n");
				sb.append("\tthen\n");
				sb.append("\t\tString symbol=\""+filename_rulename+"\";\n");
				sb.append("\t\tinsert(symbol);\n");
				sb.append("\t\tSystem.out.println(symbol);\n");
				sb.append("end\n");
				
				String filepath = ruleTempPath+ java.io.File.separator +filename_rulename;
				File tempfile = new File(filepath);
				FileOutputStream fos = new FileOutputStream(tempfile);
				fos.write(sb.toString().getBytes());
				fos.close();
				
				log.info("process No.{} line in rule \"{}\",step.2 add temp dir to knowledge builder",(i+1) ,rule.getName());
				
			    kbuilder.add(ResourceFactory.newFileResource(tempfile), ResourceType.DRL);
			    if (kbuilder.hasErrors()) {
			    		log.error("Errors: in package={}, rule={}, No.={}, info={}",pkg.getName(),rule.getName(),i,kbuilder.getErrors());
			    }
			    
			    log.info("process No.{} line in rule \"{}\",step.2 finished",(i+1) ,rule.getName());
			}
		}
		
		log.info("start to create knowledge base");
		kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		log.info("all knowledge builder packages are added to knowledge base");
		
	}
	
	public StatefulKnowledgeSession createSession() {
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
	    return ksession;
	}

	@Override
	protected void deleteStorage(String filename) {
		// TODO 自动生成的方法存根
		
	}
	
	
}
