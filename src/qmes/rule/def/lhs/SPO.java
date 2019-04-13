package qmes.rule.def.lhs;

import java.util.ArrayList;
import java.util.List;

public class SPO{
	
	public String subject;
	public String predicate;
	public String object;
	
	public static String PREDICATE_EQUALS = "==";
	public static String PREDICATE_NOT_EQUALS = "!=";
	public static String PREDICATE_NOT_EQUALS_2 = "<>";
	public static String PREDICATE_LARGER_OR_EQUALS = ">=";
	public static String PREDICATE_LESSER_OR_EQUALS = "<=";
	public static String PREDICATE_LARGER = ">";
	public static String PREDICATE_LESSER = "<";
	
	public static String OR = "||";
	
	//这些谓词的顺序重要，靠前的需要包含靠后的，比如>=需要在>之前
	//这是因为for-break的写法。否则会造成bug
	public static String[] predicates = new String[] {
			PREDICATE_EQUALS,
			PREDICATE_LARGER_OR_EQUALS,
			PREDICATE_LESSER_OR_EQUALS,
			PREDICATE_NOT_EQUALS,
			PREDICATE_NOT_EQUALS_2,
			PREDICATE_LARGER,
			PREDICATE_LESSER,
	};
	
	public static String replaceQuotationMarks(String s) {
		String s1 = s.trim();
		s1 = s1.replace("\"", "");
		return s1;
	}
	
	public static List<SPO> cut(String expr) {
		List<SPO> spos = new ArrayList<SPO>();
		for(int i=0;i<predicates.length;i++) {
			int index = expr.indexOf(predicates[i]);
			if(index>=0) {
				String head = expr.substring(0, index);
				
				//按照||来分割多段的谓词可能性
				String[] conditions = expr.substring(index).split("\\|\\|");
				if(conditions.length>0) {
					for(int p=0;p<conditions.length;p++) {
						
						for(int k=0;k<predicates.length;k++) {
							int id = conditions[p].indexOf(predicates[k]);
							if(id>=0) {
								String predicate = conditions[p].substring(id, id+predicates[p].length());
								String object = conditions[p].substring(id+predicates[p].length());
								SPO spo = new SPO();
								spo.subject = replaceQuotationMarks(head);
								spo.predicate = predicate;
								spo.object = replaceQuotationMarks(object);
								spos.add(spo);
								break;
							}
						}
					}
				}
				break;
			}
		}
		return spos;
	}
	
}