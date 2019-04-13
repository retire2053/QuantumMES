package qmes.word.ui.part;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import qmes.base.BASEUI;
import qmes.word.def.FeatureState;
import qmes.word.storage.FeatureStateStorage;
import qmes.word.storage.WordStorage;

public class FeatureStateTableModel extends DefaultTableModel {
	
	public static int COLUMN_ERROR = 0;
	public static int COLUMN_NAME = 1;
	public static int COLUMN_GROUP = 2;
	public static int COLUMN_SYNONYM = 3;
	public static int COLUMN_PARENT = 4;
	
	private FeatureStateStorage ws = null;
	
	private Object[][] getData(List<FeatureState> featurestates){
		Object[][] data = new Object[featurestates.size()][5];
		if(featurestates.size()>0) {
			for(int i=0;i<featurestates.size();i++) {
				FeatureState fs = featurestates.get(i);
				data[i][0] = new ErrorObj();
				data[i][1] = fs;
				data[i][2] = fs.getGroup();
				data[i][3] = BASEUI.stringArrayToString(fs.getAffiliates());
				data[i][4] = fs.getParent();
			}
		}
		return data;
	}
	
	private String[] getColumnNames() {
		return new String[] {"","特征值","组","同义词", "上级"};
	}
	
	public FeatureStateTableModel(FeatureStateStorage ws, List<FeatureState> featurestates) {
		this.ws = ws;
		
		Object[][] data = getData(featurestates);
		this.setDataVector(data, getColumnNames());
	}


	public void setValueAt(Object aValue, int row, int column) {
		
		
		FeatureState fs = (FeatureState)getValueAt(row, COLUMN_NAME);
		if(column==COLUMN_NAME) {
			String v = (String)aValue;
			if(v==null || v.length()<2) {
				JOptionPane.showMessageDialog(null, "无效的特征值");
			}else if(!v.equals(fs.getValue())) {
				if(ws.findWord(v)!=null) {
					JOptionPane.showMessageDialog(null, "重复的特征值");
				}
				else {
					ws.updateWord(fs, FeatureStateStorage.FIELD_NAME, aValue);
					super.setValueAt(fs, row, column);
				}
			}
		}else if(column==COLUMN_GROUP) {
			ws.updateWord(fs, FeatureStateStorage.FIELD_GROUP, aValue);
			super.setValueAt(fs.getGroup(), row, column);
		}else if(column==COLUMN_SYNONYM) {
			ws.updateWord(fs, FeatureStateStorage.FIELD_SYNONYM, aValue);
			super.setValueAt(BASEUI.stringArrayToString(fs.getAffiliates()), row, column);
		}else if(column==COLUMN_PARENT) {
			ws.updateWord(fs, FeatureStateStorage.FIELD_PARENT, aValue);
			super.setValueAt(fs.getParent(), row, column);
		}else {
			super.setValueAt(aValue, row, column);
		}
	}
	


}
