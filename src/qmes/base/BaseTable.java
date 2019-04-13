package qmes.base;

import java.awt.event.MouseEvent;

import javax.swing.JTable;

public class BaseTable extends JTable {

	public String getToolTipText(MouseEvent e) {
		java.awt.Point p = e.getPoint();
		int row = convertRowIndexToModel(rowAtPoint(p));
		int col = convertColumnIndexToModel(columnAtPoint(p));
		Object o = getValueAt(row, col);
		return o != null ? o.toString() : "";
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	
}
