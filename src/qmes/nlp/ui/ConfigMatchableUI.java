package qmes.nlp.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.core.Model;
import qmes.model.Diagnose;
import qmes.nlp.storage.MatchableStorage;
import qmes.nlp.translate.def.Expression;
import qmes.nlp.translate.def.Matchable;

public class ConfigMatchableUI {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigMatchableUI.class);

	private JDialog dialog = null;
	private JTable table = null;
	private JButton add = new JButton("增加");
	private JButton delete = new JButton("删除");
	private JButton save = new JButton("保存");
	
	private Model model = null;
	public ConfigMatchableUI(Model model) {
		this.model = model;
	}
	
	private MatchableStorage ss = null;
	public void createAndShowGUI(JFrame parent) {

		ss = model.getMatchableStorage();
		
		Container container = null;

		dialog = new JDialog(parent, "句式转换配置");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		table = new JTable();
		container.add(Box.createVerticalStrut(5));
		container.add(new JScrollPane(table));
		container.add(Box.createVerticalStrut(5));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(add);
		panel.add(delete);
		panel.add(save);
		panel.add(Box.createHorizontalStrut(5));
		container.add(panel);
		container.add(Box.createVerticalStrut(5));

		dialog.setBounds(200, 200, 1000, 480);
		dialog.setVisible(true);
		
		initListeners();
		initValues();

	}
	
	private void initListeners() {
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addMatchable();
			}
		});
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeMatchable();
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
	}
	
	private void initValues() {
		updateData(ss, ss.getMatchables());
	}
	
	private void addMatchable() {
		MatchableTableModel tm = (MatchableTableModel)table.getModel();
		
		Matchable m = new Matchable();
		m.setName("name");
		m.setClazz(Diagnose.class);
		m.getDefaults().get(Matchable.INDEX_NAME).getValues().add("value1");
		m.getDefaults().get(Matchable.INDEX_NAME).getValues().add("value2");
		m.getDefaults().get(Matchable.INDEX_STATE).getValues().add("value1");
		m.getDefaults().get(Matchable.INDEX_STATE).getValues().add("value2");
		m.getDefaults().get(Matchable.INDEX_TIME).getValues().add("0");
		m.getDefaults().get(Matchable.INDEX_TIMESPAN).getValues().add("-6 -1");
		m.getDefaults().get(Matchable.INDEX_UNIT).getValues().add("U");
		m.setExpr(new Expression("expression"));
		
		
		if(ss.addMatchable(m)) {
			tm.addRow(new Object[] {
					m,
					"qmes.model.Diagnose",
					"expression",
					"value1 value2",
					"value1 value2",
					"0",
					"-6 -1",
					"U"
					});
		}else {
			JOptionPane.showMessageDialog(null, "无法添加新的可匹配行");
		}
	}
	
	private void removeMatchable() {
		int row = table.getSelectedRow();
		if(row>=0) {
			Matchable m = (Matchable)table.getValueAt(row, MatchableTableModel.COLUMN_NAME);
			if(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(null, "确定要删除"+m.getName()+"这个匹配行吗？")) {
				ss.removeMatchable(m);
				MatchableTableModel tm = (MatchableTableModel)table.getModel();
				tm.removeRow(row);
			}
		}
	}
	
	private void save() {
		ss.saveStorage();
		JOptionPane.showMessageDialog(null, "配置的可匹配行已经被存储");
	}
	
	public void updateData(MatchableStorage ss, List<Matchable> matchables) {
		
		MatchableTableModel tm = new MatchableTableModel(matchables, ss);
		table.setRowSorter(new TableRowSorter(tm));
		table.setModel(tm);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(MatchableTableModel.COLUMN_NAME).setPreferredWidth(60);
		table.getColumnModel().getColumn(MatchableTableModel.COLUMN_SCOPE).setPreferredWidth(150);
		table.getColumnModel().getColumn(MatchableTableModel.COLUMN_EXP).setPreferredWidth(300);
		table.getColumnModel().getColumn(MatchableTableModel.COLUMN_NAMEDEFAULT).setPreferredWidth(200);
		table.getColumnModel().getColumn(MatchableTableModel.COLUMN_STATEDEFAULT).setPreferredWidth(200);
		
	}

}

class MatchableTableModel extends DefaultTableModel {
	
	public static int COLUMN_NAME = 0;
	public static int COLUMN_SCOPE = 1;
	public static int COLUMN_EXP = 2;
	public static int COLUMN_NAMEDEFAULT = 3;
	public static int COLUMN_STATEDEFAULT = 4;
	public static int COLUMN_TIMEDEFAULT = 5;
	public static int COLUMN_TIMESPANDEFAULT = 6;
	public static int COLUMN_UNITDEFAULT = 7;
		
	private Object[][] getData(List<Matchable> matchables){
		Object[][] data = new Object[matchables.size()][8];
		if(matchables.size()>0) {
			for(int i=0;i<matchables.size();i++) {
				Matchable m = matchables.get(i);
				data[i][0] = m;
				data[i][1] = m.getClazz().getName();
				data[i][2] = m.getExpr();
				data[i][3] = m.getDefaults().get(Matchable.INDEX_NAME);
				data[i][4] = m.getDefaults().get(Matchable.INDEX_STATE);
				data[i][5] = m.getDefaults().get(Matchable.INDEX_TIME);
				data[i][6] = m.getDefaults().get(Matchable.INDEX_TIMESPAN);
				data[i][7] = m.getDefaults().get(Matchable.INDEX_UNIT);
			}
		}
		return data;
	}
	
	private String[] getColumnNames() {
		return new String[] {"表达式名","范畴","表达式","${fn} 特征名默认","${fs} 特征值默认","${time} 时间点默认","${timespan} 区间默认", "${u} 单位默认"};
	}
	
	MatchableStorage ss;
	public MatchableTableModel(List<Matchable> matchables, MatchableStorage ss) {
		this.ss = ss;
		Object[][] data = getData(matchables);
		this.setDataVector(data, getColumnNames());
	}


	public void setValueAt(Object aValue, int row, int column) {
		
		Matchable m = (Matchable)getValueAt(row, COLUMN_NAME);
		if(column==COLUMN_NAME) {
			ss.updateScope(m, MatchableStorage.FIELD_NAME, aValue);
			super.setValueAt(m, row, column);
		}else if(column==COLUMN_SCOPE) {
			ss.updateScope(m,  MatchableStorage.FIELD_SCOPE, aValue);
			super.setValueAt(m.getClazz().getName(), row, column);
		}else if(column==COLUMN_EXP) {
			ss.updateScope(m, MatchableStorage.FIELD_EXP, aValue);
			super.setValueAt(m.getExpr(), row, column);
		}else if(column==COLUMN_NAMEDEFAULT) {
			ss.updateScope(m, MatchableStorage.FIELD_NAMEDEFAULT, aValue);
			super.setValueAt(m.getDefaults().get(Matchable.INDEX_NAME), row, column);
		}else if(column==COLUMN_STATEDEFAULT) {
			ss.updateScope(m, MatchableStorage.FIELD_STATEDEFAULT, aValue);
			super.setValueAt(m.getDefaults().get(Matchable.INDEX_STATE), row, column);
		}else if(column==COLUMN_TIMEDEFAULT) {
			ss.updateScope(m, MatchableStorage.FIELD_TIMEDEFAULT, aValue);
			super.setValueAt(m.getDefaults().get(Matchable.INDEX_TIME), row, column);
		}else if(column==COLUMN_TIMESPANDEFAULT) {
			ss.updateScope(m, MatchableStorage.FIELD_TIMESPANDEFAULT, aValue);
			super.setValueAt(m.getDefaults().get(Matchable.INDEX_TIMESPAN), row, column);
		}else if(column==COLUMN_UNITDEFAULT) {
			ss.updateScope(m, MatchableStorage.FIELD_UNITDEFAULT, aValue);
			super.setValueAt(m.getDefaults().get(Matchable.INDEX_UNIT), row, column);
		}
		
		else super.setValueAt(aValue, row, column);
	}
	

}
