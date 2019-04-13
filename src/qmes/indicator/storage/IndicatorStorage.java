package qmes.indicator.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.StorageBase;
import qmes.indicator.def.IndicatorDef;

public class IndicatorStorage extends StorageBase {

	private static final Logger log = LoggerFactory.getLogger(IndicatorStorage.class);

	public IndicatorStorage(String basedir) {

		setBaseDir(basedir);

		loadStorage();

	}

	List<IndicatorDef> indicatordefs = new ArrayList<IndicatorDef>();

	public void addIndicatorDef(IndicatorDef id) {
		indicatordefs.add(id);
	}

	public void clear() {
		indicatordefs.clear();
	}

	public void removeIndicatorDef(IndicatorDef id) {
		indicatordefs.remove(id);
	}

	public List<IndicatorDef> getIndicatorDefs() {
		return indicatordefs;
	}

	public IndicatorDef find(String name) {
		for (int i = 0; i < indicatordefs.size(); i++) {
			if (indicatordefs.get(i).getName().equalsIgnoreCase(name)) {
				return indicatordefs.get(i);
			}
		}
		return null;
	}

	public IndicatorDef find(String name, String unit) {
		for (int i = 0; i < indicatordefs.size(); i++) {
			if (indicatordefs.get(i).getName().equalsIgnoreCase(name)
					&& indicatordefs.get(i).getUnit().getUnit().equalsIgnoreCase(unit)) {
				return indicatordefs.get(i);
			}
		}
		return null;
	}

	protected void processFile(File file) {
		try {
			IndicatorDef id = (IndicatorDef) file2object(file, IndicatorDef.class);
			indicatordefs.add(id);
			log.info("load IndicatorDef object from file {}", file.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}

	}

	protected String getPattern() {
		return ".indicatordef";
	}

	protected void loadStorage() {
		traverse(new File(getBaseDir()), getPattern());
	}

	public void saveStorage() {
		try {
			for (int i = 0; i < indicatordefs.size(); i++) {
				File target = new File(getBaseDir(), indicatordefs.get(i).getName() + getPattern());
				this.object2file(target, indicatordefs.get(i));
				log.info("IndicatorDef {} saved to file",indicatordefs.get(i).getName(), target.getAbsolutePath());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}

	}

	public void deleteStorage(String filename) {
		File f = new File(getBaseDir(),filename+getPattern());
		f.delete();
		log.info("删除指标文件："+f);
	}
}
