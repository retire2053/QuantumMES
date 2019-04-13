package qmes.config.storage;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.StorageBase;
import qmes.config.def.NConfig;
import qmes.config.def.NConfigItem;
import qmes.nlp.storage.MatchableStorage;
import qmes.nlp.translate.def.Matchable;

public class NConfigStorage extends StorageBase{
	
	
	public static String CONFIG_SEMANTIC_TRANSFER = "SemanticTransfer";
	public static String CONFIG_SETTING = "Setting";
	
	public static String KEY_MIN_SCORE = "minScore";
	public static String KEY_DEFAULT_TEXT = "defaultText";
	public static String KEY_NOT_SHOW_NECESSARY_UNMATCH = "notShowNecessaryUnmatch";
	
	private static final Logger log = LoggerFactory.getLogger(MatchableStorage.class);
	
	List<NConfig> nconfigs = new ArrayList<NConfig>();
	
	public NConfigStorage(String basedir) {
		setBaseDir(basedir);
		
		loadStorage();
	}

	protected void processFile(File file) {
		try {
			NConfig id = (NConfig) file2object(file, NConfig.class);
			nconfigs.add(id);
			log.info("load nconfig object from file {}", file.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}

	protected String getPattern() {
		return ".nconfig";
	}

	protected void loadStorage() {
		traverse(new File(getBaseDir()), getPattern());
	}
	
	public void saveStorage() {
		try {
			for (int i = 0; i < nconfigs.size(); i++) {
				File target = new File(getBaseDir(), nconfigs.get(i).getName() + getPattern());
				this.object2file(target, nconfigs.get(i));
				log.info("nconfig {} saved to file",nconfigs.get(i).getName(), target.getAbsolutePath());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}

	protected void deleteStorage(String scope) {
		try {
			File f = new File(getBaseDir(),scope+getPattern());
			f.delete();
			log.info("nconfig {} is deleted", scope);
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}
	
	public List<NConfig> getNConfig() {
		return nconfigs;
	}
	
	public boolean addNConfig(NConfig m) {
		nconfigs.add(m);
		return true;
	}
	
	public void removeNConfig(NConfig m) {
		nconfigs.remove(m);
		deleteStorage(m.getName());
	}
	
	public void addNConfigItem(NConfig m, NConfigItem item) {
		m.getList().add(item);
		
	}
	
	public void removeNConfigItem(NConfig m, NConfigItem item) {
		m.getList().remove(item);
	}
	
	public NConfig getConfig(String name) {
		
		if(nconfigs.size()>0) {
			for(int i=0;i<nconfigs.size();i++) {
				if(name.equals(nconfigs.get(i).getName())) {
					return nconfigs.get(i);
				}
			}
		}
		
		NConfig config = new NConfig();
		config.setName(name);
		nconfigs.add(config);
		if(name.equals(CONFIG_SETTING))createSetting(config);
		return config;
	}
	
	public String getSetting(String name) {
		NConfig config = getConfig(CONFIG_SETTING);
		for(int i=0;i<config.getList().size();i++) {
			if(name.equals(config.getList().get(i).getName())) {
				return config.getList().get(i).getValue();
			}
		}
		return null;
	}
	
	public void setSetting(String name, String value) {
		NConfig config = getConfig(CONFIG_SETTING);
		for (int i = 0; i < config.getList().size(); i++) {
			if (name.equals(config.getList().get(i).getName())) {
				config.getList().get(i).setValue(value);
			}
		}
	}
	
	private void createSetting(NConfig config) {
		NConfigItem item = new NConfigItem();
		item.setName(KEY_MIN_SCORE);
		item.setValue("60");
		config.getList().add(item);
		
		item = new NConfigItem();
		item.setName(KEY_DEFAULT_TEXT);
		item.setValue("信息不足，请补充信息");
		config.getList().add(item);
		
		item = new NConfigItem();
		item.setName(KEY_NOT_SHOW_NECESSARY_UNMATCH);
		item.setValue("true");
		config.getList().add(item);
		
		
	}

}
