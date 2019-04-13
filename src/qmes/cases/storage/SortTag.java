package qmes.cases.storage;

public class SortTag {
	SortTag(String tag, int count){
		this.tag = tag;
		this.count = count;
	}
	public String tag;
	public int count;
	
	public String toString() {
		return tag+" ("+count+")";
	}
}