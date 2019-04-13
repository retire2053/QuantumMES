package qmes.rule.def.lhs;

import java.util.ArrayList;
import java.util.List;

import qmes.model.HuskyObject;

/**
 * 这个类的作用是把一个DefRule的行，解释成为一个具体主谓宾列表（spolist)和一个husky对象的组合
 * 其作用是为了和输入的案例中的husky对象进行匹配和比较
 *
 */
public class LhsLine {
	
	public LhsLine() {
		spolist = new ArrayList<SPO>();
	}
	
	private List<SPO> spolist;
	private HuskyObject husky;
	public List<SPO> getSpolist() {
		return spolist;
	}
	public void setSpolist(List<SPO> spolist) {
		this.spolist = spolist;
	}
	public HuskyObject getHusky() {
		return husky;
	}
	public void setHusky(HuskyObject husky) {
		this.husky = husky;
	}
	
	public SPO findSPOInChain(String key) {
		if(spolist!=null && spolist.size()>0) {
			for(int i=0;i<spolist.size();i++) {
				if(spolist.get(i).subject.equals(key)) {
					return spolist.get(i);
				}
			}
		}
		return null;
	}
	
}
