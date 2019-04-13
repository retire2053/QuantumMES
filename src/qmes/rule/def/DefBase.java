package qmes.rule.def;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class DefBase implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
//	private int power = 0;
//	private boolean necessary = false;
//	
//	public int getPower() {return power;}
//	public void setPower(int power) {this.power = power;}
//	
//	public boolean isNecessary() {return necessary;}
//	public void setNecessary(boolean necessary) {this.necessary = necessary;}
	
	public abstract String toString();
	
	private List<IDefListener> listeners = new ArrayList<IDefListener>();
	public void addListener(IDefListener listener) {
		listeners.add(listener);
	}
	
	public void notifyListener() {
		for(int i=0;i<listeners.size();i++) {
			listeners.get(i).changed();
		}
	}
	
	private DefBase parent;
	public DefBase getParent() {return parent;}
	public void setParent(DefBase parent) {this.parent = parent;}
}
