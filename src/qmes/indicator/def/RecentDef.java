package qmes.indicator.def;

public class RecentDef implements Base{
	
	public RecentDef() {}
	
	private double recent;
	
	public double getRecent() {return recent;}
	
	public void setRecent(double recent) {this.recent= recent;}
	
	public String toString() {
		return "recent="+recent;
	}
}
