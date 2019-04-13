package qmes.word.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import qmes.word.def.FeatureName;
import qmes.word.def.Word;

public class FeatureNameStorage extends WordStorage{
	
	public static int FIELD_NAME = 0;
	public static int FIELD_CLASS = 1;
	public static int FIELD_GROUP = 2;
	public static int FIELD_SYNONYM = 3;
	public static int FIELD_PARENT = 4;

	public FeatureNameStorage(String basedir) {
		super(basedir);
	}

	public Class getTypeClass() {
		return FeatureName.class;
	}

	public Word createTempWord() {
		return new FeatureName();
	}
	
	public void updateWord(Word w, int field, Object aValue) {
		
		this.notifyBeforeUpdateObject(w);
		
		FeatureName fn = (FeatureName)w;
		if(field==FIELD_NAME) {
			if(words.containsKey(fn.getValue())) {
				words.remove(fn.getValue());
				deleteStorage(fn.getValue());
			}
			fn.setValue((String)aValue);
			words.put(fn.getValue(), fn);
		}else if(field==FIELD_CLASS) {
			addAll(fn.getFeatureClasses(), (String)aValue);
		}else if(field==FIELD_GROUP) {
			addAll(fn.getGroups(), (String)aValue);
		}else if(field==FIELD_SYNONYM) {
			removeSynonymRef(fn);
			addAll(fn.getAffiliates(), (String)aValue);
			setSynonymRef(fn);
		}else if(field==FIELD_PARENT) {
			fn.setParent((String)aValue);
		}
		
		this.notifyAfterUpdateObject(fn);
		
	}
	
	public List<FeatureName> getFeatureNames(){
		List<FeatureName> fns = new ArrayList<FeatureName>();
		Iterator<Word> itr = words.values().iterator();
		while(itr.hasNext()) {
			Word w = itr.next();
			fns.add((FeatureName)w);
		}
		return fns;
	}
	
}
