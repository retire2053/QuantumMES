package qmes.nlp.translate.def;

import java.util.ArrayList;
import java.util.List;

import qmes.base.BASEUI;

public class DefaultValues {
	
	public DefaultValues() {}
	
	private String key;
	private List<String> values = new ArrayList<String>();
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}

	public String toString() {
		return BASEUI.stringArrayToString(values);
	}
}
