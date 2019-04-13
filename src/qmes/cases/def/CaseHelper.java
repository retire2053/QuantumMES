package qmes.cases.def;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import qmes.base.CONST;
import qmes.model.HuskyObject;

public class CaseHelper {

	private static final Logger log = LoggerFactory.getLogger(CaseHelper.class);
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	public CaseHelper(){
	}
	
	private String object2String(Object o) throws Exception {
		return objectMapper.writeValueAsString(o);
	}
	
	private Object string2Object(String content, Class type) throws Exception {
		return objectMapper.readValue(content, type);

	}
	
	public HuskyWrapper husky2Wrapper(HuskyObject ho) {
		try {
			String json = object2String(ho);
			HuskyWrapper hw = new HuskyWrapper();
			hw.setClazz(ho.getClass().getName());
			hw.setJson(json);
			return hw;
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
		
	}
	
	public HuskyObject wrapper2Husky(HuskyWrapper wrapper) {
		try {
			Class c = Class.forName(wrapper.getClazz());
			return (HuskyObject)string2Object(wrapper.getJson(), c);
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
		
	}
	
	public List<HuskyWrapper> list2WrapperList(List<HuskyObject> hos) {
		try {
			List<HuskyWrapper> hws = new ArrayList<HuskyWrapper>();
			for(int i=0;i<hos.size();i++) {
				hws.add(husky2Wrapper(hos.get(i)));
			}
			return hws;
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return new ArrayList<HuskyWrapper>();
	}
	
	public List<HuskyObject> wrapper2Husky(List<HuskyWrapper> hws){
		try {
			List<HuskyObject> hos= new ArrayList<HuskyObject>();
			for(int i=0;i<hws.size();i++) {
				hos.add(wrapper2Husky(hws.get(i)));
			}
			return hos;
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return new ArrayList<HuskyObject>();
		
	}
	
	
	Map<Class, Integer> ordermap = new HashMap<Class, Integer>();
	
	private void setUpOrder(){
		for(int i=0;i<CONST.classes.length;i++) {
			ordermap.put(CONST.classes[i], CONST.order[i]);
		}
	}
	
	public void sortHuskyList(List<HuskyObject> list) {
		
		setUpOrder();
		
		list.sort(new Comparator<HuskyObject>() {
			public int compare(HuskyObject o1, HuskyObject o2) {
				Class c1 = o1.getClass();
				Class c2 = o2.getClass();
				if(ordermap.containsKey(c1) && ordermap.containsKey(c2)) {
					return ordermap.get(c1) - ordermap.get(c2);
				}else if(ordermap.containsKey(c1) && !ordermap.containsKey(c2))
					return ordermap.get(c1)-100;
				else if(!ordermap.containsKey(c1) && ordermap.containsKey(c2))
					return 100-ordermap.get(c2);
				else {
					return 0;
				}
			}
		});
	}
	
	public boolean toFile(File file, CaseDef casedef) {
		try {
			
			List<HuskyObject> sorted = new ArrayList<HuskyObject>();
			sorted.addAll(wrapper2Husky(casedef.getHuskys()));
			sortHuskyList(sorted);
			
			CaseDef newcase = new CaseDef();
			newcase.setDocument(casedef.getDocument());
			newcase.setName(casedef.getName());
			newcase.setRemarks(casedef.getRemarks());
			newcase.setTags(casedef.getTags());
			newcase.setHuskys(list2WrapperList(sorted));
			
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(newcase);
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(json.getBytes());
			fos.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
		return false;
	}
	
}
