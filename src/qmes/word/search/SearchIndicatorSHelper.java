package qmes.word.search;

import java.util.ArrayList;
import java.util.List;

import qmes.base.CONST;
import qmes.core.Model;
import qmes.indicator.calc.VALUES;
import qmes.indicator.def.IndicatorDef;
import qmes.indicator.def.RegionDef;
import qmes.model.IndicatorS;
import qmes.model.IndicatorTS;

public class SearchIndicatorSHelper {
	
	Model model = null;
	public SearchIndicatorSHelper(Model model) {
		this.model = model;
	}
	
	public List<WordSequence> createIndicatorS() {
		List<IndicatorDef> ids = model.getIndicatorDefs();
		List<WordSequence> wss = new ArrayList<WordSequence>();
		for (int i = 0; i < ids.size(); i++) {
			IndicatorDef id = ids.get(i);
			String name = id.getName();
			List<RegionDef> rds = id.getRegions();
			for (int t = 0; t < rds.size(); t++) {
				WordSequence ws = new WordSequence(CONST.NAMESPACE, IndicatorS.class.getName(), name,
						rds.get(t).getState(), name, rds.get(t).getState(), name + "_组");
				wss.add(ws);
			}
		}
		return wss;
	}
	
	public List<WordSequence> createIndicatorTS(){
		String[] trends = new String[] {VALUES.TREND_ASCEND, VALUES.TREND_DESCEND};
		String[] surge = new String[] {VALUES.SURGE_ABOVE_THRESHOLD, VALUES.SURGE_UNDER_THRESHOLD};
		String[] time = new String[] {VALUES.TIMESPAN_ALL_TIME, VALUES.TIMESPAN_RECENT};
		
		List<String> fss = new ArrayList<String>();
		for(int i=0;i<trends.length;i++) {
			for(int j=0;j<trends.length;j++) {
				for(int k=0;k<time.length;k++) {
					fss.add(trends[i]+"."+surge[j]+"."+time[k]);
				}
			}
		}
		
		List<IndicatorDef> ids = model.getIndicatorDefs();
		List<WordSequence> wss = new ArrayList<WordSequence>();
		for (int i = 0; i < ids.size(); i++) {
			IndicatorDef id = ids.get(i);
			String name = id.getName();
			for(int p=0;p<fss.size();p++) {
				WordSequence ws = new WordSequence(CONST.NAMESPACE, IndicatorTS.class.getName(), name,
						fss.get(p), name, fss.get(p), name + "_TS组");
				wss.add(ws);
			}
		}
		return wss;
		
	}
	
	public String createTSLHS(String fn, String state) {
		StringBuffer sb = new StringBuffer();
		sb.append(IndicatorTS.class.getSimpleName()+"(name==\""+fn+"\",");
		String[] s = state.split("\\.");
		sb.append("trend==\""+s[0]+"\",");
		sb.append("surge==\""+s[1]+"\",");
		sb.append("timespan==\""+s[2]+"\")");
		return sb.toString();
	}
	
	public String createTSRHS(String fn, String state) {
		StringBuffer sb = new StringBuffer();
		String[] s = state.split("\\.");
		sb.append("insert(new IndicatorTS(\""+fn+"\",\""+s[0]+"\",\""+s[1]+"\",\""+s[2]+"\"));");
		return sb.toString();
	}

}
