package qmes.config.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.config.def.NConfig;
import qmes.config.def.NConfigItem;
import qmes.config.storage.NConfigStorage;

public class NConfigUI {
	
	private static final Logger log = LoggerFactory.getLogger(NConfigUI.class);
	
	private JDialog dialog = null;
	
	private NConfig semanticConfig = null;
	
	private NConfigStorage ns = null;
	public void createAndShowGUI(JFrame parent, NConfigStorage ns) {

		this.ns = ns;
		
		semanticConfig  = ns.getConfig(NConfigStorage.CONFIG_SEMANTIC_TRANSFER);
		
		Container container = null;

		dialog = new JDialog(parent, "配置默认值");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		JPanel panel = createExecutionSetting();
		JPanel panel2 = createSemanticTrasfer();
		
		container.add(Box.createHorizontalStrut(5));
		container.add(panel);
		container.add(Box.createVerticalStrut(5));
		container.add(panel2);
		container.add(Box.createVerticalStrut(5));

		dialog.setBounds(200, 200, 500, 320);
		dialog.setVisible(true);
		
		initSemantic();
		initSetting();
	}
	
	
	private JTable table = null;
	private JButton add = new JButton("增加");
	private JButton delete = new JButton("删除");
	private JButton save = new JButton("保存并退出");
	
	private JPanel createSemanticTrasfer() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(Box.createHorizontalStrut(5));
		table = new JTable();
		JScrollPane jsp = new JScrollPane(table);
		p1.add(jsp);
		p1.add(Box.createHorizontalStrut(5));
		
		JPanel controller = new JPanel();
		controller.setLayout(new BoxLayout(controller, BoxLayout.X_AXIS));
		controller.add(Box.createHorizontalGlue());
		controller.add(add);
		controller.add(delete);
		controller.add(save);
		controller.add(Box.createHorizontalStrut(5));
		
		panel.add(p1);
		panel.add(controller);
		
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addConfigItem();
			}
		});
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeConfigItem();
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		
		return panel;
	}
	
	private JTextField minScore = new JTextField();
	private JTextField defaultText = new JTextField();
	private JCheckBox notShowNecessaryUnmatch = new JCheckBox();
	private JButton reset = new JButton("重置以上设置");
	
	
	private JPanel createExecutionSetting() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(Box.createHorizontalStrut(5));
		p1.add(BASEUI.alabel("最低被显示的案例的得分"));
		p1.add(minScore);
		p1.add(Box.createHorizontalStrut(5));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(Box.createHorizontalStrut(5));
		p2.add(BASEUI.alabel("当无匹配时的默认提示语言"));
		p2.add(defaultText);
		p2.add(Box.createHorizontalStrut(5));
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		p3.add(Box.createHorizontalStrut(5));
		notShowNecessaryUnmatch.setText("当必要条件未匹配时不显示");
		p3.add(notShowNecessaryUnmatch);
		p3.add(Box.createHorizontalGlue());
		p3.add(Box.createHorizontalStrut(5));
		
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
		p4.add(Box.createHorizontalGlue());
		p4.add(reset);
		p4.add(Box.createHorizontalStrut(5));
		
		panel.add(p1);
		panel.add(p2);
		panel.add(p3);
		panel.add(p4);
		
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetSetting();
			}
		});
		
		return panel;
	}
	
	private void resetSetting() {
		initSetting();
	}
	
	private void addConfigItem() {
		NConfigTableModel tm = (NConfigTableModel)table.getModel();
		
		NConfigItem m = new NConfigItem();
		m.setName("name");
		m.setValue("value");
		semanticConfig.getList().add(m);
		
		tm.addRow(new Object[] { m, m.getValue() });

	}
	
	private void removeConfigItem() {
		int row = table.getSelectedRow();
		if(row>=0) {
			NConfigItem m = (NConfigItem)table.getValueAt(row, NConfigTableModel.COLUMN_NAME);
			if(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(null, "确定要删除"+m.getName()+"这个匹配行吗？")) {
				semanticConfig.getList().remove(m);
				NConfigTableModel tm = (NConfigTableModel)table.getModel();
				tm.removeRow(row);
			}
		}
	}
	
	private void save() {
		
		try {
			String v = Integer.valueOf(minScore.getText()).toString();
			ns.setSetting(NConfigStorage.KEY_MIN_SCORE, v);
			
			v = defaultText.getText();
			ns.setSetting(NConfigStorage.KEY_DEFAULT_TEXT, v);
			
			v = String.valueOf(notShowNecessaryUnmatch.isSelected());
			ns.setSetting(NConfigStorage.KEY_NOT_SHOW_NECESSARY_UNMATCH, v);
			
			ns.saveStorage();
			
			JOptionPane.showMessageDialog(null, "设置已经被成功保存");
			dialog.dispose();
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			BASEUI.promptError(null);
		}
	}
	
	public void initSemantic() {
		
		NConfigTableModel tm = new NConfigTableModel(semanticConfig);
		table.setRowSorter(new TableRowSorter(tm));
		table.setModel(tm);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(NConfigTableModel.COLUMN_NAME).setPreferredWidth(200);
		table.getColumnModel().getColumn(NConfigTableModel.COLUMN_VALUE).setPreferredWidth(200);
	}
	
	private void initSetting() {
		String minScoreText = ns.getSetting(NConfigStorage.KEY_MIN_SCORE);
		minScore.setText(minScoreText);
		
		String defaultTextString = ns.getSetting(NConfigStorage.KEY_DEFAULT_TEXT);
		defaultText.setText(defaultTextString);
		
		String notShowNeccUnmatch = ns.getSetting(NConfigStorage.KEY_NOT_SHOW_NECESSARY_UNMATCH);
		if(Boolean.valueOf(notShowNeccUnmatch).booleanValue()) {
			notShowNecessaryUnmatch.setSelected(true);
		}else {
			notShowNecessaryUnmatch.setSelected(false);
		}
	}
	

}

class NConfigTableModel extends DefaultTableModel {
	
	public static int COLUMN_NAME = 0;
	public static int COLUMN_VALUE = 1;
		
	private Object[][] getData(NConfig config){
		Object[][] data = new Object[config.getList().size()][2];
		if(config.getList().size()>0) {
			for(int i=0;i<config.getList().size();i++) {
				NConfigItem m = config.getList().get(i);
				data[i][0] = m;
				data[i][1] = m.getValue();
			}
		}
		return data;
	}
	
	private String[] getColumnNames() {
		return new String[] {"值","语义上替换为"};
	}
	

	NConfig config;
	public NConfigTableModel(NConfig config) {
		this.config = config;
		Object[][] data = getData(config);
		this.setDataVector(data, getColumnNames());
	}

	public void setValueAt(Object aValue, int row, int column) {
		
		NConfigItem m = (NConfigItem)getValueAt(row, COLUMN_NAME);
		if(column==COLUMN_NAME) {
			m.setName((String)aValue);
			super.setValueAt(m, row, column);
		}else if(column==COLUMN_VALUE) {
			m.setValue((String)aValue);
			super.setValueAt(m.getValue(), row, column);
		}
		else super.setValueAt(aValue, row, column);

	}
	

}
