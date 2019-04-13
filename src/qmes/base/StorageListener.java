package qmes.base;

public interface StorageListener {

	public void addObject(Object object);
	public void removeObject(Object object);
	
	public void beforeUpdateObject(Object object);
	public void afterUpdateObject(Object object);
	
}
