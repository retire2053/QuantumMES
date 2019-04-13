package qmes.word.ui.part;

public class ErrorObj {

	private String error = null;
	
	public ErrorObj() {}
	
	public ErrorObj(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String toString() {
		if(error!=null && error.trim().length()>0)return "X";
		else return "";
	}
}
