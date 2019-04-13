package qmes.word.ui.part;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import qmes.word.def.FeatureName;

public class FeatureNameTableCellRenderer extends DefaultTableCellRenderer {
	
	
	private FeatureName current = null;
	private boolean inCheck = false;
	
	public FeatureNameTableCellRenderer() {
		
	}
	
	public void setCheck(boolean check) {
		inCheck = check;
	}
	
	public void setCurrent(FeatureName fn) {
		this.current = fn;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		

		if(inCheck && column==FeatureNameTableModel.COLUMN_ERROR) {
			ErrorObj eo = (ErrorObj)table.getValueAt(row, FeatureNameTableModel.COLUMN_ERROR);
			if(eo.getError()!=null && eo.getError().trim().length()>0)
				setForeground(Color.RED);
			else
				setForeground(Color.BLACK);
		}else if(current!=null && column==FeatureNameTableModel.COLUMN_NAME) {

			
			FeatureName fn = (FeatureName)table.getValueAt(row,FeatureNameTableModel.COLUMN_NAME );
			if(fn.getValue()!=null && fn.getValue().equals(current.getParent())) {
				setForeground(Color.RED);
			}else if(current.getValue()!=null && current.getValue().equals(fn.getParent())){
				setForeground(Color.BLUE);
			}else if(current.getParent()!=null && current.getParent().trim().length()>0 && current.getParent().equals(fn.getParent())) {
				setForeground(Color.GREEN);
			}else {
				setForeground(Color.BLACK);
			}
		}else{
			setForeground(Color.BLACK);
		}
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
