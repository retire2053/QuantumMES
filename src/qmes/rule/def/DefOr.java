package qmes.rule.def;

import java.util.ArrayList;
import java.util.List;

public class DefOr extends DefBase {

	public List<DefBase> objects = new ArrayList<DefBase>();
	
	public List<DefBase> getObjects() {
		return objects;
	}

	public void setObjects(List<DefBase> objects) {
		this.objects = objects;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<objects.size();i++) {
			if(i>0)sb.append(" or ");
			sb.append(objects.get(i).toString());
		}
		return sb.toString();
	}
}
