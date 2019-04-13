package qmes.indicator.calc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import qmes.indicator.def.IndicatorDef;
import qmes.model.IndicatorV;

public class VALUES {
	public int COUNT = 0;
	public int RECENT_COUNT = 0;
	
	public String ALL_TIME_TREND = null;
	public String ALL_TIME_SURGE = null;
	public String RECENT_TREND = null;
	public String RECENT_SURGE = null;
	
	public double ALL_TIME_SURGE_VALUE = 0;
	public double RECENT_SURGE_VALUE = 0;
	
	public static String NO_DATA = "数据不足";
	public static String TREND_ASCEND = "趋势上升";
	public static String TREND_DESCEND = "趋势下降";
	public static String SURGE_ABOVE_THRESHOLD = "大波动";
	public static String SURGE_UNDER_THRESHOLD = "小波动";
	
	public static String TIMESPAN_RECENT = "最近";
	public static String TIMESPAN_ALL_TIME = "所有时间";

	
	public IndicatorV recent = null;
	
	private IndicatorDef def;
	private List<IndicatorV> values;
	
	public VALUES(IndicatorDef def, List<IndicatorV> values) {
		this.def = def;
		this.values = values;
		
		COUNT = values.size();
		
		values.sort(new Comparator<IndicatorV>() {
			public int compare(IndicatorV o1, IndicatorV o2) {
				if(o1.getTime() > o2.getTime()) return 1;
				else if(o1.getTime() == o2.getTime()) return 0;
				else return -1;
			}
		});
		
		recent = values.get(values.size()-1);
		
		calcvalue(values, true);
		
		List<IndicatorV> subvalues = new ArrayList<IndicatorV>();
		double maxtime = recent.getTime();
		
		for(int i=0;i<values.size();i++) {
			if(values.get(i).getTime()-maxtime>=def.getRecentDef().getRecent()) {
				subvalues.add(values.get(i));
			}
		}
		
		RECENT_COUNT = subvalues.size();
		calcvalue(subvalues, false);
		
	}
	
	private void calcvalue(List<IndicatorV> indicators, boolean alltime) {
		
		if(indicators.size()<2) {
			if(alltime) {
				ALL_TIME_TREND = NO_DATA;
				ALL_TIME_SURGE = "";
			}else {
				RECENT_TREND = NO_DATA;
				RECENT_SURGE = "";
			}
		}else {
			RegressionLine line = new RegressionLine();  
			for(int i=0;i<indicators.size();i++){
				line.addDataPoint(new DataPoint(indicators.get(i).getTime(),  indicators.get(i).getValue()));
			}
	        if(line.getA1()>=0) {
	        		if(alltime)ALL_TIME_TREND = TREND_ASCEND;
	        		else RECENT_TREND = TREND_ASCEND;
	        }
	        else {
	        		if(alltime)ALL_TIME_TREND = TREND_DESCEND;
	        		else RECENT_TREND = TREND_DESCEND;
	        }
	        
	        double[] vs = new double[values.size()];
	        for(int i=0;i<vs.length;i++) {
	        		vs[i] = values.get(i).getValue();
	        }
	        
			double m = mutation(vs);
			m = (double) Math.round(m * 100) / 100;
			
			if (alltime) {
				ALL_TIME_SURGE_VALUE = m;
			} else {
				RECENT_SURGE_VALUE = m;
			}
	        if(m>=def.getSurge().getSurge()) {
	        		if(alltime)ALL_TIME_SURGE = SURGE_ABOVE_THRESHOLD;
	        		else RECENT_SURGE = SURGE_ABOVE_THRESHOLD;
	        }
	        else {
	        		if(alltime)ALL_TIME_SURGE = SURGE_UNDER_THRESHOLD;
	        		else RECENT_SURGE = SURGE_UNDER_THRESHOLD;
	        }
			
		}
		
	}
	
	public static double mutation(double[] values) {
		double total = 0;
		
		for(int i=0;i<values.length;i++) {
			total += values[i];
		}
		double average = total/values.length;
		
		double powertotal = 0;
		for(int i=0;i<values.length;i++) {
			powertotal += (values[i]-average)*(values[i]-average);
		}
		double mut = Math.pow(powertotal/(values.length-1), 0.5)/average;
		return mut;
		
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("COUNT="+COUNT+";ALL_TIME_TREND="+ALL_TIME_TREND+ALL_TIME_SURGE+";RECENT_TREND="+RECENT_TREND+RECENT_SURGE);
		return sb.toString();
	}
	
}
