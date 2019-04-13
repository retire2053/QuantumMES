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

import qmes.base.CONST;
import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;
import qmes.word.def.Word;
import qmes.word.storage.FeatureNameStorage;
import qmes.word.storage.FeatureStateStorage;

public class FeatureNameTable extends JPanel{
	
	private FeatureNameTableCellRenderer renderer = new FeatureNameTableCellRenderer();
	private JTable table = null;
	
	private JButton check = new JButton("开始检测错误");
	private JButton add = new JButton("增加");
	private JButton remove = new JButton("删除");
	private JButton save = new JButton("保存");
	private JLabel info = new JLabel();
	
	
	FeatureNameStorage wsfn = null;
	FeatureStateStorage wsfs = null;
	Set<String> classset = new HashSet<String>();
	Set<String> groupset = new HashSet<String>();
	JScrollPane jsp = null;
	
	public FeatureNameTable(FeatureNameStorage wsfn, FeatureStateStorage wsfs) {
		
		this.wsfn = wsfn;
		this.wsfs = wsfs;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		table = new JTable() {
			public boolean isCellEditable(int row, int column) {
				if (column == FeatureNameTableModel.COLUMN_ERROR)
					return false;
				else
					return true;
			}

			public String getToolTipText(MouseEvent e) {
				java.awt.Point p = e.getPoint();
				int row = convertRowIndexToModel(rowAtPoint(p));
				int col = convertColumnIndexToModel(columnAtPoint(p));
				if(col==FeatureNameTableModel.COLUMN_ERROR) {
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
		
		if(classset.size()==0) {
			for(int i=0;i<CONST.classes.length;i++) {
				classset.add(CONST.classes[i].getName());
			}
		}
		
		groupset.clear();
		Iterator<Word> itr = wsfs.listWords().iterator();
		while (itr.hasNext()) {
			FeatureState fs = (FeatureState) itr.next();
			groupset.add(fs.getGroup());
		}
	}
	
	public void updateData(FeatureNameStorage ws, List<FeatureName> fns) {
		
		info.setText("共搜索出"+fns.size()+"条结果");
		
		FeatureNameTableModel tm = new FeatureNameTableModel(ws, fns);
		table.setRowSorter(new TableRowSorter(tm));
		table.setModel(tm);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(FeatureNameTableModel.COLUMN_ERROR).setPreferredWidth(30);
		table.getColumnModel().getColumn(FeatureNameTableModel.COLUMN_CLASS).setPreferredWidth(200);
		table.getColumnModel().getColumn(FeatureNameTableModel.COLUMN_NAME).setPreferredWidth(120);
		table.getColumnModel().getColumn(FeatureNameTableModel.COLUMN_GROUP).setPreferredWidth(120);
		table.getColumnModel().getColumn(FeatureNameTableModel.COLUMN_SYNONYM).setPreferredWidth(200);
		table.getColumnModel().getColumn(FeatureNameTableModel.COLUMN_PARENT).setPreferredWidth(120);
		
		toggleErrorCheck(false);
	}
	
	boolean checkStatus = false;
	
	private void toggleErrorCheck(boolean b) {
		FeatureNameTableModel tm = (FeatureNameTableModel)table.getModel();
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
				FeatureNameTableModel tm = (FeatureNameTableModel) table.getModel();

				FeatureName fn = new FeatureName();
				fn.setValue(WordPartConst.DEFAULT_NAME);
				fn.getFeatureClasses().add(WordPartConst.DEFAULT_CLASS);
				fn.getGroups().add(WordPartConst.DEFAULT_GROUP);

				if (wsfn.addWord(fn)) {
					tm.addRow(new Object[] { new ErrorObj(), WordPartConst.DEFAULT_CLASS, fn, WordPartConst.DEFAULT_GROUP, WordPartConst.DEFAULT_SYNONYM, WordPartConst.DEFAULT_PARENT});
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
					FeatureName fn = (FeatureName)table.getValueAt(row, FeatureNameTableModel.COLUMN_NAME);
					if(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(null, "确定要删除"+fn.getValue()+"这个特征名吗？")) {
						wsfn.deleteWord(fn);
						FeatureNameTableModel tm = (FeatureNameTableModel)table.getModel();
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
					FeatureName fn = (FeatureName)table.getValueAt(row,FeatureNameTableModel.COLUMN_NAME);
					if(fn!=null) {
						renderer.setCurrent(fn);
						FeatureNameTable.this.repaint();
					}
				}
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wsfn.saveStorage();
				JOptionPane.showMessageDialog(null, "所有特征名已经保存到硬盘上");
			}
		});
	}
	
	
	private void updateErrorStatus(FeatureNameTableModel tm) {
		
		if(tm.getRowCount()==0)return;
		
		for(int i=0;i<tm.getRowCount();i++) {
			FeatureName fn = (FeatureName)tm.getValueAt(i, FeatureNameTableModel.COLUMN_NAME);
			String err = checkError(fn);
			if(err.length()==0)tm.setValueAt(new ErrorObj(), i, FeatureNameTableModel.COLUMN_ERROR);
			else {
				tm.setValueAt(new ErrorObj(err), i, FeatureNameTableModel.COLUMN_ERROR);
			}
		}
		
	}
	
	private void cleanErrorStatus(FeatureNameTableModel tm) {
		
		prepare();
		
		if(tm.getRowCount()==0)return;
		for(int i=0;i<tm.getRowCount();i++) {
			tm.setValueAt(new ErrorObj(), i, FeatureNameTableModel.COLUMN_ERROR);
		}
	}
	
	private String checkError(FeatureName fn) {
		
		StringBuffer sb = new StringBuffer();
		if(fn.getValue()==null || fn.getValue().length()==0 || WordPartConst.DEFAULT_NAME.equals(fn.getValue())) {
			sb.append("词名非法 ");
		}
		if(fn.getParent()!=null && wsfn.findWord(fn.getParent())==null) {
			sb.append("上级词不存在 ");
		}
		if(fn.getGroups()!=null && fn.getGroups().size()>0) {
			for(int i=0;i<fn.getGroups().size();i++) {
				String g = fn.getGroups().get(i);
				if(g!=null && g.length()>0 && (!groupset.contains(g) || WordPartConst.DEFAULT_GROUP.equals(g)))
					sb.append("非法组"+fn.getGroups().get(i)+" ");
			}
		}
		
		if(fn.getFeatureClasses().size()==0) {
			sb.append("没有设置类 ");
		}else {
			for(int i=0;i<fn.getFeatureClasses().size();i++) {
				String c = fn.getFeatureClasses().get(i);
				if(!classset.contains(c) || WordPartConst.DEFAULT_CLASS.equals(c)) {
					sb.append("非法类名"+fn.getFeatureClasses().get(i)+" ");
				}
			}
		}
		return sb.toString();
	}

	

}
