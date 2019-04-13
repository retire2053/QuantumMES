package qmes.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AnnotationRule implements Serializable {

	private String document;
	private String rulename;
	private String meaning;
	private String documentName;
	
	public AnnotationRule() {}
	
	public String getMeaning() {
		if(meaning==null)return meaning;
		else return meaning;
	}
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	
	public String getRulename() {
		return rulename;
	}
	public void setRulename(String rulename) {
		this.rulename = rulename;
	}
	
	public String getDocument() {
		if(document!=null)return document;
		else return "";
	}
	
	public void setDocument(String document) {
		this.document = document;
	}
	
	private List<AnnotationLine> lines = new ArrayList<AnnotationLine>();
	
	public List<AnnotationLine> getLines(){
		return lines;
	}
	
	public void setLines(List<AnnotationLine> lines) {
		this.lines = lines;
	}
	public String getDocumentName() {
		if(documentName!=null)return documentName;
		else return "";
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

//	Map<Integer, AnnotationLine> map = new HashMap<Integer, AnnotationLine>();
//	
//	public void add(AnnotationLine line) {
//		map.put(line.getLhsindex(), line);
//		line.setParent(this);
//	}
//	
//	public List<AnnotationLine> getLines(){
//		int maxValue = -1;
//		Iterator<Integer> itr = map.keySet().iterator();
//		while(itr.hasNext()) {
//			Integer i = itr.next();
//			if(i.intValue()>maxValue)maxValue = i.intValue();
//		}
//		
//		List<AnnotationLine> lines = new ArrayList<AnnotationLine>();
//		if(maxValue>=0) {
//			for(int i=0;i<=maxValue;i++) {
//				lines.set(i, map.get(Integer.valueOf(i)));
//			}
//			return lines;
//		}
//		return lines;
//	}
//	
//	public AnnotationLine getLine(int i) {
//		AnnotationLine al = map.get(Integer.valueOf(i));
//		if(al==null) {
//			al = new AnnotationLine(i);
//			map.put(i, al);
//		}
//		return al;
//	}
	
}
