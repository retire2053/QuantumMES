package qmes.word.ui.part;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableRowSorter;

import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;
import qmes.word.def.Word;
import qmes.word.storage.FeatureNameStorage;
import qmes.word.storage.FeatureStateStorage;
import qmes.word.storage.WordStorage;

public class FeatureStateTable extends JPanel{
	
	private FeatureStateTableCellRenderer renderer = new FeatureStateTableCellRenderer();
	private JTable table = null;
	
	private JButton check = new JButton("开始检测错误");
	private JButton add = new JButton("增加");
	private JButton remove = new JButton("删除");
	private JButton save = new JButton("保存");
	private JLabel info = new JLabel();
	
	FeatureNameStorage wsfn = null;
	FeatureStateStorage wsfs = null;
	Set<String> groupset = new HashSet<String>();
	Set<String> fnset = new HashSet<String>();
	JScrollPane jsp = null;
	
	public FeatureStateTable(FeatureNameStorage wsfn, FeatureStateStorage wsfs) {
		
		this.wsfn = wsfn;
		this.wsfs = wsfs;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		table = new JTable() {
			public boolean isCellEditable(int row, int column) {
				if (column == FeatureStateTableModel.COLUMN_ERROR)
					return false;
				else
					return true;
			}

			public String getToolTipText(MouseEvent e) {
				java.awt.Point p = e.getPoint();
				int row = convertRowIndexToModel(rowAtPoint(p));
				int col = convertColumnIndexToModel(columnAtPoint(p));
				if(col==FeatureStateTableModel.COLUMN_ERROR) {
					ErrorObj o = (ErrorObj)getValueAt(row, col);
					return o.getError();
				}else {
					Object o = getValueAt(row, col);
					return o != null ? o.toString() : "";
				}
				
				
			}

		};
		jsp = new JScrollPane(table);
		add(jsp);
		
		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
		controls.add(Box.createHorizontalGlue());
		controls.add(info);
		controls.add(check);
		controls.add(add);
		controls.add(remove);
		controls.add(save);
		
		add(controls);
		
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Object.class, renderer);
		
		initListeners();
	}
	
	private void prepare() {
		groupset.clear();
		fnset.clear();
		Iterator<Word> itr = wsfn.listWords().iterator();
		while (itr.hasNext()) {
			FeatureName fn = (FeatureName) itr.next();
			groupset.addAll(fn.getGroups());
			fnset.add(fn.getValue());
		}
	}
	
	public void updateData(FeatureStateStorage ws, List<FeatureState> fss) {
		
		info.setText("共搜索出"+fss.size()+"条结果");
		
		FeatureStateTableModel tm = new FeatureStateTableModel(ws, fss);
		table.setRowSorter(new TableRowSorter(tm));
		table.setModel(tm);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(FeatureStateTableModel.COLUMN_ERROR).setPreferredWidth(30);
		table.getColumnModel().getColumn(FeatureStateTableModel.COLUMN_NAME).setPreferredWidth(120);
		table.getColumnModel().getColumn(FeatureStateTableModel.COLUMN_GROUP).setPreferredWidth(120);
		table.getColumnModel().getColumn(FeatureStateTableModel.COLUMN_SYNONYM).setPreferredWidth(300);
		table.getColumnModel().getColumn(FeatureStateTableModel.COLUMN_PARENT).setPreferredWidth(120);
		
		
		toggleErrorCheck(false);
	}
	
	boolean checkStatus = false;
	
	private void toggleErrorCheck(boolean b) {
		FeatureStateTableModel tm = (FeatureStateTableModel)table.getModel();
		if(b) {
			check.setText("停止检测错误");
			checkStatus = true;
			renderer.setCheck(true);
			updateErrorStatus(tm);
		}else {
			check.setText("开始检测错误");
			checkStatus = false;
			renderer.setCheck(false);
			cleanErrorStatus(tm);
		}
	}
	
	private void initListeners() {
		
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleErrorCheck(!checkStatus);
			}
		});
		
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FeatureStateTableModel tm = (FeatureStateTableModel) table.getModel();
				FeatureState fs = new FeatureState();
				fs.setValue(WordPartConst.DEFAULT_NAME);
				fs.setGroup(WordPartConst.DEFAULT_GROUP);

				if (wsfs.addWord(fs)) {
					tm.addRow(new Object[] { new ErrorObj(), fs, WordPartConst.DEFAULT_GROUP, WordPartConst.DEFAULT_SYNONYM });
					int rowcount = tm.getRowCount();
					table.setRowSelectionInterval(rowcount-1, rowcount-1);
					table.repaint();
					jsp.getVerticalScrollBar().setValue(jsp.getVerticalScrollBar().getMaximum());
					
				} else {
					JOptionPane.showMessageDialog(null, "无法添加新的词汇，可能是重名的原因，或有未编辑的词语");
				}
			}
		});
		
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row>=0) {
					FeatureState fs = (FeatureState)table.getValueAt(row, FeatureStateTableModel.COLUMN_NAME);
					if(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(null, "确定要删除"+fs.getValue()+"这个特征值吗？")) {
						wsfs.deleteWord(fs);
						
						FeatureStateTableModel tm = (FeatureStateTableModel)table.getModel();
						
						boolean lastRow = (row==tm.getRowCount()-1)?true:false;
						tm.removeRow(row);
						if(lastRow) {
							if(tm.getRowCount()>0) {
								table.setRowSelectionInterval(row-1, row-1);
							}
						}else {
							table.setRowSelectionInterval(row, row);
						}
						table.repaint();
						
					}
				}
			}
		});
		
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int row = table.getSelectedRow();
				if(row>=0) {
					FeatureState fs = (FeatureState)table.getValueAt(row,FeatureStateTableModel.COLUMN_NAME);
					if(fs!=null) {
						renderer.setCurrent(fs);
						FeatureStateTable.this.repaint();
					}
				}
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wsfs.saveStorage();
				JOptionPane.showMessageDialog(null, "所有特征值已经保存到硬盘上");
			}
		});
	}

	
	private void updateErrorStatus(FeatureStateTableModel tm) {
		
		if(tm.getRowCount()==0)return;
		
		prepare();
		
		for(int i=0;i<tm.getRowCount();i++) {
			FeatureState fs = (FeatureState)tm.getValueAt(i, FeatureStateTableModel.COLUMN_NAME);
			String err = checkError(fs);
			if(err.length()==0)tm.setValueAt(new ErrorObj(), i, FeatureStateTableModel.COLUMN_ERROR);
			else {
				tm.setValueAt(new ErrorObj(err), i, FeatureStateTableModel.COLUMN_ERROR);
			}
		}
		
	}
	
	private void cleanErrorStatus(FeatureStateTableModel tm) {

		if(tm.getRowCount()==0)return;
		for(int i=0;i<tm.getRowCount();i++) {
			tm.setValueAt(new ErrorObj(), i, FeatureStateTableModel.COLUMN_ERROR);
		}
	}
	
	private String checkError(FeatureState fs) {
		
		StringBuffer sb = new StringBuffer();
		if(fs.getValue()==null || fs.getValue().length()==0 || WordPartConst.DEFAULT_NAME.equals(fs.getValue())) {
			sb.append("词名非法 ");
		}
		String g = fs.getGroup();
		if(g!=null && g.trim().length()>0) {
			if(!groupset.contains(g) || WordPartConst.DEFAULT_GROUP.equals(g))
					sb.append("非法组"+g+" ");
		}
		String p = fs.getParent();
		if(p!=null && p.trim().length()>0) {
			if(!fnset.contains(g))sb.append("非法上级"+p+" ");
		}
		return sb.toString();
	}

	

}
