package qmes.rule.search;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.compiler.lang.DRL5Expressions.relationalOp_return;

import qmes.core.Model;
import qmes.model.HuskyObject;
import qmes.rule.def.DefBase;
import qmes.rule.def.DefLhs;
import qmes.rule.def.DefRule;


public class RuleLhsResolve {

	private String clazz = null;
	private String lhsRaw = "";
	private String rhsRaw = "";
	private String featureName = null;
	private String featureState = null;
	private String lhsContent = null;
	private List<DefBase> dLhs;
	private List<HuskyObject> dRhs = null;
	
	public RuleLhsResolve(DefRule dRule,Model model) {
		this.dLhs = dRule.getLhs().getObjectList();
		this.dRhs = model.getRhsObject(dRule);
		//处理左侧
		this.dealLhs();
		//处理右侧
		this.dealRhs();
	}
	public String getClazz() {return clazz;}
	public String getLhsRaw() {return this.lhsRaw;}
	public String getRhsRaw() {return this.rhsRaw;}
	private void dealLhs() {
		for(int i1=0;i1<this.dLhs.size();i1++) {
			String lhs = this.dLhs.get(i1).toString();
			String pattern = "^(\\w+)\\((.*?)\\)";
			String expressionPattern = "\"(.*?)\"";
			Pattern p = Pattern.compile(pattern);
			Pattern ep = Pattern.compile(expressionPattern);
			Matcher m = p.matcher(lhs);
			if(m.find()) {
				this.clazz = m.group(1);
				Matcher eMatcher = ep.matcher(m.group(2));
				while (eMatcher.find()) {
	                this.lhsRaw += eMatcher.group(1);
				}
			}
		}
	}
	private void dealRhs() {
		String pattern = ";name=(.*?);state=(.*?);";
		for(int i=0; i<this.dRhs.size();i++) {
			System.out.println(this.dRhs.get(i));
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(this.dRhs.get(i).toString());
			if(m.find()) {
				this.rhsRaw += m.group(1)+m.group(2)+",";
			}
		}
	}
}
