package qmes.rule.execution;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import husky.service.Utility;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.model.HuskyObject;
import qmes.rule.def.DefRule;

/**
 * 构建.drl文件的运行session，并将所有的input放入working memory中
 */
public class RuleMatchRunner {
	
	private static final Logger log = LoggerFactory.getLogger(RuleMatchRunner.class);
	
	Model model;
	public RuleMatchRunner(Model model) {
		this.model = model;
	}
	
	public Object[] run(List<HuskyObject> inputs) {
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		
		Object[] ret = new Object[3];
		
		File dir = new File(model.getRuleFileSubPath());
		File[] fs = dir.listFiles();
		for(int i=0;i<fs.length;i++) {
			if(fs[i].isFile() && fs[i].getName().toLowerCase().endsWith(".drl")) {
				kbuilder.add(ResourceFactory.newFileResource(fs[i]), ResourceType.DRL);
			    if (kbuilder.hasErrors()) {
			    		ret[1] = kbuilder.getErrors();
			    }
			}
			
		}
		
	    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
	    StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
	    
	    for(int i=0;i<inputs.size();i++) {
	    		session.insert(inputs.get(i));
	    }
	    
	    Utility.rules.clear();
	    
	    session.fireAllRules();
	    
	    List<DefRule> drs = new ArrayList<DefRule>();
	    if(Utility.rules.size()>0){
		    for(int i=0;i<Utility.rules.size();i++) {
	    			DefRule dr = model.getRuleByName(Utility.rules.get(i));
		    		if(dr!=null)drs.add(dr);
		    }
	    }
	    ret[0] = drs;
	    List<HuskyObject> generatedObjects = new ArrayList<HuskyObject>();
	    Iterator<FactHandle> itrfh = session.getFactHandles().iterator();
	    while(itrfh.hasNext()) {
	    		FactHandle fh = itrfh.next();
	    		Object object = session.getObject(fh);
	    		if(object!=null && object instanceof HuskyObject) {
		    		generatedObjects.add((HuskyObject)object);
	    		}
	    }
	    ret[2] = generatedObjects;
		return ret;
	}

}
