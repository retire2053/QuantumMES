package qmes.rule.def;

import java.util.ArrayList;
import java.util.List;

public class DefLhs extends DefBase{

	private List<DefBase>    objects  = new ArrayList<DefBase>();
	
	public void addObject(DefBase object) {
		objects.add(object);
	}
	
	public List<DefBase> getObjectList(){return objects;}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\twhen\n");
		for(int i=0;i<objects.size();i++) {
			sb.append("\t\t"+objects.get(i).toString()+"\n");
		}
		return sb.toString();
	}
	
}
