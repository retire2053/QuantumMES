package qmes.word.ui.part;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import qmes.base.BASEUI;
import qmes.word.def.FeatureName;
import qmes.word.storage.FeatureNameStorage;
import qmes.word.storage.WordStorage;

public class FeatureNameTableModel extends DefaultTableModel {
	


	public static int COLUMN_ERROR = 0;
	public static int COLUMN_CLASS = 1;
	public static int COLUMN_NAME = 2;
	public static int COLUMN_GROUP = 3;
	public static int COLUMN_SYNONYM = 4;
	public static int COLUMN_PARENT = 5;
	
	private FeatureNameStorage ws = null;
	
	private Object[][] getData(List<FeatureName> featurenames){
		Object[][] data = new Object[featurenames.size()][6];
		if(featurenames.size()>0) {
			for(int i=0;i<featurenames.size();i++) {
				FeatureName fn = featurenames.get(i);
				data[i][0] = new ErrorObj();
				data[i][1] = BASEUI.stringArrayToString(fn.getFeatureClasses());
				data[i][2] = fn;
				data[i][3] = BASEUI.stringArrayToString(fn.getGroups());
				data[i][4] = BASEUI.stringArrayToString(fn.getAffiliates());
				data[i][5] = fn.getParent();

				
			}
		}
		return data;
	}
	
	private String[] getColumnNames() {
		return new String[] {"","类","特征名","组","同义词","上级"};
	}
	
	public FeatureNameTableModel(FeatureNameStorage ws, List<FeatureName> featurenames) {
		this.ws = ws;
		
		Object[][] data = getData(featurenames);
		this.setDataVector(data, getColumnNames());
	}


	public void setValueAt(Object aValue, int row, int column) {
		
		FeatureName fn = (FeatureName)getValueAt(row, COLUMN_NAME);
		if(column==COLUMN_CLASS) {
			ws.updateWord(fn, FeatureNameStorage.FIELD_CLASS, aValue);
			super.setValueAt(BASEUI.stringArrayToString(fn.getFeatureClasses()), row, column);
		}else if(column==COLUMN_NAME) {
			String v = (String)aValue;
			if(v==null || v.length()<2) {
				JOptionPane.showMessageDialog(null, "无效的特征名");
			}else if(!v.equals(fn.getValue())) {
				if(ws.findWord(v)!=null) {
					JOptionPane.showMessageDialog(null, "重复的特征名");
				}
				else {
					ws.updateWord(fn,  FeatureNameStorage.FIELD_NAME, aValue);
					super.setValueAt(fn, row, column);
				}
			}
		}else if(column==COLUMN_GROUP) {
			ws.updateWord(fn, FeatureNameStorage.FIELD_GROUP, aValue);
			super.setValueAt(BASEUI.stringArrayToString(fn.getGroups()), row, column);
		}else if(column==COLUMN_SYNONYM) {
			ws.updateWord(fn,  FeatureNameStorage.FIELD_SYNONYM, aValue);
			super.setValueAt(BASEUI.stringArrayToString(fn.getAffiliates()), row, column);
		}else if(column==COLUMN_PARENT) {
			ws.updateWord(fn, FeatureNameStorage.FIELD_PARENT, aValue);
			super.setValueAt(fn.getParent(), row, column);
		}else super.setValueAt(aValue, row, column);

	}
	

}
