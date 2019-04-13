package qmes.word.def;

import java.util.ArrayList;
import java.util.List;

public class FeatureName extends Word{
	
	public static String DEFAULT_BIND_PROPERTY_NAME = "name";

	private List<String> featureClasses = new ArrayList<String>();

	public List<String> getFeatureClasses() {
		return featureClasses;
	}

	public void setFeatureClasses(List<String> featureClasses) {
		this.featureClasses = featureClasses;
	}

	private List<String> groups = new ArrayList<String>();

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
}
