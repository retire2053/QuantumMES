package qmes.word.storage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.CONST;
import qmes.cases.def.CaseDef;
import qmes.cases.def.CaseHelper;
import qmes.core.Model;
import qmes.indicator.def.IndicatorDef;
import qmes.model.HuskyObject;
import qmes.model.IndicatorV;
import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;
import qmes.word.def.Word;

public class WordStorageHelper {
	
	private static final Logger log = LoggerFactory.getLogger(WordStorageHelper.class);
	

	private Model model = null;

	private WordStorage featurenames = null;
	private WordStorage featurestates = null;
	
	private String namespace = null;

	public WordStorageHelper(Model model, String namespace) {
		this.model = model;
		this.namespace = namespace;

		featurenames = model.getWordStorage(namespace, CONST.TYPE_FEATURE_NAMES);
		featurestates = model.getWordStorage(namespace, CONST.TYPE_FEATURE_STATES);
	}

	public List<FeatureName> listFeatureNameWithClass(String classname) {

		List<Word> temp = featurenames.listWords();
		List<FeatureName> results = new ArrayList<FeatureName>();
		for (int i = 0; i < temp.size(); i++) {
			FeatureName fn = (FeatureName) temp.get(i);
			if (fn.getFeatureClasses().indexOf(classname) >= 0) {
				results.add(fn);
			}
		}
		return results;
	}

	public List<String> listGroupNames(FeatureName featurename) {
		return featurename.getGroups();
	}

	public List<FeatureState> listFeatureStatesByGroup(String group) {
		List<Word> temp = featurestates.listWords();
		List<FeatureState> results = new ArrayList<FeatureState>();
		for (int i = 0; i < temp.size(); i++) {
			FeatureState fs = (FeatureState) temp.get(i);
			if (group.equals(fs.getGroup())) {
				results.add(fs);
			}
		}
		return results;
	}

	public List<FeatureState> listFeatureStatesByBinding(FeatureName featurename) {
		List<Word> temp = featurestates.listWords();
		List<FeatureState> results = new ArrayList<FeatureState>();
		for (int i = 0; i < temp.size(); i++) {
			FeatureState fs = (FeatureState) temp.get(i);
			if (fs.getFeatureNames().indexOf(featurename.getValue()) >= 0) {
				results.add(fs);
			}
		}
		return results;
	}

	public static List<FeatureState> listExistState() {
		List<FeatureState> results = new ArrayList<FeatureState>();
		FeatureState fs = new FeatureState();
		fs.setValue(CONST.EXIST_GROUP_VALUE_EXIST);
		results.add(fs);

		fs = new FeatureState();
		fs.setValue(CONST.EXIST_GROUP_VALUE_NON_EXIST);
		results.add(fs);
		return results;
	}
	
	//////////////////////////////////////////////////////////
	// 用来检查Case中的husky对象是否符合词义网络的规范
	//////////////////////////////////////////////////////////
	
	public String checkCaseIntegriy(CaseDef cd) {

		CaseHelper helper = new CaseHelper();
		List<HuskyObject> hos = helper.wrapper2Husky(cd.getHuskys());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hos.size(); i++) {
			HuskyObject ho = hos.get(i);
			try {
				Method getName = ho.getClass().getMethod("getName", new Class[] {});
				String name = (String) getName.invoke(ho, new Object[] {});

				if (name != null) {
					FeatureName w = (FeatureName) featurenames.findWord(name);
					if (w == null || !w.getValue().equals(name)) {// 可能是Null，也可能是当前Name是一个affiliate
						String err = cd.getName() + " [No." + (i + 1) + "][" + ho.getClass().getSimpleName() + "][invalid feature name:"+ name + "]";
						sb.append(err+"\n");
						log.error(err);
					}
				}

				if (!(ho instanceof IndicatorV)) {
					Method getState = ho.getClass().getMethod("getState", new Class[] {});
					String state = (String) getState.invoke(ho, new Object[] {});

					if (state != null && !CONST.EXIST_GROUP_VALUE_EXIST.equals(state)
							&& !CONST.EXIST_GROUP_VALUE_NON_EXIST.equals(state)) {
						FeatureState fs = (FeatureState) featurestates.findWord(state);
						if (fs == null || !fs.getValue().equals(state)) {// 可能是Null，也可能是当前Name是一个affiliate
							String err = cd.getName() + " [No." + (i + 1) + "][" + ho.getClass().getSimpleName() + "][invalid feature state:"+ state + "]";
							sb.append(err+"\n");
							log.error(err);
						}
					}
				} else {
					IndicatorDef id = model.getIndicatorDefByName(name);
					if (id == null) {
						String err = cd.getName() + " [No." + (i + 1) + "][" + ho.getClass().getSimpleName() + "][invalid feature name:"	+ name + "]";
						sb.append(err+"\n");
						log.error(err);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}
		return sb.toString();
	}
	
	public String checkCaseIntegrity() {

		List<CaseDef> cds = model.getCaseList();
		StringBuffer sb = new StringBuffer();
		for (int t = 0; t < cds.size(); t++) {
			sb.append(checkCaseIntegriy(cds.get(t)));
		}
		return sb.toString();
	}
	

	//////////////////////////////////////////////////////////
	// 以下几个文件是用来进行word错误检查的，分别检查
	// 1.没有组的featurestate checkFeatureStateWithoutGroup
	// 2.没有组的featuregname checkFeatureNameWithoutGroup
	// 3.获得出所有的featurenames所用的组set
	// 4.获得出没有featurename引用的featurestate group
	//////////////////////////////////////////////////////////

	private List<FeatureState> checkFeatureStateWithoutGroup() {

		List<FeatureState> fss = new ArrayList<FeatureState>();
		Iterator<Word> itr = featurestates.listWords().iterator();
		while (itr.hasNext()) {
			FeatureState fs = (FeatureState) itr.next();
			if (fs.getGroup() == null || "".equals(fs.getGroup().trim())) {
				
				fss.add(fs);
			}
		}
		return fss;
	}

	private List<FeatureName> checkFeatureNameWithoutGroup() {
		List<FeatureName> fns = new ArrayList<FeatureName>();
		Iterator<Word> itr = featurenames.listWords().iterator();
		while (itr.hasNext()) {
			FeatureName fn = (FeatureName) itr.next();
			System.out.println(fn.getValue()+"="+fn.getGroups().size());
			if (fn.getGroups() == null || fn.getGroups().size() == 0) {
				fns.add(fn);
			}
		}
		return fns;

	}

	private Set<String> getAllGroupsFromFeatureNames() {
		Set<String> set = new HashSet<String>();
		Iterator<Word> itr = featurenames.listWords().iterator();
		while (itr.hasNext()) {
			FeatureName fn = (FeatureName) itr.next();
			set.addAll(fn.getGroups());
		}
		return set;

	}

	private List<FeatureState> getOrphanFeatureState(Set<String> allgroupsfromfeaturename) {

		List<FeatureState> fss = new ArrayList<FeatureState>();
		Iterator<Word> itr = featurestates.listWords().iterator();
		while (itr.hasNext()) {
			FeatureState fs = (FeatureState) itr.next();
			if (!allgroupsfromfeaturename.contains(fs.getGroup())) {
				fss.add(fs);
			}
		}
		return fss;

	}
	
	public String showGroupErrorCheckHtml() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("命名空间="+namespace+"\n\n");
		
		List<FeatureState> fss = checkFeatureStateWithoutGroup();
		if(fss.size()>0) {
			sb.append("没有组的特征名\n");
			for(int i=0;i<fss.size();i++) {
				sb.append("\t"+fss.get(i).getValue()+"\n");
			}
			sb.append("\n");
		}
		
		List<FeatureName> fns = checkFeatureNameWithoutGroup();
		if(fns.size()>0) {
			sb.append("没有组的特征名\n");
			for(int i=0;i<fns.size();i++) {
				sb.append("\t"+fns.get(i).getValue()+"\n");
			}
			sb.append("\n");
		}
		
		Set<String> set = getAllGroupsFromFeatureNames();
		fss = getOrphanFeatureState(set);
		if(fss.size()>0) {
			sb.append("有组但是却没有被引用的特征值\n");
			for(int i=0;i<fss.size();i++) {
				sb.append("\t"+fss.get(i).getValue()+"\n");
			}
			sb.append("\n");
		}
		sb.append("\n");
		return sb.toString();
	}

}
