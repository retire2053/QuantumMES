package qmes.cases.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.StorageBase;
import qmes.cases.def.CaseDef;
import qmes.cases.def.CaseHelper;
import qmes.core.Model;
import qmes.model.Diagnose;
import qmes.model.HuskyObject;
import qmes.model.Patient;

public class CaseStorage extends StorageBase {

	private static final Logger log = LoggerFactory.getLogger(CaseStorage.class);

	List<CaseDef> cases = new ArrayList<CaseDef>();
	
	public CaseStorage(String basedir) {

		setBaseDir(basedir);

		init();

	}

	private void init() {

		log.info("start to load case storage");

		loadStorage();
	}
	
	public void addCase(CaseDef n) {
		cases.add(n);
	}
	
	public void updateCaseByName(CaseDef n) {
		if (cases.size() > 0) {
			CaseDef oldCase = null;
			for (int i = 0; i < cases.size(); i++) {
				if (cases.get(i).getName().equals(n.getName())) {
					oldCase = cases.get(i);
					break;
				}
			}
			if (oldCase != null) {
				cases.remove(oldCase);
			}
		}
		addCase(n);
	}

	public List<CaseDef> getCases() {
		return cases;
	}

	protected void processFile(File file) {
		try {
			CaseDef cd = (CaseDef) file2object(file, CaseDef.class);
			cases.add(cd);
			log.info("load case object from file {}", file.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}

	protected String getPattern() {
		return ".case";
	}

	protected void loadStorage() {
		traverse(new File(getBaseDir()), getPattern());
	}

	public void saveStorage() {
		try {
			for (int i = 0; i < cases.size(); i++) {
				File target = new File(getBaseDir(), cases.get(i).getName() + getPattern());
				this.object2file(target, cases.get(i));
				log.info("case {} saved to file", cases.get(i).getName(), target.getAbsolutePath());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}

	}

	public void deleteStorage(String filename) {
		try {
			String pathname = getBaseDir() + java.io.File.separator + filename + getPattern();
			File file = new File(pathname);
			file.delete();
			log.info("delete file {}{} from case storage ", filename, getPattern());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	private int matchTag(String[] tag1, String[] tag2) {
		int count = 0;
		for(int i=0;i<tag1.length;i++) {
			for(int k=0;k<tag2.length;k++) {
				if(tag1[i]!=null && tag1[i].equals(tag2[k])){
					count++;
				}
			}
		}
		return count;
	}
	
	public List<CaseDef> filterByTags(String[] tags){
		if(tags==null || tags.length==0 || (tags.length==1 && tags[0].trim().equals(""))) {
			return cases;
		}else {
			List<CaseDef> results = new ArrayList<CaseDef>();
			if(cases.size()>0) {
				for(int i=0;i<cases.size();i++) {
					CaseDef cd = cases.get(i);
					String[] tagInCase = cd.getTags();
					if(matchTag(tags, tagInCase)>0) {
						results.add(cd);
					}
				}
			}
			return results;
		}
	}

	public SortTag[] getSortedTags() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		if (cases.size() > 0) {
			for (int i = 0; i < cases.size(); i++) {
				CaseDef cd = cases.get(i);
				if (cd.getTags() != null && cd.getTags().length > 0) {
					for (int k = 0; k < cd.getTags().length; k++) {

						if (map.containsKey(cd.getTags()[k])) {
							int v = map.get(cd.getTags()[k]).intValue();
							map.put(cd.getTags()[k], Integer.valueOf(v + 1));
						} else {
							map.put(cd.getTags()[k], Integer.valueOf(1));
						}
					}
				}

			}
		}

		List<SortTag> list = new ArrayList<SortTag>();
		Iterator<String> itr = map.keySet().iterator();
		while (itr.hasNext()) {
			String tag = itr.next();
			Integer v = map.get(tag);
			list.add(new SortTag(tag, v));
		}
		list.sort(new Comparator<SortTag>() {
			public int compare(SortTag o1, SortTag o2) {
				return o2.count - o1.count;
			}
		});
		
		SortTag[] sts = new SortTag[list.size()];
		if(list.size()>0) {
			for(int i=0;i<list.size();i++)sts[i] = list.get(i);
		}
		return sts;
	}
	
	
	public static void main(String[] args)throws Exception {
		CaseHelper h = new CaseHelper();
		Patient p = new Patient();
		p.setState("a");
		List<HuskyObject> hos = new ArrayList<HuskyObject>();
		hos.add(p);
		
		CaseDef cd1= new CaseDef();
		cd1.setName("Case1");
		cd1.setTags(new String[] {"tag1", "tag2"});
		cd1.getHuskys().addAll(h.list2WrapperList(hos));
		
		
		Patient p2 = new Patient();
		p2.setState("ab");
		
		Diagnose d = new Diagnose();
		d.setName("chb");
		
		hos = new ArrayList<HuskyObject>();
		hos.add(p2);
		hos.add(d);
		
		CaseDef cd2= new CaseDef();
		cd2.setName("Case2");
		cd2.setTags(new String[] {"tag1", "tag8"});
		cd2.getHuskys().addAll(h.list2WrapperList(hos));
		
		Patient p3 = new Patient();
		p3.setState("aab");
		
		Diagnose d2 = new Diagnose();
		d2.setName("chb");
		
		hos = new ArrayList<HuskyObject>();
		hos.add(p3);
		hos.add(d2);
		
		CaseDef cd3= new CaseDef();
		cd3.setName("Case3");
		cd3.setTags(new String[] {"tag4", "tag6", "tag1"});
		cd3.getHuskys().addAll(h.list2WrapperList(hos));
		
		Model model = new Model("/Users/retire2053/Documents/datawb/workbench");
		model.getCaseStorage().addCase(cd1);
		model.getCaseStorage().addCase(cd2);
		model.getCaseStorage().addCase(cd3);
		
		model.getCaseStorage().saveStorage();
		
	}

}
