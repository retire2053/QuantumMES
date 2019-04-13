package qmes.word.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;
import qmes.word.def.Word;

public class FeatureStateStorage extends WordStorage {
	
	public static int FIELD_NAME = 0;
	public static int FIELD_GROUP = 1;
	public static int FIELD_SYNONYM = 2;
	public static int FIELD_PARENT = 3;
	
	public FeatureStateStorage(String basedir) {
		super(basedir);
	}

	public Class getTypeClass() {
		return FeatureState.class;
	}

	public Word createTempWord() {
		return new FeatureState();
	}
	
	public void updateWord(Word w, int field, Object aValue) {
		
		this.notifyBeforeUpdateObject(w);
		
		FeatureState fs = (FeatureState)w;
		if(field==FIELD_NAME) {
			if(words.containsKey(fs.getValue())) {
				words.remove(fs.getValue());
				deleteStorage(fs.getValue());
			}
			fs.setValue((String)aValue);
			words.put(fs.getValue(), fs);
		}else if(field==FIELD_GROUP) {
			fs.setGroup((String)aValue);
		}else if(field==FIELD_SYNONYM) {
			removeSynonymRef(fs);
			addAll(fs.getAffiliates(), (String)aValue);
			setSynonymRef(fs);
		}else if(field==FIELD_PARENT) {
			fs.setParent((String)aValue);
		}
		
		this.notifyAfterUpdateObject(fs);
		
	}

	public List<FeatureState> findFeatureStateByGroup(String group) {

		List<FeatureState> fss = new ArrayList<FeatureState>();
		Iterator<Word> itr = words.values().iterator();
		while (itr.hasNext()) {
			FeatureState fs = (FeatureState) itr.next();
			if (group.equals(fs.getGroup())) {
				fss.add(fs);
			}
		}
		return fss;

	}
	
	public List<FeatureState> getFeatureState() {
		List<FeatureState> fss = new ArrayList<FeatureState>();
		Iterator<Word> itr = words.values().iterator();
		while (itr.hasNext()) {
			Word w = itr.next();
			fss.add((FeatureState) w);
		}
		return fss;
	}
}
