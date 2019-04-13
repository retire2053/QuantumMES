
package qmes.indicator.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.indicator.calc.VALUE;
import qmes.indicator.calc.VALUES;
import qmes.indicator.def.IndicatorDef;
import qmes.indicator.def.RegionDef;

public class IndicatorUI extends BASEUI {

	private static final Logger log = LoggerFactory.getLogger(IndicatorUI.class);
	private IndicatorDef current = null;
	private static String NAME_DEF = "新创建指标";
	private static String COMMENT_DEF = "请修改备注";
	private static String REGION_NAME = "区间名称";
	private static String REGION_STATE = "状态名称";

	JComboBox defcombo = new JComboBox();

	JTextField name = new JTextField();
	JTextField comment = new JTextField();
	JComboBox unit = new JComboBox();
	JComboBox surge = new JComboBox();
	JComboBox recent = new JComboBox();
	JTable regions = new JTable();

	JButton addregion = new JButton("增加区间");
	JButton removeregion = new JButton("删除区间");

	JButton add = new JButton("增加新指标");
	JButton remove = new JButton("删除当前指标");

	JButton save = new JButton("保存");

	JTextField testvalue = new JTextField();
	JButton test = new JButton("测试区间");
	JButton testSurge = new JButton("测试波动");

	private Model model = null;
	private JFrame frame = null;
	private JDialog dialog = null;

	public void createAndShowGUI(JFrame parent, Model model) {

		this.model = model;

		
		Container container = null;
		
		this.frame = parent;
		dialog = new JDialog(parent, "配置定量指标");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		JPanel p0 = new JPanel();
		p0.setLayout(new BoxLayout(p0, BoxLayout.X_AXIS));
		p0.add(Box.createHorizontalStrut(5));
		p0.add(alabel("指标定义列表"));
		p0.add(defcombo);
		p0.add(Box.createHorizontalStrut(5));

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createHorizontalStrut(5));
		p.add(alabel("名称"));
		p.add(name);
		p.add(alabel("注释"));
		p.add(comment);
		p.add(Box.createHorizontalStrut(5));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(Box.createHorizontalStrut(5));
		p2.add(alabel("单位"));
		p2.add(unit);
		p2.add(alabel("大波动阈值"));
		p2.add(surge);
		p2.add(Box.createHorizontalStrut(5));

		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		p3.add(Box.createHorizontalStrut(5));
		p3.add(alabel("\"最近\"是向前最多算多少个月？"));
		p3.add(recent);
		p3.add(Box.createHorizontalStrut(5));

		container.add(Box.createVerticalStrut(5));
		container.add(p0);
		container.add(alabel(" "));
		container.add(p);
		container.add(p2);
		container.add(p3);

		JPanel p5 = new JPanel();
		p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
		p5.add(addregion);
		p5.add(removeregion);

		container.add(p5);
		
		JPanel p8 = new JPanel();
		p8.setLayout(new BoxLayout(p8, BoxLayout.X_AXIS));
		p8.add(Box.createHorizontalStrut(5));
		p8.add(new JScrollPane(regions));
		p8.add(Box.createHorizontalStrut(5));
		
		container.add(p8);

		JPanel p6 = new JPanel();
		p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));
		p6.add(Box.createHorizontalStrut(5));
		p6.add(alabel("测试区间/波动"));
		p6.add(testvalue);
		p6.add(test);
		p6.add(testSurge);
		p6.add(Box.createHorizontalStrut(5));
		container.add(p6);

		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));

		p4.add(add);
		p4.add(remove);
		p4.add(save);

		container.add(p4);
		container.add(Box.createVerticalStrut(5));

		dialog.setBounds(300, 300, 700, 420);
		dialog.setVisible(true);

	}

	private String wrapper(String text) {
		if (text != null)
			return text;
		else
			return "";
	}

	private void fillObject(IndicatorDef id) {
		if (id != null) {
			name.setText(wrapper(id.getName()));
			comment.setText(wrapper(id.getComment()));
			setCombo(id.getUnit().getUnit(), unit);
			setCombo(id.getSurge().getSurge(), surge);
			setCombo(id.getRecentDef().getRecent(), recent);

			if (regions.getRowCount() > 0)
				for (int i = regions.getRowCount() - 1; i >= 0; i--) {
					((DefaultTableModel) regions.getModel()).removeRow(i);
				}
			for (int i = 0; i < id.getRegions().size(); i++) {
				RegionDef r = id.getRegions().get(i);

				Vector v = new Vector();
				v.add(wrapper(r.getName()));
				v.add(wrapper(r.getState()));
				v.add(r.getLower());
				v.add(r.getUpper());
				if (r.getParent() != null)
					v.add(r.getParent().getName());
				((DefaultTableModel) regions.getModel()).addRow(v);
			}

		} else {
			name.setText("");
			comment.setText("");
			unit.setSelectedIndex(0);
			surge.setSelectedIndex(0);
			recent.setSelectedIndex(0);

		}

	}

	private void updateStatus() {
		if (current == null) {
			name.setEnabled(false);
			comment.setEnabled(false);
			unit.setEnabled(false);
			surge.setEnabled(false);
			recent.setEnabled(false);
			regions.setEnabled(false);
			remove.setEnabled(false);
			addregion.setEnabled(false);
			removeregion.setEnabled(false);
		} else {
			name.setEnabled(true);
			comment.setEnabled(true);
			unit.setEnabled(true);
			surge.setEnabled(true);
			recent.setEnabled(true);
			regions.setEnabled(true);
			remove.setEnabled(true);
			addregion.setEnabled(true);
			removeregion.setEnabled(true);

		}
	}

	private void event() {
		int row = regions.getSelectedRow();
		int column = regions.getSelectedColumn();

		String value = (String) regions.getModel().getValueAt(row, column);
		System.out.println("TableChange:row=" + row + ",col=" + column + ",value=" + value + ",type="
				+ value.getClass().getSimpleName());
		if (value != null) {
			RegionDef region = current.getRegions().get(row);
			if (column == 0)
				region.setName((String) value);
			if (column == 1)
				region.setState((String) value);
			if (column == 2)
				region.setLower(Double.valueOf(value));
			if (column == 3)
				region.setUpper(Double.valueOf(value));

			if (column == 4)
				if (value.length()>0) {
					for (int i = 0; i < current.getRegions().size(); i++) {
						RegionDef r = current.getRegions().get(i);
						if (r != null && r != region && value.equals(r.getName())) {
							region.setParent(r);
						}
					}
				}else {
					region.setParent(null);
				}
		}
	}
	private void updateDefCombo() {

		defcombo.removeAllItems();

		List<IndicatorDef> list = model.getIndicatorDefs();
		defcombo.addItem("请选择");
		for (int i = 0; i < list.size(); i++) {
			defcombo.addItem(list.get(i).getName() + " [单位=" + list.get(i).getUnit().getUnit() + "]");
		}
		if (current == null)
			defcombo.setSelectedIndex(0);
		else {
			int index = list.indexOf(current);
			if (index >= 0) {
				defcombo.setSelectedIndex(index + 1);
			}
		}
	}

	public void init() throws Exception {

		defcombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (defcombo.getSelectedIndex() > 0) {
					current = model.getIndicatorDefs().get(defcombo.getSelectedIndex() - 1);
					fillObject(current);
					updateStatus();
				}
			}
		});
		updateDefCombo();
		updateStatus();

		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IndicatorDef id = new IndicatorDef();
				id.setName(NAME_DEF);
				id.setComment(COMMENT_DEF);
				model.addIndicatorDef(id);
				current = id;
				fillObject(current);
				updateDefCombo();
				updateStatus();
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (current != null) {
					model.removeIndicatorDef(current);
					current = null;
					fillObject(current);
					updateDefCombo();
					updateStatus();
				}
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					model.storeIndicatorDefs();
					JOptionPane.showMessageDialog(null, "模型已经被保存在\"" + model.getIndicatorDefSubPath() + "\"", "保存成功",
							JOptionPane.YES_OPTION);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		addregion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegionDef region = new RegionDef();
				current.addRegion(region);
				Vector v = new Vector();
				v.add(REGION_NAME);
				v.add(REGION_STATE);
				v.add(00);
				v.add(00);
				v.add("");
				((DefaultTableModel) regions.getModel()).addRow(v);
			}
		});
		removeregion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = regions.getSelectedRow();
				if (current != null) {
					current.getRegions().remove(row);
				}
				((DefaultTableModel) regions.getModel()).removeRow(row);

			}
		});

		unit.addItem("");
		for(int i=0;i<CONST.units.length;i++) {
			unit.addItem(CONST.units[i]);
		}

		Object[] surges = new Object[] { "", 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.16, 1.38, 1.41 };
		for (Object s : surges)
			surge.addItem(s);

		Object[] recents = { "", -1.0, -2.0, -3.0, -6.0, -12.0 };
		for (Object s : recents)
			recent.addItem(s);

		String[] cols = { "名称", "状态", "区间下限", "区间上限", "父区间" };

		DefaultTableModel mm = new DefaultTableModel(cols, 0);
		regions.setModel(mm);

		regions.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				event();
			}
		});
		//点击文本框清空内容
		regions.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent e){
	            	int row = regions.getSelectedRow();
	        		int column = regions.getSelectedColumn();
	        		DefaultTableModel model = (DefaultTableModel) regions.getModel();//获取defaulttablemodel
	        		Object val = model.getValueAt(row, column);//根据行号和列号，获取某个单元格的值
	        		if(REGION_NAME.equals(val) || REGION_STATE.equals(val))
	        			model.setValueAt("", row, column);//修改某单元格的值  
	        }  
        });

		name.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				current.setName(name.getText());
			}
		});
		//点击文本框清空内容
		name.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent e){
            		
            		if(name.getText().equals(NAME_DEF))
            			name.setText("");
            }  
        });
		comment.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				current.setComment(comment.getText());
			}
		});
		//点击文本框清空内容
		comment.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent e){
            	if(comment.getText().equals(COMMENT_DEF))
            		comment.setText("");
            }  
        });
		unit.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (unit.getSelectedIndex() > 0) {
					current.getUnit().setUnit((String) unit.getSelectedItem());
				}
			}
		});
		recent.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (recent.getSelectedIndex() > 0) {
					current.getRecentDef().setRecent((Double) recent.getSelectedItem());
				}
			}
		});
		surge.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (surge.getSelectedIndex() > 0) {
					current.getSurge().setSurge((Double) surge.getSelectedItem());
				}
			}
		});
		test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (current != null) {
					try {
						String value = testvalue.getText();
						double v = Double.valueOf(value);
						VALUE output = new VALUE(current, v);
						JOptionPane.showMessageDialog(frame, output.toString(), "测试结果", JOptionPane.YES_OPTION);
					}catch(Exception ex) {
						ex.printStackTrace();
						log.error(ex.getMessage());
						BASEUI.promptError(frame);
					}
				}
			}
		});
		
		testSurge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (current != null) {
					try {
						String value = testvalue.getText();
						String[] v = value.split(" ");
						if (v != null && v.length >= 2) {
							List<Double> lv = new ArrayList<Double>();
							for (int i = 0; i < v.length; i++) {
								if (v[i] != null && v[i].length() > 0)
									lv.add(Double.parseDouble(v[i]));
							}

							if (lv.size() >= 2) {
								double[] v2 = new double[lv.size()];
								for (int p = 0; p < lv.size(); p++) {
									v2[p] = lv.get(p);
								}

								double m = VALUES.mutation(v2);
								m = (double) Math.round(m * 100) / 100;

								JOptionPane.showMessageDialog(frame, "变异系数=" + m);
							}else {
								JOptionPane.showMessageDialog(frame, "请至少输入两个值，以计算变异系数");
							}
						}else {
							JOptionPane.showMessageDialog(frame, "请至少输入两个值，以计算变异系数");
						}
					}catch(Exception ex) {
						ex.printStackTrace();
						log.error(ex.getMessage());
						BASEUI.promptError(frame);
					}
				}

			}
		});

	}
}