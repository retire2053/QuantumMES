package qmes.indicator.def;

import java.util.ArrayList;
import java.util.List;

public class IndicatorDef implements Base {
	
	public IndicatorDef() {}
	
	public RecentDef getRecent() {
		return recent;
	}

	public void setRecent(RecentDef recent) {
		this.recent = recent;
	}

	public void setRegions(List<RegionDef> regions) {
		this.regions = regions;
	}

	private String name;

	private String comment;
	
	private UnitDef unit=new UnitDef();
	
	private SurgeDef surge=new SurgeDef();
	
	private RecentDef recent=new RecentDef();
	
	private List<RegionDef> regions = new ArrayList<RegionDef>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public UnitDef getUnit() {
		return unit;
	}

	public void setUnit(UnitDef unit) {
		this.unit = unit;
	}

	public SurgeDef getSurge() {
		return surge;
	}

	public void setSurge(SurgeDef surge) {
		this.surge = surge;
	}

	public RecentDef getRecentDef() {
		return recent;
	}

	public void setRecentDef(RecentDef recent) {
		this.recent = recent;
	}
	
	public void addRegion(RegionDef region) {
		regions.add(region);
	}
	
	public List<RegionDef> getRegions(){
		return regions;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("name="+name+";");
		sb.append("comment="+comment+";");
		sb.append("unit=["+unit.toString()+"];");
		sb.append("surge=["+surge.toString()+"];");
		sb.append("recent=["+recent.toString()+"];");
		for(int i=0;i<regions.size();i++) {
			sb.append("region:["+regions.get(i).toString()+"]");
		}
		return sb.toString();
	}
	

	
}
