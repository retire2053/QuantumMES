package qmes.cases.def;

import java.util.ArrayList;
import java.util.List;

import qmes.indicator.def.Base;

public class CaseDef  implements Base {
	
	//案例名称
	private String name;
	//案例
	private List<HuskyWrapper> huskys = new ArrayList<HuskyWrapper>();
	//案例标签
	private String[] tags;

	private String document;
	
	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	//案例备注
	private String remarks;
	
	public String getName() {
		return name;
	}

	public List<HuskyWrapper> getHuskys() {
		return huskys;
	}

	public void setHuskys(List<HuskyWrapper> huskys) {
		this.huskys = huskys;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String toString() {
		return name;
		
	}

	public void remove(CaseDef id) {
		// TODO 自动生成的方法存根
		
	}
	
}