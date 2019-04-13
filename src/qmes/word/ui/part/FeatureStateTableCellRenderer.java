package qmes.word.ui.part;


import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;

public class FeatureStateTableCellRenderer extends DefaultTableCellRenderer {
	
	
	private FeatureState current = null;
	private boolean inCheck = false;
	
	public FeatureStateTableCellRenderer() {
		
	}
	
	public void setCheck(boolean check) {
		inCheck = check;
	}
	
	public void setCurrent(FeatureState fn) {
		this.current = fn;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		

		if(inCheck && column==FeatureStateTableModel.COLUMN_ERROR) {
			ErrorObj eo = (ErrorObj)table.getValueAt(row, FeatureStateTableModel.COLUMN_ERROR);
			if(eo.getError()!=null && eo.getError().trim().length()>0)
				setForeground(Color.RED);
			else
				setForeground(Color.BLACK);
		}else{
			setForeground(Color.BLACK);
		}
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
