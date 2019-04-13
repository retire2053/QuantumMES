package husky.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import qmes.model.IndicatorS;

public class IndicatorService implements Comparator<IndicatorS>{
	
	HashMap<String, List<IndicatorS>> map = new HashMap<String, List<IndicatorS>>();
	
	
	


	@Override
	public int compare(IndicatorS o1, IndicatorS o2) {
		double t1 = o1.getTime();
		double t2 = o2.getTime();
		return (int)(t2-t1);
	}

	public int getIndicatorCount(String name) {
		List<IndicatorS> v = map.get(name);
		if(v==null)
		{
			return 0;
		}else {
			return v.size();
		}
	}
	
	public int logHBVDNA(float value) {
		return (int)(Math.log(value) / Math.log(10));
	}

}
