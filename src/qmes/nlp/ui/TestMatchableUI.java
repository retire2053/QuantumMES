package qmes.nlp.ui;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.config.storage.NConfigStorage;
import qmes.config.ui.NConfigUI;
import qmes.core.Model;
import qmes.nlp.storage.MatchableStorage;
import qmes.nlp.translate.def.Matchable;

public class TestMatchableUI {
	
	private static final Logger log = LoggerFactory.getLogger(TestMatchableUI.class);
	
	private MatchableStorage ss = null;
	private NConfigStorage ns = null;
	private Model model = null;
	
	public TestMatchableUI(Model model) {
		this.model = model;
		this.ss = model.getMatchableStorage();
		this.ns = model.getNConfigStorage();
	}
	
	private JFrame frame = null;
	private JDialog dialog = null;
	
	private JButton test = new JButton("测试匹配");
	private JButton clean = new JButton("清空");
	private JComboBox clazzcombo = new JComboBox();
	
	private JTextField input = new JTextField();
	private JTable match = new JTable();
	
	public void createAndShowGUI(JFrame frame) {

		log.info("start to build the GUI component");
		
		this.frame = frame;

		dialog = new JDialog(frame, "句式转换配置");
		dialog.setDefaultLookAndFeelDecorated(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		Container container = dialog.getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		JPanel panel = createSearchPanel();
		
		container.add(Box.createVerticalStrut(5));
		
		JPanel configPanel = createConfigPanel();
		
		container.add(panel);
		container.add(configPanel);
		
		container.add(Box.createVerticalStrut(5));

		dialog.setBounds(new Rectangle(300, 300, 800, 430));
		dialog.setVisible(true);
		log.info("finish building GUI component");
		
		initValues();
		initListeners();
	}
	
	private void initValues() {
		
		match.setModel(new DefaultTableModel(new String[] {"匹配行名称", "类", "匹配结果"}, 0));
		match.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		match.getColumnModel().getColumn(0).setPreferredWidth(120);
		match.getColumnModel().getColumn(1).setPreferredWidth(120);
		match.getColumnModel().getColumn(2).setPreferredWidth(500);
		
		BASEUI.initCombo(CONST.classes, clazzcombo);

		
	}
	
	private void initListeners() {
		
		clean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setText("");
				clazzcombo.setSelectedIndex(0);
			}
		});
		
		input.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if(e.getKeyCode()==10) {
					search();
				}
			}
			
		});
		
		test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
	
		
	}
	
	private JPanel createSearchPanel() {
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(input);
		panel.add(clazzcombo);
		panel.add(clean);
		panel.add(test);
		
		p.add(panel);
		p.add(new JScrollPane(match));
		
		return p;
	}
	
	private JPanel createConfigPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		return panel;
	}
	
	
	
	
	private void search() {
		
		DefaultTableModel dtm = BASEUI.cleanTable(match);
		
		if(input.getText()!=null && input.getText().trim().length()>0) {
			MatchableHelper mh = new MatchableHelper(model);
			List<Matchable> ms = ss.getMatchables();
			
			String clazzfilter = null;
			if(clazzcombo.getSelectedIndex()>0) {
				clazzfilter = ((Class)clazzcombo.getSelectedItem()).getName();
			}

			for(int i=0;i<ms.size();i++) {
				Matchable m = ms.get(i);
				if(clazzfilter==null || (clazzfilter!=null && clazzfilter.equals(m.getClazz().getName()))) {
					List<String> result = mh.match(input.getText(), ms.get(i));
					for(int p=0;p<result.size();p++) {
						dtm.addRow(new Object[] { m.getName(), m.getClazz().getSimpleName(), result.get(p) });
					}
				};
			}
		}

		match.setModel(dtm);
	}

}
