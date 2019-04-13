package qmes.indicator.calc;

import qmes.indicator.def.IndicatorDef;
import qmes.indicator.def.RegionDef;

public class VALUE {
	public String STATE = null;
	public double ATTACH_PARENT = 1.0d;
	
	private IndicatorDef def = null;
	private double value;
	public VALUE(IndicatorDef def, double value) {
		this.def = def;
		this.value = value;
		calc();
	}
	
	private void calc() {
		for(int i=0;i<def.getRegions().size();i++) {
			RegionDef r = def.getRegions().get(i);
			if(r.getLower()<= value && value<r.getUpper()) {
				STATE = r.getState();
				if(r.getParent()!=null) {
					if(r.getParent().getLower()>=r.getUpper()) {
						ATTACH_PARENT = (value - r.getLower())/(r.getUpper()-r.getLower());
					}else if(r.getParent().getUpper() <= r.getLower()) {
						ATTACH_PARENT = (r.getUpper() - value)/(r.getUpper()-r.getLower());
					}
				}
				else {
					ATTACH_PARENT = 1.0d;
				}
			}
		}
		
	}
	
	public String toString() {
		return "状态="+STATE+";疑似度="+ATTACH_PARENT;
	}
}
