package qmes.cases.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.base.InfoUI;
import qmes.cases.def.CaseDef;
import qmes.cases.def.CaseHelper;
import qmes.cases.storage.CaseStorage;
import qmes.cases.storage.SortTag;
import qmes.core.Model;
import qmes.indicator.def.IndicatorDef;
import qmes.indicator.ui.IndicatorUI;
import qmes.model.HuskyObject;
import qmes.model.IndicatorV;
import qmes.word.storage.WordStorageHelper;
import qmes.word.ui.WordUI2;

public class CasesUI {
	
	private static final Logger log = LoggerFactory.getLogger(CasesUI.class);
	
	
	private CaseHelper helper = new CaseHelper();
	private Model model = null;
	private JDialog dialog = null;
	private CaseStorage cs = null;
	private JFrame parent = null;
	
	private CaseDef currentCase = null;
	private String caseOld = null;
	
	CasesUIExtended uiextend = null;
	
	public void createAndShowGUI(JFrame parent, Model model) {
		this.parent = parent;
		this.model = model;
		cs = model.getCaseStorage();
		
		Container container = null;

		dialog = new JDialog(parent);
		
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(Box.createHorizontalStrut(5));
		
		JPanel panel1= new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		createUpPanel(panel1);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		createBasicInfo(panel2);
		
		JPanel part1 = new JPanel();
		part1.setLayout(new BoxLayout(part1, BoxLayout.X_AXIS));
		part1.add(Box.createHorizontalStrut(5));
		part1.add(panel1);
		part1.add(Box.createHorizontalStrut(15));
		part1.add(panel2);
		part1.add(Box.createHorizontalStrut(5));
		
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
		createFeaturePanel(panel3);
		
		container.add(part1);
		container.add(panel3);
		container.add(Box.createHorizontalStrut(5));
		dialog.setTitle("案例管理");
	    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	    int screenHeight = (int) screenSize.getHeight();
	    int screenWidth = (int) screenSize.getWidth();

	    dialog.setBounds(0,0,screenWidth, screenHeight-30);
		dialog.setVisible(true);
		
		initValues();

	}
	
	
	JComboBox tags = new JComboBox();
	JTextField tagfilter = new JTextField();
	JButton tagsearch = new JButton("搜索");
	JButton checkintegrity = new JButton("检查词语");
	JButton cleantag = new JButton("清空");
	JButton delcase = new JButton("删除选中案例");
	JTable caselist = new JTable();
	
	
	private void createUpPanel(JPanel panel) {
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(BASEUI.alabel("标签排序"));
		p1.add(tags);
		p1.add(checkintegrity);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(BASEUI.alabel("标签过滤器"));
		p2.add(tagfilter);
		p2.add(tagsearch);
		p2.add(cleantag);
		p2.add(delcase);
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		p3.add(new JScrollPane(caselist));
		
		panel.add(p1);
		panel.add(p2);
		panel.add(p3);
		
		checkintegrity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WordStorageHelper wsh = new WordStorageHelper(model, CONST.NAMESPACE);
				String caseintegrity = wsh.checkCaseIntegrity();
				InfoUI iui = new InfoUI();
				iui.createAndShowGUI(parent, caseintegrity, "检查案例的用词一致性", InfoUI.TYPE_TEXT);
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
		
		//删除案例
		delcase.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				log.info("\"delete caselist\" button is clicked");
				int row = caselist.getSelectedRow();
				if(row>=0) {
					CaseDef cd = (CaseDef)caselist.getValueAt(row, 0);
					delTags(cd.getName());
					cs.deleteStorage(cd.getName());
					refreshTags();
					searchtag();
				}else {
					JOptionPane.showMessageDialog(dialog, "请选择要删除的案例");
				}
			}
		});
		
		caselist.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(e.getClickCount()==2) {
					caseListClicked();
				}
			}
		});
	}
	
	JTextField name = new JTextField();
	JTextField taginput = new JTextField();
	JTextField remarks = new JTextField();
	JTable huskylist = new JTable();
	JTextArea document = new JTextArea();
	JButton create = new JButton("新建案例");
	JButton save = new JButton("保存当前案例");
	JButton delete = new JButton("删除条件");
	JButton refresh = new JButton("刷新列表值");
	JButton configword = new JButton("语义网络");

	
	private void createBasicInfo(JPanel panel) {
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(BASEUI.alabel("案例名称"));
		p1.add(name);
		p1.add(BASEUI.alabel("标签列表"));
		p1.add(taginput);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(BASEUI.alabel("案例备注信息"));
		p2.add(remarks);
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		p3.add(new JScrollPane(huskylist));
		
		JPanel pa = new JPanel();
		pa.setLayout(new BoxLayout(pa, BoxLayout.X_AXIS));
		pa.add(BASEUI.alabel("特征文本"));
		pa.add(Box.createHorizontalGlue());

		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
		p4.add(BASEUI.alabel("案例原文"));
		p4.add(Box.createHorizontalGlue());
		
		JPanel p5 = new JPanel();
		p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
		p5.add(new JScrollPane(document));
		document.setRows(10);
		document.setLineWrap(true);
		document.setWrapStyleWord(true);
		document.setFont(CONST.DEFAULT_FONT);
		
		JPanel p6 = new JPanel();
		p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));
		p6.add(create);
		p6.add(save);
		p6.add(delete);
		p6.add(refresh);
		p6.add(configword);
		
		panel.add(p1);
		panel.add(p2);
		panel.add(p3);
		panel.add(pa);
		panel.add(p4);
		panel.add(p5);
		panel.add(p6);
		
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createCase();
			}
		});
		
		//保存案例
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("\"save case\" button is clicked");
				saveCase();
			}
		});
		//删除案例条件
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("\"delete conditions\" button is clicked");
				int row = huskylist.getSelectedRow();
				if(row>=0) {
					currentCase.getHuskys().remove(row);
					updateUIWithCurrentCase();
				}else {
					JOptionPane.showMessageDialog(dialog, "请选择要删除的条件");
				}
			}
		});
		
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshAllComboOtherThanIndicatorDef();
			}
		});
		
		name.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if(currentCase!=null) {
					currentCase.setName(name.getText());
				}
			}
		});
		
		remarks.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if(currentCase!=null) {
					currentCase.setRemarks(remarks.getText());
				}
			}
		});
		
		taginput.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if(currentCase!=null) {
					currentCase.setTags(BASEUI.stringToStringArray(taginput.getText()));
				}
			}
		});
		
		document.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if(currentCase!=null) {
					currentCase.setDocument(document.getText());
				}
			}
		});
		
		configword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WordUI2 wui = new WordUI2();
				wui.createAndShowGUI(parent, model);
			}
		});
		
	}
	
	
	protected JButton configindicator = new JButton("配置");

	protected JComboBox indicatordefcombo = new JComboBox();
	protected JTextField indicatorvalue = new JTextField();
	protected JComboBox indicatorvtime = new JComboBox();
	protected JButton addindicatorv = new JButton("创建定量指标");

	private void createFeaturePanel(JPanel panel) {

		log.info("start to build the panel component");

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createVerticalStrut(5));
		{
			JPanel panelup = new JPanel();
			BoxLayout bl = new BoxLayout(panelup, BoxLayout.Y_AXIS);
			panelup.setLayout(bl);
			panelup.setBorder(BorderFactory.createTitledBorder("创建输入条件"));

			JPanel p2 = new JPanel();
			p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
			p2.add(BASEUI.alabel("定量指标"));
			p2.add(indicatordefcombo);
			p2.add(BASEUI.alabel("值"));
			p2.add(indicatorvalue);
			p2.add(BASEUI.alabel("时间"));
			p2.add(indicatorvtime);
			p2.add(addindicatorv);
			p2.add(configindicator);

			panelup.add(p2);
			
			uiextend = new CasesUIExtended(dialog, model);
			uiextend.createExtendedUI(panelup);
			uiextend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					HuskyObject ho = (HuskyObject)e.getSource();
					addHuskyObjectToList(ho);
				}
			});

			panel.add(panelup);
		}
		
		panel.add(Box.createVerticalStrut(5));

		log.info("finish building panel component");
		
		addindicatorv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addIndicatorV();
			}
		});
		
		configindicator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configIndicator();
			}
		});
	}
	
	private void initValues() {
		refreshTags();
		
		caselist.setModel(new DefaultTableModel(new String[] {"案例名称","案例标签", "条件数量","案例备注"}, 0) {
			public boolean isCellEditable(int row, int column) {return false;}
		});
		caselist.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		caselist.getColumnModel().getColumn(0).setPreferredWidth(200);
		caselist.getColumnModel().getColumn(1).setPreferredWidth(200);
		caselist.getColumnModel().getColumn(2).setPreferredWidth(50);
		caselist.getColumnModel().getColumn(3).setPreferredWidth(400);
		
		huskylist.setModel(new DefaultTableModel(new String[] {"类名称","明细"}, 0) {
			public boolean isCellEditable(int row, int column) {return false;}
		});
		huskylist.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		huskylist.getColumnModel().getColumn(0).setPreferredWidth(200);
		huskylist.getColumnModel().getColumn(1).setPreferredWidth(400);

		refreshAllComboOtherThanIndicatorDef();

		BASEUI.initCombo(BASEUI.getTreatTimes(), indicatorvtime);
		
		createCase();
	}
	
	protected void refreshAllComboOtherThanIndicatorDef() {
		
		uiextend.reload();
		
		indicatordefcombo.removeAllItems();
		indicatordefcombo.addItem("");
		List<IndicatorDef> list = model.getIndicatorDefs();
		for(int k=0 ; k<list.size() ; k++) {
			String v = list.get(k).getName()+" [unit="+list.get(k).getUnit().getUnit()+"]";
			indicatordefcombo.addItem(v);
		}

	}

	private void cleantag() {
		tagfilter.setText("");
	}
	
	private void searchtag() {
		String[] tagseg = tagfilter.getText().split(" ");
		List<CaseDef> cds = cs.filterByTags(tagseg);
		
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
				caseOld = cd.getName();
				updateUIWithCurrentCase();
			}
		}
	}
	//刷新标签
	private void refreshTags() {
		BASEUI.initCombo(cs.getSortedTags(), tags);
	}
	
	//删除标签
	private void delTags(String tagName) {
		for (int i = 0; i < cs.getCases().size(); i++) {
			if(tagName.equals(cs.getCases().get(i).getName())) {
				cs.getCases().remove(i);
			}
		}
	}
	
	private void createCase() {
		currentCase = new CaseDef();
		caseOld = null;
		updateUIWithCurrentCase();
	}
	//保存案例
	private void saveCase() {
		if(name.getText()==null || name.getText().isEmpty()) {
			JOptionPane.showMessageDialog(dialog, "请输入要保存案例的名称");
		}else if(taginput.getText()==null || taginput.getText().isEmpty()) {
			JOptionPane.showMessageDialog(dialog, "请至少输入一个标签");
		}else if(document.getText()==null || document.getText().isEmpty())
			JOptionPane.showMessageDialog(dialog, "请输入案例的原文信息");
		else {
			try {
				if(currentCase!=null) {
					
					currentCase.setName(name.getText());
					currentCase.setTags(BASEUI.stringToStringArray(taginput.getText()));
					currentCase.setRemarks(remarks.getText());
					currentCase.setDocument(document.getText());
					
					if(caseOld == null) {//如果当前case是新增case
						cs.addCase(currentCase);
						cs.saveStorage();
					}else {	
						//如果当前case是编辑已经有的case
						if(caseOld!=null && !caseOld.equals(currentCase.getName())) {//if the mode is EDITING, and name is changed
							cs.saveStorage();
							cs.deleteStorage(caseOld); //删除原始文件
						}
					}
					//在保存完，currentCase就变成了“编辑已经有的case”
					caseOld = currentCase.getName();
					
					refreshTags();
					JOptionPane.showMessageDialog(dialog, "案例\"" +currentCase.getName()+ "\"已成功保存", "保存成功",JOptionPane.YES_OPTION);
					log.info("Case \"{}\" is successfully saved", currentCase.getName());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void updateUIWithCurrentCase() {
		name.setText(currentCase.getName());
		taginput.setText(BASEUI.stringArrayToString(currentCase.getTags()));
		remarks.setText(currentCase.getRemarks());
		document.setText(currentCase.getDocument());
		
		DefaultTableModel dtm = BASEUI.cleanTable(huskylist);
		
		if(currentCase.getHuskys().size()>0) {
			try {
				List<HuskyObject> hos = helper.wrapper2Husky(currentCase.getHuskys());
				for(int i=0;i<hos.size();i++) {
					HuskyObject ho = (HuskyObject)hos.get(i);
					dtm.addRow(new Object[] {ho.getClass().getSimpleName(), ho });
				}
			}catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}
		huskylist.setModel(dtm);
	}
	
	protected void addHuskyObjectToList(HuskyObject ho) {
		
		if(currentCase!=null) {
			try {
				currentCase.getHuskys().add(helper.husky2Wrapper(ho));
				updateUIWithCurrentCase();
			}catch(Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}
	}
	
	protected void addIndicatorV() {
		log.info("\"add v-type indicator\" button is clicked");
		
		int index = indicatordefcombo.getSelectedIndex();
		int index2 = indicatorvtime.getSelectedIndex();
		
		if(index>0 && index2>0) {
			IndicatorDef id = model.getIndicatorDefs().get(index-1);
			String unit = id.getUnit().getUnit();
			double value = Double.valueOf(indicatorvalue.getText()).doubleValue();
			double time = Double.valueOf(indicatorvtime.getSelectedItem().toString()).doubleValue();
			IndicatorV iv = new IndicatorV(id.getName(), value, unit, time);
			
			addHuskyObjectToList(iv);
		}
	}
	
	protected void configIndicator() {
		log.info("\"config indicator\" button is clicked");
		try {
			IndicatorUI hws = new IndicatorUI();
			hws.createAndShowGUI(parent, model);
			hws.init();
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}
	
}