package qmes.word.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Word {

	private static final Logger log = LoggerFactory.getLogger(Word.class);
	
	private String english = null;
	
	private String comment = null;
	
	private String value = null;
	
	private String parent = null;
	
	private String tag = null;
	
	private String namespace = null;
	
	private String backupValue = null;
	
	public String getBackupValue() {
		return backupValue;
	}
	
	public void setBackupValue(String backupValue) {
		this.backupValue = backupValue;
	}
	
	public String getNamespace() {
		return namespace;
	}


	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}


	public String getTag() {
		return tag;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}

	private Set<String> children = new HashSet<String>();
	
//	private List<ClassPropertyDef> cpds = new ArrayList<ClassPropertyDef>();
	
	private List<String> affiliates = new ArrayList<String>();
	
	
	public Word() {}
	
	
	public String getParent() {return parent;}
	public void setParent(String w) {
		this.parent = w;
	}
	
	
	
	public String getEnglish() {return english;}
	public void setEnglish(String english) {this.english = english;}
	
	public String getComment() {return comment;}
	public void setComment(String comment) {this.comment = comment;}
	
	public String getValue() {return value;}
	public void setValue(String value) {this.value =value;}
	
	public void addAffiliate(String aff) {
		if(affiliates.indexOf(aff)>=0) {
			//do nothing so far
		}else {
			affiliates.add(aff);
		}
	}
	public void removeAffiliate(String aff) {
		int index = affiliates.indexOf(aff);
		if(index>=0)affiliates.remove(index);
		
	}
	public void clearAffiliates() {
		affiliates.clear();
	}
	public List<String> getAffiliates(){
		return affiliates;
	}
	
	public Set<String> getChildren(){
		return children;
	}
	public void setChildren(Set<String> children) {
		this.children = children;
	}


	public void setAffiliates(List<String> affiliates) {
		this.affiliates = affiliates;
	}

	public String toString() {
		return value;
	}
}
