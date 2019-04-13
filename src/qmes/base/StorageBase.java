package qmes.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class StorageBase {
	
	private static final Logger log = LoggerFactory.getLogger(StorageBase.class);

	ObjectMapper objectMapper = new ObjectMapper();
	
	private String basedir;
	
	public String getBaseDir() {return basedir;}
	public void setBaseDir(String basedir) {this.basedir = basedir;}
	
	protected abstract void processFile(File file) ;
	
	protected abstract String getPattern();
	
	protected abstract void loadStorage() ;
	
	protected abstract void saveStorage() ;
	
	protected abstract void deleteStorage(String filename);
	
	protected void traverse(File basepath, String pattern) {
		
		if(basepath.isDirectory()) {
			File[] fs = basepath.listFiles();
			for(int i=0;i<fs.length;i++) {
				if(fs[i].isDirectory()) {
					traverse(fs[i], pattern);
				}
				else if(fs[i].isFile() && !fs[i].isHidden()){
					String filename = fs[i].getName();
					if(!filename.startsWith("~") && !filename.startsWith(".") && filename.toLowerCase().endsWith(pattern))
					{
						processFile(fs[i]);
					}
				}
			}
		}
		
	}
	
	protected void object2file(File path, Object object) throws Exception{
		String json =  objectMapper.writeValueAsString(object);
		saveFileContent(path, json);
	}
	
	protected Object file2object(File path,  Class type)throws Exception{
		      
		String content = loadFileContent(path);
		return objectMapper.readValue(content, type);
	}
	
	private void saveFileContent(File path, String content)throws Exception{
		
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content.getBytes());
		fos.close();
	}
	
	private String loadFileContent(File path)throws Exception {
		
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String s;
		StringBuffer sb = new StringBuffer();
        while ((s = br.readLine() )!=null) {
            sb.append(s);
         }
        fr.close();
        return sb.toString();
	}
	
	List<StorageListener> listeners = new ArrayList<StorageListener>();
	
	public void addStorageListener(StorageListener sl) {
		listeners.add(sl);
	}
	
	protected void notifyAddObject(Object o) {
		log.info("notify add object in Storage Base, object={}", o);
		if(listeners.size()>0) {
			for(int i=0;i<listeners.size();i++) {
				listeners.get(i).addObject(o);
			}
		}
	}
	
	protected void notifyRemoveObject(Object o) {
		log.info("notify remove object in Storage Base, object={}", o);
		if(listeners.size()>0) {
			for(int i=0;i<listeners.size();i++) {
				listeners.get(i).removeObject(o);
			}
		}
	}
	
	protected void notifyAfterUpdateObject(Object o) {
		log.info("notify after update object in Storage Base, object={}", o);
		if(listeners.size()>0) {
			for(int i=0;i<listeners.size();i++) {
				listeners.get(i).afterUpdateObject(o);
			}
		}
	}
	
	protected void notifyBeforeUpdateObject(Object o) {
		log.info("notify before update object in Storage Base, object={}", o);
		if(listeners.size()>0) {
			for(int i=0;i<listeners.size();i++) {
				listeners.get(i).beforeUpdateObject(o);
			}
		}
	}
}
