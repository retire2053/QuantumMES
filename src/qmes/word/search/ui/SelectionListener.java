package qmes.word.search.ui;

import qmes.model.HuskyObject;

public interface SelectionListener {
	
	public void select(String namespace, String clazz, String featurename, String featurestate);

	public void updateLhs(String lhs);
	
	public void updateRhs(String rhs);
	
	public void updateObject(HuskyObject ho);
	
}
