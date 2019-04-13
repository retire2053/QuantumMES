package qmes.annotation.storage;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.annotation.AnnotationLine;
import qmes.annotation.AnnotationRule;
import qmes.base.StorageBase;
import qmes.rule.def.DefRule;

public class AnnotationStorage extends StorageBase{
	
	private static final Logger log = LoggerFactory.getLogger(AnnotationStorage.class);

	public AnnotationStorage(String basedir) {
		
		setBaseDir(basedir);
		
		init();
	}
	
	private void init() {
		log.info("start to load annoation storage file");
		loadStorage();
	}
	
	private Map<String, AnnotationRule> map = new HashMap<String, AnnotationRule>();

	protected void processFile(File file) {
		try {
			AnnotationRule ar = (AnnotationRule)this.file2object(file, AnnotationRule.class);
			map.put(ar.getRulename(), ar);
			log.info("Annotation Rules loadded from file {}", file.getAbsolutePath());
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}

	protected String getPattern() {return ".annotation";}

	protected void loadStorage() {
		traverse(new File(getBaseDir()), getPattern());
	}


	public void saveStorage() {
		log.info("start to store annoation to file");

		try {
			Iterator<AnnotationRule> itrs = map.values().iterator();
			while (itrs.hasNext()) {
				AnnotationRule ar = itrs.next();
				String p = getBaseDir() + java.io.File.separator + ar.getRulename() + getPattern();
				this.object2file(new File(p), ar);
				log.info("Annotation Rules written to file {}", p);
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

	}

	public AnnotationRule get(DefRule rule) {
		AnnotationRule r = null;
		if(map.containsKey(rule.getName())) {
			r = map.get(rule.getName());
		}else {
			r = new AnnotationRule();
			r.setRulename(rule.getName());
			map.put(rule.getName(), r);
		}
		alignRuleLHSCount(rule, r);
		return r;
	}
	
	private void alignRuleLHSCount(DefRule rule, AnnotationRule ar) {
		int rulecount = rule.getLhs().getObjectList().size();
		int arcount = ar.getLines().size();
		if(rulecount>arcount) {
			for(int i=0;i<rulecount-arcount;i++)ar.getLines().add(new AnnotationLine());
		}else if(rulecount<=arcount) {
			//donothing
		}
	}

	@Override
	protected void deleteStorage(String filename) {
		// TODO 自动生成的方法存根
		
	}

}
