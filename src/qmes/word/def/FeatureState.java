package qmes.word.def;

import java.util.ArrayList;
import java.util.List;

public class FeatureState extends Word{

	public static String DEFAULT_BIND_PROPERTY_NAME = "state";
	
	private String bindProperty = null;
	
	private String group = null;
	
	private List<String> featureNames = new ArrayList<String>();

	public List<String> getFeatureNames() {
		return featureNames;
	}

	public void setFeatureNames(List<String> featureNames) {
		this.featureNames = featureNames;
	}

	public String getBindProperty() {
		return bindProperty;
	}

	public void setBindProperty(String bindProperty) {
		this.bindProperty = bindProperty;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	
}
