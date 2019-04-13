package husky.ut;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import qmes.model.HuskyObject;

//JUNIT4 testcase

public abstract class BaseTest{
	
	protected KieSession session = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		
		KieServices ks = KieServices.Factory.get();
	    KieContainer kContainer = ks.getKieClasspathContainer();
    		session = kContainer.newKieSession(getSessionName());
		setGlobals();
	}
	
	protected abstract String getSessionName();
	
	public void setGlobals() {};

	@After
	public void tearDown() throws Exception {
		session.dispose();
	}
	
	protected boolean testValue(List<HuskyObject> results, TestTarget target)
			throws NoSuchMethodException,InvocationTargetException,IllegalAccessException {
		for(int i=0;i<results.size();i++) {
			HuskyObject ho = results.get(i);
			if(ho.getClass().getSimpleName().equals(target.getClassname())) {
				String p = BeanUtils.getProperty(ho, target.getPropertyname());
				if(p!=null && p.equals(String.valueOf(target.getTargetvalue()))) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean testValue(List<HuskyObject> results, TestTargetList targetlist)
			throws NoSuchMethodException,InvocationTargetException,IllegalAccessException {
		for(int i=0;i<results.size();i++) {
			HuskyObject ho = results.get(i);
			if(ho.getClass().getSimpleName().equals(targetlist.getClassname())) {
				boolean found = true;
				for(int p=0;p<targetlist.getKVCount();p++) {
					String valueofproperty = BeanUtils.getProperty(ho, targetlist.getPropertyname(p));
					if(valueofproperty!=null && valueofproperty.equals(String.valueOf(targetlist.getTargetvalue(p)))) {
						continue;
					}
					else {
						found=false;
						break;
					}
				}
				if(found)return true;
			}
		}
		return false;
	}
	
	protected int testCount(List<HuskyObject> results, Class clazz) {
		int count = 0;
		for(int i=0;i<results.size();i++) {
			HuskyObject ho = results.get(i);
			if(ho.getClass().getName().equals(clazz.getName())) count++;
		}
		return count;
	}
	
	public List<HuskyObject> getFacts() {
		Iterator<FactHandle> itr = session.getFactHandles().iterator();
		List<HuskyObject> results = new ArrayList<HuskyObject>();
		while(itr.hasNext()) {
			FactHandle fh = itr.next();
			HuskyObject object = (HuskyObject)session.getObject(fh);
			object.setClazz(object.getClass().getSimpleName());
			System.out.println(object.toString());
			results.add(object);
		}
		return results;
		
		
	}
	
}