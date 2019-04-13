package qmes.core;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.InfoUI;
import qmes.base.ReleaseNote;
import qmes.cases.def.CaseDef;
import qmes.cases.storage.SortTag;
import qmes.cases.ui.CaseInfoUI;
import qmes.cases.ui.CasesUI;
import qmes.config.ui.NConfigUI;
import qmes.indicator.ui.IndicatorUI;
import qmes.nlp.ui.ConfigMatchableUI;
import qmes.nlp.ui.TestMatchableUI;
import qmes.rule.execution.ExecutionProcess;
import qmes.rule.execution.result.StackedMatchResult;
import qmes.rule.execution.ui.CaseExecutionUI;
import qmes.rule.ui.RuleManage;
import qmes.word.search.ui.SearchUI;
import qmes.word.ui.WordUI2;

public class MainFrame {
	
	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

	protected JFrame frame = null;
	protected Model model = null;
	private CaseDef currentCase = null;
	private JLabel status = new JLabel("没有案例被选中");

	public MainFrame(Model model) {
		this.model = model;
	}
	
	protected void createAndShowGUI() throws Exception {
		
		log.info("start to build the GUI component");

		frame = new JFrame("量子医疗专家系统 - Quantum Medical Expert System (Q-MES)");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container container = frame.getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		createUpPanel(main);
		
		JPanel downtool = new JPanel();
		downtool.setLayout(new BoxLayout(downtool, BoxLayout.Y_AXIS));
		
		createRightTool(downtool);
		
		container.add(Box.createVerticalStrut(5));
		container.add(main);
		container.add(Box.createVerticalStrut(2));
		container.add(downtool);
		container.add(Box.createVerticalStrut(5));

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		int x = (int)(d.getWidth()/4);
		int y = (int)(d.getHeight()/4);
		int width = 2*x;
		int height = 2*y;
		
		frame.setBounds(x, y, width, height);
		frame.setVisible(true);
		log.info("finish building GUI component");
		
		JMenu system = new JMenu("系统");
		JMenuItem about = new JMenuItem("关于Q-MES");
		JMenuItem open = new JMenuItem("打开工作数据目录");
		JMenuItem error = new JMenuItem("错误信息调试");
		JMenuItem info = new JMenuItem("日志查看");
		system.add(about);
		system.addSeparator();
		system.add(open);
		system.addSeparator();
		system.add(error);
		system.add(info);
		
		JMenu caseMenu = new JMenu("案例");
		JMenuItem cases = new JMenuItem("案例管理");
		caseMenu.add(cases);

		JMenu netMenu = new JMenu("决策网络");
		JMenuItem rules = new JMenuItem("决策网络管理");
		JMenuItem setting = new JMenuItem("配置默认值");
		netMenu.add(rules);
		netMenu.add(setting);
		
		JMenu kgMenu = new JMenu("知识图谱");
		JMenuItem words = new JMenuItem("三元组管理");
		JMenuItem configindicator = new JMenuItem("配置定量指标");
		JMenuItem trysearch = new JMenuItem("模糊特征搜索");
		JMenuItem translateconfig = new JMenuItem("句式转换配置");
		JMenuItem testtranslate = new JMenuItem("句式转换测试");
		kgMenu.add(words);
		kgMenu.add(trysearch);
		kgMenu.addSeparator();
		kgMenu.add(configindicator);
		kgMenu.addSeparator();
		kgMenu.add(translateconfig);
		kgMenu.add(testtranslate);
		
		JMenuBar br = new JMenuBar();
		br.add(system); 
		br.add(caseMenu); 
		br.add(netMenu); 
		br.add(kgMenu);
		frame.setJMenuBar(br); 
		
		error.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showErrorDialog();
			}
		});
		
		translateconfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigMatchableUI cmui = new ConfigMatchableUI(model);
				cmui.createAndShowGUI(frame);
			}
		});
		
		testtranslate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TestMatchableUI sui = new TestMatchableUI(model);
				sui.createAndShowGUI(frame);
			}
		});
		
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showInfoDialog();
			}
		});
		
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReleaseNote rn = new ReleaseNote();
				rn.showReleaseNote(frame);
			}
		});
		
		setting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NConfigUI nui = new NConfigUI();
				nui.createAndShowGUI(frame, model.getNConfigStorage());
			}
		});
		
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BASEUI.openFileInExplorer(frame, model.getBaseDir());
			}
		});
		
		cases.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCaseManager();
			}
		});
		
		words.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showWordManager();
			}
		});
		
		rules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRuleManager();
			}
		});
		
		configindicator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					IndicatorUI iui = new IndicatorUI();
					iui.createAndShowGUI(frame, model);
					iui.init();
				}catch(Exception ex) {
					ex.printStackTrace();
					log.error(ex.getMessage());
				}
			}
		});
		
		trysearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trySearch();
			}
		});
		
		initValues();
	}
	
	JComboBox tags = new JComboBox();
	JTextField tagfilter = new JTextField();
	JButton reloadtag = new JButton("刷新标签");
	JButton tagsearch = new JButton("搜索");
	JButton cleantag = new JButton("清空");
	JTable caselist = new JTable();
	
	
	private void createUpPanel(JPanel panel) {
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(Box.createHorizontalStrut(5));
		p1.add(BASEUI.alabel("标签排序"));
		p1.add(tags);
		p1.add(reloadtag);
		p1.add(Box.createHorizontalStrut(5));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(Box.createHorizontalStrut(5));
		p2.add(BASEUI.alabel("标签过滤器"));
		p2.add(tagfilter);
		p2.add(tagsearch);
		p2.add(cleantag);
		p2.add(Box.createHorizontalStrut(5));
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		p3.add(Box.createHorizontalStrut(5));
		p3.add(new JScrollPane(caselist));
		p3.add(Box.createHorizontalStrut(5));
		
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
		p4.add(Box.createHorizontalStrut(5));
		p4.add(status);
		p4.add(Box.createHorizontalGlue());
		p4.add(Box.createHorizontalStrut(5));
		
		panel.add(p1);
		panel.add(p2);
		panel.add(p3);
		panel.add(p4);
		
		reloadtag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reloadtag();
			}
		});
		
		tagsearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchtag();
			}
		});
		
		tags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tagselect();
			}
		});
		
		cleantag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cleantag();
			}
		});
		
		caselist.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				caseListClicked();
			}
		});
		
		
		
	}
	
	JButton viewcase = new JButton("查看当前案例");
	JButton execution = new JButton("智能建议");
	
	private void createRightTool(JPanel panel) {
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(viewcase);
		p1.add(execution);
		
		panel.add(p1);
		
		viewcase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewCase();
			}
		});
		execution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeCase();
			}
		});

	}
	
	private void initValues() {
		
		BASEUI.initCombo(model.getCaseStorage().getSortedTags(), tags);
		
		caselist.setModel(new DefaultTableModel(new String[] {"案例名称","案例标签", "条件数量","案例备注"}, 0) {
			public boolean isCellEditable(int row, int column) {return false;}
		});
		caselist.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		caselist.getColumnModel().getColumn(0).setPreferredWidth(150);
		caselist.getColumnModel().getColumn(1).setPreferredWidth(100);
		caselist.getColumnModel().getColumn(2).setPreferredWidth(50);
		caselist.getColumnModel().getColumn(3).setPreferredWidth(300);
	}
	
	private void reloadtag() {
		BASEUI.initCombo(model.getCaseStorage().getSortedTags(), tags);
	}
	
	private void cleantag() {
		tagfilter.setText("");
	}
	
	private void searchtag() {
		String[] tagseg = tagfilter.getText().split(" ");
		List<CaseDef> cds = model.getCaseStorage().filterByTags(tagseg);
		
		
		DefaultTableModel dtm = BASEUI.cleanTable(caselist);
		if(cds.size()>0) {
			for(int i=0;i<cds.size();i++) {
				CaseDef cd = cds.get(i);
				dtm.addRow(new Object[] {cd, BASEUI.stringArrayToString(cd.getTags()), cd.getHuskys().size(), cd.getRemarks()});
			}
		}
		caselist.setModel(dtm);
		
	}
	
	private void tagselect() {
		if(tags.getSelectedIndex()>0) {
			SortTag st = (SortTag)tags.getSelectedItem();
			tagfilter.setText(tagfilter.getText()+" "+st.tag);
		}
	}
	
	private void caseListClicked() {
		int row = caselist.getSelectedRow();
		if(row>=0) {
			CaseDef cd = (CaseDef)caselist.getValueAt(row, 0);
			if(cd!=null) {
				currentCase = cd;
				status.setText("当前案例 : "+currentCase.getName());

			}
		}
	}
	
	private void trySearch() {
		SearchUI sui = new SearchUI();
		sui.createAndShowGUI(frame, model);	
	}
	
	private void showWordManager() {
		WordUI2 wui = new WordUI2();
		wui.createAndShowGUI(frame, model);
	}
	
	private void showRuleManager() {
		RuleManage rui = new RuleManage();
		rui.createAndShowGUI(model, frame, model.getRuleFileSubPath(), null);
	}
	
	private void showCaseManager() {
		CasesUI cui = new CasesUI();
		cui.createAndShowGUI(frame, model);	
	}
	
	private void showErrorDialog() {
		InfoUI iui = new InfoUI();
		iui.createAndShowGUI(frame, model.getLogUtil().getError(), "错误信息调试", InfoUI.TYPE_TEXT);
	}
	
	private void showInfoDialog() {
		InfoUI iui = new InfoUI();
		iui.createAndShowGUI(frame, model.getLogUtil().getInfo(), "日志查看", InfoUI.TYPE_TEXT);
	}
	
	private void viewCase() {
		if(currentCase!=null) {
			CaseInfoUI ciui = new CaseInfoUI();
			ciui.createAndShowGUI(frame, model, currentCase);
		}else {
			JOptionPane.showMessageDialog(frame,"请先在表格中选择一个case");
		}
	}
	
	private void executeCase() {
		if(currentCase!=null) {
			try {
				ExecutionProcess ep = new ExecutionProcess(model);
				StackedMatchResult smr = ep.run(currentCase);
				if(smr.isContainsError()) {
					BASEUI.promptError(frame);
				}else {
					CaseExecutionUI pui = new CaseExecutionUI();
					pui.createAndShowGUI(frame, currentCase, model, smr);
				}
			}catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				BASEUI.promptError(frame);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(frame,"请先在表格中选择一个案例");
		}
	}
	
}
