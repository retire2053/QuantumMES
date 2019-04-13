package qmes.nlp.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.StorageBase;
import qmes.nlp.translate.def.Matchable;

public class MatchableStorage extends StorageBase{
	
	private static final Logger log = LoggerFactory.getLogger(MatchableStorage.class);
	
	List<Matchable> matchables = new ArrayList<Matchable>();
	
	public MatchableStorage(String basedir) {
		setBaseDir(basedir);
		
		loadStorage();
	}

	protected void processFile(File file) {
		try {
			Matchable id = (Matchable) file2object(file, Matchable.class);
			matchables.add(id);
			log.info("load scope object from file {}", file.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}

	protected String getPattern() {
		return ".scope";
	}

	protected void loadStorage() {
		traverse(new File(getBaseDir()), getPattern());
	}
	
	public void saveStorage() {
		try {
			for (int i = 0; i < matchables.size(); i++) {
				File target = new File(getBaseDir(), matchables.get(i).getName() + getPattern());
				this.object2file(target, matchables.get(i));
				log.info("scope {} saved to file",matchables.get(i).getName(), target.getAbsolutePath());
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
			log.info("scope {} is deleted", scope);
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}
	
	public boolean addMatchable(Matchable m) {
		matchables.add(m);
		return true;
	}
	
	public void removeMatchable(Matchable m) {
		matchables.remove(m);
		deleteStorage(m.getName());
	}
	
	public Matchable getMatchable(Class clazz) {
		if(matchables.size()>0) {
			for(int i=0;i<matchables.size();i++) {
				if(clazz == matchables.get(i).getClazz()){
					return matchables.get(i);
				}
			}
		}
		return null;
	}
	
	
	public static int FIELD_NAME = 0;
	public static int FIELD_SCOPE = 1;
	public static int FIELD_EXP = 2;
	public static int FIELD_NAMEDEFAULT = 3;
	public static int FIELD_STATEDEFAULT = 4;
	public static int FIELD_TIMEDEFAULT = 5;
	public static int FIELD_TIMESPANDEFAULT = 6;
	public static int FIELD_UNITDEFAULT = 7;
	
	public void updateScope(Matchable matchable, int fieldtype, Object object) {
		if(fieldtype==FIELD_NAME) {
			deleteStorage(matchable.getName());
			matchable.setName((String)object);
		}else if(fieldtype==FIELD_SCOPE) {
			try {
				matchable.setClazz(Class.forName((String)object));
			}catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}else if(fieldtype==FIELD_EXP) {
			matchable.getExpr().setTemplate((String)object);;
		}else if(fieldtype==FIELD_NAMEDEFAULT) {
			addAll(matchable.getDefaults().get(Matchable.INDEX_NAME).getValues(), (String)object);
		}else if(fieldtype==FIELD_STATEDEFAULT) {
			addAll(matchable.getDefaults().get(Matchable.INDEX_STATE).getValues(), (String)object);
		}else if(fieldtype==FIELD_TIMEDEFAULT) {
			addAll(matchable.getDefaults().get(Matchable.INDEX_TIME).getValues(), (String)object);
		}else if(fieldtype==FIELD_TIMESPANDEFAULT) {
			addAll(matchable.getDefaults().get(Matchable.INDEX_TIMESPAN).getValues(), (String)object);
		}else if(fieldtype==FIELD_UNITDEFAULT) {
			addAll(matchable.getDefaults().get(Matchable.INDEX_UNIT).getValues(), (String)object);
		}
		
	}
	
	protected void addAll(List<String> list, String s) {
		list.clear();
		String[] v = BASEUI.stringToStringArray(s);
		if(v!=null && v.length>0) {
			for(int i=0;i<v.length;i++) {
				list.add(v[i]);
			}
		}
	}
	
	public List<Matchable> getMatchables(){
		return matchables;
	}

}
