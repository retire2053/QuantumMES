package qmes.config.def;

import java.util.ArrayList;
import java.util.List;

public class NConfig {
	
	public NConfig() {
		list = new ArrayList<NConfigItem>();
	}

	private String name;
	
	private List<NConfigItem> list;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NConfigItem> getList() {
		return list;
	}

	public void setList(List<NConfigItem> list) {
		this.list = list;
	}
}
