package qmes.rule.execution.result;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.annotation.AnnotationLine;
import qmes.annotation.AnnotationRule;
import qmes.base.BASEUI;
import qmes.config.storage.NConfigStorage;
import qmes.core.Model;
import qmes.model.HuskyObject;
import qmes.nlp.ui.MatchableHelper;
import qmes.rule.def.DefBase;
import qmes.rule.def.DefRule;

public class RuleExplanation {
	
	private static final Logger log = LoggerFactory.getLogger(RuleExplanation.class);
	
	private Model model = null;
	private MatchableHelper matchableHelper = null;
	
	public RuleExplanation(Model model) {
		this.model = model;
		matchableHelper = new MatchableHelper(model);
	}
	
	///////////////////////////////////////////////////////////////////
	// 整个执行结果的html解释
	///////////////////////////////////////////////////////////////////
	
	public String showStackedMatchResult(StackedMatchResult smr) {
		
		log.info("show USER-style stacked match result");
		
		ExplainUnmatch eu = new ExplainUnmatch();
		
		StringBuffer sb = new StringBuffer();
		String according = "根据医生的逻辑推断，给与您一下建议:";
		String according_2 = "根据医生的逻辑推断，以下建议最接近您的病情，如果您提供更多信息，可能会给与您更精确的建议:";
		String prematch_all_head = "另外，如果您能提供更多信息，或近期您的的病情发生变化，以下建议可能会对您有帮助";
		String reason = "因为";
		
		sb.append("<font size=4>");
		
		if(smr.getTriggered().size()==0 && smr.getPrematch().size()==0 && smr.getTagMatch().size()==0) {
			//当没有任何匹配结果的时候，显示设置中的默认值
			String defaultText = model.getNConfigStorage().getSetting(NConfigStorage.KEY_DEFAULT_TEXT);
			sb.append(defaultText);
		}else {
			
			List<MatchResult> triggered = smr.getTriggered();
			
			if(triggered.size()>0) {
				sb.append(according+br());
				for(int i=0;i<triggered.size();i++) {
					MatchResult mr = triggered.get(i);
					DefRule rule = mr.getRule();
					sb.append(tab()+appendIndex(i)+space()+appendRHS(rule)+br());
				}
				sb.append(reason+br());
				for(int i=0;i<triggered.size();i++) {
					MatchResult mr = triggered.get(i);
					DefRule rule = mr.getRule();
					AnnotationRule ar = model.getAnnotationRule(mr.getRule());
					sb.append(tab()+appendIndex(i)+space());
					for(int p=0;p<rule.getLhs().getObjectList().size();p++) {
						sb.append(  appendExplanation(ar.getLines().get(p)));
						sb.append(space());
					}
					sb.append(appendMeaning(ar));
					sb.append(br());
				}
				sb.append(br());
				sb.append(prematch_all_head+br());
				
			}else {
				sb.append(according_2+br());
			}
			
			String prematch_case_head = "如果以下情况全部发生:";
			String prematch_case_then = "那么将给与建议";
			
			for(int i=0;i<smr.getPrematch().size();i++) {
				MatchResult mr = smr.getPrematch().get(i);
				DefRule rule = mr.getRule();
				sb.append(tab()+appendIndex(i)+space()+prematch_case_head+br());
				
				List<DefBase> deflist = new ArrayList<DefBase>();
				deflist.addAll(mr.getNecessaryNotFound());
				deflist.addAll(mr.getUnnecessaryNotFound());
				
				if(deflist.size()>0) {
					for(int k=0;k<deflist.size();k++) {
						DefBase dbase =deflist.get(k);
						String lhsexpr = matchableHelper.translateLHS(dbase, eu);
						sb.append(tab()+tab()+ String.valueOf(i+1)+"."+String.valueOf(k+1)+lhsexpr+br());
					}
				}
				
				sb.append(tab()+prematch_case_then+appendRHS(rule)+space()+appendRuleName(rule.getName()));
				sb.append(br()+br());
			}
		}
		
		
		
		sb.append("</font>");
		
		return sb.toString();
	}
	
	private String appendIndex(int i) {
		return "[<b>"+(i+1)+"</b>]";
	}
	
	private String appendRHS(DefRule rule) {
		StringBuffer sb = new StringBuffer();
		List<HuskyObject> results = rule.getRhsObject();
		if(results==null || results.size()==0) {
			List<HuskyObject> hos = model.getRhsObject(rule);
			rule.setRhsObject(hos);
		}
		
		results = rule.getRhsObject();
		if(results!=null && results.size()>0) {
			for(int p=0;p<results.size();p++) {
				sb.append("<font color=red><b>"+matchableHelper.translateHuskyObject(results.get(p))+"</b></font>");
				if(p<results.size()-1)sb.append(comma());
			}
		}else {
			sb.append("<font color=red>[NO INFER]&nbsp;</font>");
		}
		return sb.toString();
	}
	
	private String appendExplanation(AnnotationLine al) {
		StringBuffer sb = new StringBuffer();
		if(al==null || al.getExplanation()==null || al.getExplanation().length()==0) {
			sb.append("<font color=blue>[NO EXPLANATION]&nbsp;</font>");
		}else {
			sb.append("<font color=blue><b>"+al.getExplanation()+"</b></font>");
		}
		return sb.toString();
	}
	
	private String appendMeaning(AnnotationRule ar) {
		StringBuffer sb = new StringBuffer();
		if(ar==null || ar.getMeaning()==null || ar.getMeaning().length()==0) {
			sb.append("<font color=green>[NO MEANING]&nbsp;</font>");
		}else {
			sb.append("<font color=green><b>"+ar.getMeaning()+"</b></font>");
		}
		sb.append(space() + appendRuleName(ar.getRulename()) + br());
		return sb.toString();
	}
	
	private String appendRuleName(String rulename) {
		return "[<b>"+rulename+"</b>]";
	}
	

	///////////////////////////////////////////////////////////////////
	// 单个Rule的html格式的解释
	///////////////////////////////////////////////////////////////////
	
	public String showRuleWithAnnotation(DefRule rule, MatchResult mr) {
		AnnotationRule ar = model.getAnnotationRule(rule);
		StringBuffer sb = new StringBuffer();
		sb.append("rule \"" + rule.getName() + "\"<br>");
		sb.append("<font color=red size=3>[解释="+ar.getMeaning()+"]</font><br>");
		if (rule.getSalience() != null)
			sb.append(BASEUI.nbsp(4)+"salience " + rule.getSalience()+"<br>");
		sb.append(BASEUI.nbsp(4)+"when<br>");
		for(int i=0;i<rule.getLhs().getObjectList().size();i++) {
			DefBase dbase = rule.getLhs().getObjectList().get(i);
			AnnotationLine al = ar.getLines().get(i);
			
			if(mr!=null && (mr.getNecessaryFound().indexOf(dbase)>=0 || mr.getUnnecessaryFound().indexOf(dbase)>=0)) {
				sb.append(BASEUI.nbsp(8)+ "<font color=green size=4>"+ dbase.toString() + "</font><br>");
			}else {
				sb.append(BASEUI.nbsp(8)+ "<font color=blue size=4>"+ dbase.toString() + "</font><br>");
			}
			
			sb.append(BASEUI.nbsp(10)+"<font color=red size=3>" + 
					(al.isNecessary()?"[必须]":"[非必须]&nbsp;") + 
					"[权重="+al.getPower()+"]&nbsp;"+ 
					"[解释=" +al.getExplanation() +"]"+
					"</font><br>");
			sb.append("<br>");
		}

		sb.append(BASEUI.nbsp(8)+"then<br>");
		String consequence = rule.getConsequence().toString();
		consequence.replaceAll("\n","<br>");
		consequence.replaceAll("\t",BASEUI.nbsp(4));
		sb.append(consequence);
		sb.append("end<br>");
		
		sb.append("<br><br>文档:<font color=red size=3>");
		String doc = ar.getDocument();
		doc = doc.replaceAll("\n","<br>");
		doc = doc.replaceAll("\t",BASEUI.nbsp(4));
		sb.append(doc);
		sb.append("</font>");
		return sb.toString();
		
	}
	
	///////////////////////////////////////////////////////////////////
	// html输出时候的一些固定函数
	///////////////////////////////////////////////////////////////////

	private String tab() {
		return BASEUI.nbsp(6);
	}
	
	private String br() {
		return "<br>";
	}
	
	private String space() {
		return "&nbsp;";
	}
	
	private String comma() {
		return ";";
	}
}
