package qmes.word.storage;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.StorageBase;
import qmes.model.Diagnose;
import qmes.word.def.Word;

public abstract class WordStorage extends StorageBase implements Comparator<Word>{
	
	private static final Logger log = LoggerFactory.getLogger(WordStorage.class);
	
	private final static Comparator<Object> CHINA_COMPARE = Collator.getInstance(java.util.Locale.CHINA);
	
	protected Map<String, Word> words = new HashMap<String, Word>();
	protected Map<String, Word> affwords = new HashMap<String, Word>();
	
	public WordStorage(String basedir) {
		
		setBaseDir(basedir);
		
		init();
	}
	
	public void init() {
		
		log.info("start to load word storage in init()");
		
		loadStorage();
	}
	
	public abstract Class getTypeClass();
	
	protected void processFile(File file) {

		try {
			Word w = (Word) file2object(file, getTypeClass());
			addWord(w);
			log.info("Word \"{}\" is loadded", w.getValue());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}

	}
	
	protected String getPattern() { return ".word";}
	
	protected void loadStorage() {
		
		log.info("prepare load words from persistence storage with type \"{}\"", getTypeClass());
		
		traverse(new File(getBaseDir()), getPattern());
	}
	
	public void saveStorage() {
		
		List<Word> list = listWords();
		
		log.info("prepare store "+list.size()+" word(s) to persistence storage");
		
		for(int i=0;i<list.size();i++) {
			try {
				String filename = getBaseDir()+java.io.File.separator+list.get(i).getValue()+getPattern();
				object2file(new File(filename), list.get(i));
				log.info("Word \"{}\" is stored", list.get(i).getValue());
			}catch(Exception ex) {
				log.error(ex.getMessage());
				ex.printStackTrace();
			}
		}
		
	}
	
	
	private void addWordInternal(Word w) {
		words.put(w.getValue(), w);
		List<String> aff = w.getAffiliates();
		if (aff != null && aff.size() > 0) {
			for (int i = 0; i < aff.size(); i++) {
				affwords.put(aff.get(i), w);
			}
		}
	}
	
	public boolean addWord(Word w) {

		if (words.containsKey(w.getValue())) {
			// 该词语已经存在
			log.error("word \"{}\" already exists", w.getValue());
			return false;
		} else {
			addWordInternal(w);
			notifyAddObject(w);
			return true;
		}
	}
	
	public abstract void updateWord(Word w, int field, Object aValue);
	
	public boolean deleteWord(Word w) {
		
		String key = w.getValue();
		if (words.containsKey(key)) {
			words.remove(key);
			
			removeSynonymRef(w);

			notifyRemoveObject(w);
			deleteStorage(key);
			return true;
		}
		return true;
	}
	
	public abstract Word createTempWord();
	
	public Word findWord(String value) {
		if (words.containsKey(value))
			return words.get(value);
		else if (affwords.containsKey(value)) {
			return affwords.get(value);
		} else {
			return null;
		}
	}
	
	public List<Word> searchWord(String value){
		List<Word> temp = listWords();
		List<Word> ret = new ArrayList<Word>();
		for(int i=0;i<temp.size();i++) {
			if(temp.get(i).getValue().indexOf(value)>=0) {
				ret.add(temp.get(i));
				continue;
			}
			List<String> ts = temp.get(i).getAffiliates();
			for(int k=0;k<ts.size();k++) {
				if(ts.get(k).indexOf(value)>=0) {
					ret.add(temp.get(i));
					continue;
				}
			}
		}
		return ret;
	}
	
	public List<Word> searchTag(String value){
		List<Word> temp = listWords();
		List<Word> ret = new ArrayList<Word>();
		for(int i=0;i<temp.size();i++) {
			if(value.equalsIgnoreCase(temp.get(i).getTag())) {
				ret.add(temp.get(i));
				continue;
			}
		}
		return ret;
	}
	
	public List<Word> listWords(){
		List<Word> temp = new ArrayList<Word>();
		temp.addAll(words.values());
		temp.sort(this);
		return temp;
	}
	
	
	
	public int compare(Word o1, Word o2) {
		return CHINA_COMPARE.compare(o1.getValue(), o2.getValue());
	}
	
	//删除本地文件
	protected void deleteStorage(String filename) {
		String pathname = getBaseDir()+java.io.File.separator+filename+getPattern();
		File file = new File(pathname);
		file.delete();
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
	

	protected void removeSynonymRef(Word w) {
		if(w.getAffiliates()!=null) {
			for(int i=0;i<w.getAffiliates().size();i++) {
				String f = w.getAffiliates().get(i);
				if(f!=null && affwords.containsKey(f.trim())) {
					affwords.remove(f);
				}
			}
		}
	}
	
	protected void setSynonymRef(Word w) {
		if(w.getAffiliates()!=null) {
			for(int i=0;i<w.getAffiliates().size();i++) {
				String f = w.getAffiliates().get(i);
				if(f!=null && !"".equals(f.trim())) {
					affwords.put(f.trim(), w);
				}
			}
		}
	}
		
}
