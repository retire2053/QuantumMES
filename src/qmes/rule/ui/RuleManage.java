package qmes.rule.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.undo.UndoManager;

import org.apache.lucene.document.Document;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.annotation.AnnotationLine;
import qmes.annotation.AnnotationRule;
import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.rule.def.DefBase;
import qmes.rule.def.DefLhs;
import qmes.rule.def.DefObject;
import qmes.rule.def.DefRule;
import qmes.rule.search.RuleSearch;
import qmes.word.def.FeatureName;
import qmes.word.storage.WordStorageHelper;

public class RuleManage extends BASEUI implements CONST {
	
	private static final Logger log = LoggerFactory.getLogger(RuleManage.class);

	private String basepath = null;
	private JDialog dialog = null;
	private JFrame parent = null;
	
	private Model model = null;
	
	private String currentSelectRuleName = null;
	private String currentSelectRuleContent = "";
	
	private String[] ruleHead = {"类","特征名","特征值","编号"};
	
	private JTable ruleListTable = new JTable() ;//规则列表
	
	TableRowSorter sRowSorter = null;
	
	protected JTable lhs = new JTable();
	protected JTextArea rhsEditor = new JTextArea();
	protected JTextArea lhsEditor = new JTextArea();
	static UndoManager lhsUndoManager = new UndoManager();
	static UndoManager rhsUndoManager = new UndoManager();
	static UndoManager ruleMeaningUndoManager = new UndoManager();
	static UndoManager documentUndoManager = new UndoManager();
	static UndoManager documentNameUndoManager = new UndoManager();
	
	WordStorageHelper wsh = null;
	CascadeHelper ch = null;
	
	private String defaultrulename = null;
	
	public void createAndShowGUI(Model model, JFrame parent, String basepath, String defaultrulename) {
		log.info("start show Rule Manage Gui");
		this.model = model;
		
		this.basepath = basepath;
		
		this.parent = parent;
		
		this.defaultrulename = defaultrulename;
		
		Container container = null;

		dialog = new JDialog(parent, "决策网络管理");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();
		
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

		container.add(Box.createHorizontalStrut(5));
		container.add(createLeft());
		container.add(Box.createHorizontalStrut(15));
		container.add(createRight());
		container.add(Box.createHorizontalStrut(5));
		
		Dimension   screensize   =   Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setBounds(0, 0, (int)screensize.getWidth(),(int)screensize.getHeight());
		dialog.setVisible(true);
		log.info("start initValues");
		initValues();
		log.info("end initValues");
		log.info("start initListeners");
		initListeners();
		log.info("end initListeners");
		
		ch = new CascadeHelper(model);
		ch.setControls(featurenames, groups, featurestates);
		
		ch.addStateActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fsexample.setText(e.getActionCommand());
			}
		});
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e)  
            {
				log.info("start save Annotationstorage");
				model.saveAnnotationRule();
            }
		});
		
	}
	JButton searchButton1 = new JButton("筛选");
	JButton searchButton2 = new JButton("筛选");
	
	JButton create = new JButton("创建规则");
	JButton delete = new JButton("删除");
	
	JButton createLHS = new JButton("添加LHS");
	JButton createLhsAndRhs = new JButton("添加LHS和RHS");
	JButton save = new JButton("保存规则");
	
	JButton editRuleName = new JButton("修改名称");
	
	JComboBox classcombo = new JComboBox();
	JComboBox featurenames = new JComboBox();
	JComboBox groups = new JComboBox();
	JComboBox featurestates = new JComboBox();
	JTextField fsexample = new JTextField();
	JTextField keyword = new JTextField();
	JTextField lhsKeyword = new JTextField();
	JTextField rhsKeyword = new JTextField();
	
	protected JTextArea rulemeaning = new JTextArea();
	protected JTextArea document = new JTextArea();
	protected JTextField documentName = new JTextField();
	protected JButton saveannotation = new JButton("保存规则附注");
	
	
	private JPanel createLeft() {
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.add(Box.createVerticalStrut(5));
		left.setPreferredSize(new Dimension(600, 300));
		JPanel leftMenu = new JPanel();
		leftMenu.setLayout(new BoxLayout(leftMenu, BoxLayout.X_AXIS));
		leftMenu.add(create);
		leftMenu.add(createLHS);
		leftMenu.add(createLhsAndRhs);
		leftMenu.add(save);
		leftMenu.add(editRuleName);
		
		JPanel leftup = new JPanel();
		leftup.setLayout(new BoxLayout(leftup, BoxLayout.Y_AXIS));
		
		JPanel search1 = new JPanel();
		search1.setLayout(new BoxLayout(search1, BoxLayout.Y_AXIS));
		search1.setBorder(BorderFactory.createTitledBorder("列表过滤"));
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(BASEUI.alabel("选择类"));
		p1.add(classcombo);
		JPanel p6 = new JPanel();
		p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));
		p6.add(BASEUI.alabel("选择特征名"));
		p6.add(featurenames);
		p6.add(BASEUI.alabel("选择组名"));
		p6.add(groups);
		p6.add(BASEUI.alabel("选择特征值"));
		p6.add(featurestates);
		p6.add(searchButton1);
		search1.add(p1);
		search1.add(p6);
		JPanel search2 = new JPanel();
		search2.setLayout(new BoxLayout(search2, BoxLayout.X_AXIS));
		search2.setBorder(BorderFactory.createTitledBorder("搜索引擎"));
		
		search2.add(BASEUI.alabel("规则名称"));
		search2.add(keyword);
		search2.add(BASEUI.alabel("LHS"));
		search2.add(lhsKeyword);
		search2.add(BASEUI.alabel("RHS"));
		search2.add(rhsKeyword);
		search2.add(searchButton2);
		
		leftup.add(search1);
		leftup.add(Box.createVerticalStrut(3));
		leftup.add(search2);
		
		JPanel leftRuleList = new JPanel();
		leftRuleList.setLayout(new BoxLayout(leftRuleList, BoxLayout.Y_AXIS));
		leftRuleList.setPreferredSize(new Dimension(400,200));
		leftRuleList.setBorder(BorderFactory.createTitledBorder("规则列表"));
		leftRuleList.add(new JScrollPane(ruleListTable));
		
		JPanel lhsJpanel = new JPanel();
		lhsJpanel.setLayout(new BoxLayout(lhsJpanel, BoxLayout.Y_AXIS));
		lhsJpanel.setPreferredSize(new Dimension(0, 180));
		lhsJpanel.add(new JScrollPane(lhsEditor));
		lhsEditor.setTabSize(4);
		lhsJpanel.setBorder(BorderFactory.createTitledBorder("LHS"));
		
		JPanel rhsJpanel = new JPanel();
		rhsJpanel.setLayout(new BoxLayout(rhsJpanel, BoxLayout.Y_AXIS));
		rhsJpanel.setPreferredSize(new Dimension(0, 180));
		rhsJpanel.add(new JScrollPane(rhsEditor));
		rhsEditor.setTabSize(4);
		rhsJpanel.setBorder(BorderFactory.createTitledBorder("RHS"));
		
		
		left.add(leftup);
		left.add(leftRuleList);
		left.add(leftMenu);
		left.add(lhsJpanel);
		left.add(rhsJpanel);
		return left;
	}
	
	private JPanel createRight() {
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.add(Box.createVerticalStrut(5));
		
		JPanel rightup = new JPanel();
		rightup.setLayout(new BoxLayout(rightup, BoxLayout.Y_AXIS));
		rightup.add(new JScrollPane(lhs));
		rightup.setBorder(BorderFactory.createTitledBorder("注解"));
		rightup.setPreferredSize(new Dimension(550, 240));
		right.add(rightup);
		
		right.add(BASEUI.alabel("本规则被触发的意义解释"));
		right.add(new JScrollPane(rulemeaning));
		rulemeaning.setRows(15);
		rulemeaning.setFont(CONST.DEFAULT_FONT);
		rulemeaning.setLineWrap(true);
		rulemeaning.setWrapStyleWord(true);
		
		right.add(BASEUI.alabel("本规则文档 - 原文出处"));
		right.add(documentName);
		
		right.add(BASEUI.alabel("本规则文档 - documentation"));
		right.add(new JScrollPane(document));
		document.setRows(10);
		document.setFont(CONST.DEFAULT_FONT);
		document.setLineWrap(true);
		document.setWrapStyleWord(true);
		
		changeInputBoxState(false);
		return right;
	}
	
	private void initListeners() {
		
		classcombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(classcombo.getSelectedIndex()>0 && wsh!=null) {
					Class clazz = (Class)classcombo.getSelectedItem();
					ch.setClazz(clazz);
					List<FeatureName> fns = wsh.listFeatureNameWithClass(clazz.getName());
					featurenames.removeAllItems();
					featurenames.addItem("");
					if(fns.size()>0) {
						for(int i=0;i<fns.size();i++) {
							featurenames.addItem(fns.get(i));
						}
					}
				}
			}
		});
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateRule cr = new CreateRule();
				cr.createAndShowGUI(model, dialog, basepath,RuleManage.this);
			}
		});
		ruleListTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int row = ruleListTable.getSelectedRow();
				List<String> tableValue = new ArrayList<String>();
				if(ruleListTable.getValueAt(row, 0).toString().length()>0) {
					tableValue.add(ruleListTable.getValueAt(row, 0).toString());
				}
				if(ruleListTable.getValueAt(row, 1).toString().length()>0) {
					tableValue.add(ruleListTable.getValueAt(row, 1).toString());
				}
				if(ruleListTable.getValueAt(row, 2).toString().length()>0) {
					tableValue.add(ruleListTable.getValueAt(row, 2).toString());
				}
				if(ruleListTable.getValueAt(row, 3).toString().length()>0) {
					tableValue.add(ruleListTable.getValueAt(row, 3).toString());
				}
				String ruleName = String.join(CONST.RULE_NAME_DELIMITER, tableValue);
				log.info(ruleName+"被选中");
				selectedRule(ruleName);
			}
		});
		lhs.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				log.info("\"LHS\" table is key-changed");
				lhsedit();
			}
		});
		lhs.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				log.info("\"LHS\" table is double-clicked");
				if(e.getClickCount()==2) {
					lhsdoubleclick();
				}
			}
		});
		createLHS.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(getCurrentSelectRuleName().length()==0) {
					JOptionPane.showMessageDialog(dialog, "请先选择一个规则再进行添加");
				}else {
					CreateLHS clhs = new CreateLHS();
					clhs.createAndShowGUI(model, dialog, basepath,RuleManage.this);
				}
			}
		});
		createLhsAndRhs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				if(getCurrentSelectRuleName().length()==0) {
					JOptionPane.showMessageDialog(dialog, "请先选择一个规则再进行添加");
				}else {
					CreateLhsAndRhs cLhsAndRhs = new CreateLhsAndRhs();
					cLhsAndRhs.createAndShowGUI(model,dialog,basepath,RuleManage.this);
				}
			}
		});
		lhsEditor.addKeyListener(new KeyListener() {	
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO 自动生成的方法存根
			}	
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO 自动生成的方法存根
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO 自动生成的方法存根
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {  
                    if (lhsUndoManager.canUndo()) {  
                        lhsUndoManager.undo();  
                    }
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {  
                    if (lhsUndoManager.canRedo()) {  
                        lhsUndoManager.redo();  
                    }  
                }  
			}
		});
		rhsEditor.addKeyListener(new KeyListener() {	
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO 自动生成的方法存根
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {  
                    if (rhsUndoManager.canUndo()) {  
                        rhsUndoManager.undo();  
                    }
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {  
                    if (rhsUndoManager.canRedo()) {  
                        rhsUndoManager.redo();  
                    }  
                }  
			}
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO 自动生成的方法存根	
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO 自动生成的方法存根
			}
		});
		document.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				log.info("\"document\" textarea is changed");
				DefRule rule = model.getCurrentRule();
				AnnotationRule ar = model.getAnnotationRule(rule);
				ar.setDocument(document.getText());
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO 自动生成的方法存根
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {  
                    if (documentUndoManager.canUndo()) {  
                        documentUndoManager.undo();  
                    }
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {  
                    if (documentUndoManager.canRedo()) {  
                    	documentUndoManager.redo();  
                    }  
                }  
			}
		});
		documentName.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				log.info("\"documentName\" textField is changed");
				DefRule rule = model.getCurrentRule();
				AnnotationRule ar = model.getAnnotationRule(rule);
				ar.setDocumentName(documentName.getText());
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO 自动生成的方法存根
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {  
                    if (documentUndoManager.canUndo()) {  
                        documentUndoManager.undo();  
                    }
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {  
                    if (documentUndoManager.canRedo()) {  
                    	documentUndoManager.redo();  
                    }  
                }  
			}
		});
		rulemeaning.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				log.info("\"rule meaning\" textfield is changed");
				DefRule rule = model.getCurrentRule();
				AnnotationRule ar = model.getAnnotationRule(rule);
				ar.setMeaning(rulemeaning.getText());
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO 自动生成的方法存根
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {  
                    if (ruleMeaningUndoManager.canUndo()) {  
                    	ruleMeaningUndoManager.undo();  
                    }
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {  
                    if (ruleMeaningUndoManager.canRedo()) {  
                    	ruleMeaningUndoManager.redo();  
                    }  
                }  
			}
		});
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				saveDrlFile();
			}
		});
		searchButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String className = "";
				String featureName = "";
				String featureState = "";
				
				sRowSorter.setRowFilter(null);
				RowFilter<Object, Object> classFilter = null;
				RowFilter<Object, Object> featureNameFilter = null;
				RowFilter<Object, Object> featureStateFilter = null;
				ArrayList<RowFilter<Object, Object>> condition = new ArrayList<RowFilter<Object,Object>>();
				if(classcombo.getSelectedIndex()>0) {
					Class clazz = (Class)classcombo.getSelectedItem();
					className = clazz.getSimpleName();
					classFilter = RowFilter.regexFilter(className, ruleListTable.getColumnModel().getColumnIndex("类"));
					condition.add(classFilter);
				}
				if(featurenames.getSelectedIndex()>0) {
					featureName = featurenames.getSelectedItem().toString();
					featureNameFilter = RowFilter.regexFilter(featureName, ruleListTable.getColumnModel().getColumnIndex("特征名"));
					condition.add(featureNameFilter);
				}
				if(featurestates.getSelectedIndex()>0) {
					featureState = featurestates.getSelectedItem().toString();
					featureStateFilter = RowFilter.regexFilter(featureState, ruleListTable.getColumnModel().getColumnIndex("特征值"));
					condition.add(featureStateFilter);
				}
				sRowSorter.setRowFilter(RowFilter.andFilter(condition));
			}
		});
		searchButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("start searchRule");
				searchRule(keyword.getText(),lhsKeyword.getText(),rhsKeyword.getText());
				log.info("end searchRule");
			}
		});
		editRuleName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				if(getCurrentSelectRuleName().length()==0) {
					JOptionPane.showMessageDialog(dialog, "请先选择一个规则再进行添加");
				}else {
					String currentRulename = "当前名称:"+getCurrentSelectRuleName();
					String text = JOptionPane.showInputDialog(null, currentRulename,getCurrentSelectRuleName());
					if(text!=null && text!=currentRulename) {
						String filename = getCurrentSelectRuleName()+".drl";
						String filedir = basepath;
						File currentFile = new File(filedir+java.io.File.separator+filename);
						File newFile = new File(filedir+java.io.File.separator+text+".drl");
						if(newFile.exists()) {
							JOptionPane.showMessageDialog(dialog, "该规则名称已存在");
						}else {
							setCurrentSelectRuleName(text);
							saveDrlFile();
							currentFile.delete();
							log.info("删除规则文件："+currentFile);
							updateList(text,null,true);
						}
					}
				}
			}
		});
	}
	private void initValues() {
		
		if(defaultrulename!=null) {
			updateList(this.defaultrulename,null,false);
		}else {
			updateList("",null,false);
		}
		
		classcombo.addItem("");
		for(int i=0;i<CONST.classes.length;i++) {
			classcombo.addItem(CONST.classes[i]);
		}
		lhs.setModel(new DefaultTableModel(new String[]{ "类型","是否必须","权重","解释","表达式"}, 0) {
			public boolean isCellEditable(int row, int column) {if(column==1 || column==2)return true;else return false;}
		});
		lhs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		lhs.getColumnModel().getColumn(0).setPreferredWidth(80);
		lhs.getColumnModel().getColumn(1).setPreferredWidth(50);
		lhs.getColumnModel().getColumn(2).setPreferredWidth(50);
		lhs.getColumnModel().getColumn(3).setPreferredWidth(120);
		lhs.getColumnModel().getColumn(4).setPreferredWidth(350);
	}
	public void updateList(String setSelectedRuleName,List<String> searchRule,boolean isLoad) {
		log.info("start updateList");
		int selectedRow = -1;
		if(isLoad) {
			model.loadRuleList();
		}
		DefaultTableModel dtm = new DefaultTableModel(ruleHead, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		for(int i=0;i<model.getRules().size();i++) {
			String ruleName = model.getRules().get(i).getName();
			if(searchRule!=null && !searchRule.contains(ruleName)) {
				continue;
			}
			String[] ruleNameArr = ruleName.split(CONST.RULE_NAME_DELIMITER);
			String ruleClass = "";
			String ruleFeaturename = "";
			String ruleFeaturestate = "";
			String ruleNumber = "";
			if(ruleNameArr.length>=4) {
				ruleClass = ruleNameArr[0];
				ruleFeaturename = ruleNameArr[1];
				ruleFeaturestate = ruleNameArr[2];
				ruleNumber = ruleNameArr[3];
			}else {
				ruleNumber = ruleName;
			}
			if(setSelectedRuleName.equals(ruleName)) {
				selectedRow = i;
			}
			dtm.addRow(new Object[] { ruleClass,ruleFeaturename,ruleFeaturestate,ruleNumber });
		}
		sRowSorter = new TableRowSorter(dtm);
		ruleListTable.setRowSorter(sRowSorter);
	
		ruleListTable.setModel(dtm);
		if(selectedRow>=0) {
			ruleListTable.setRowSelectionInterval(selectedRow, selectedRow);
			selectedRule(setSelectedRuleName);
		}
		log.info("end updateList");
	}
	public void fillRuleDetail() {
		DefRule rule = model.getCurrentRule();
		AnnotationRule ar = model.getAnnotationRule(rule);
		DefLhs dlhs = rule.getLhs();
		DefaultTableModel mm = BASEUI.cleanTable(lhs);
		for(int i=0;i<dlhs.getObjectList().size();i++) {
			DefBase dbase = dlhs.getObjectList().get(i);
			AnnotationLine al = ar.getLines().get(i);
			String explanationSymbol = "";
			if(al.getExplanation()!=null && al.getExplanation().length()>=5)
				explanationSymbol = "Y";
			if(dbase instanceof DefObject) {
				mm.addRow(new Object[] { ((DefObject)dbase).getObjectType(), al.isNecessary(), al.getPower(), explanationSymbol,   dbase });
			}else {
				mm.addRow(new Object[] { "", al.isNecessary(), al.getPower(), explanationSymbol, dbase });
			}
		}
		lhs.setModel(mm);
		lhsEditor.setText(rule.getLhs().toString());
		lhsEditor.getDocument().addUndoableEditListener(lhsUndoManager);
		
		rhsEditor.setText(rule.getConsequence().toString());
		rhsEditor.getDocument().addUndoableEditListener(rhsUndoManager);
		
		rulemeaning.setText(ar.getMeaning());
		rulemeaning.getDocument().addUndoableEditListener(ruleMeaningUndoManager);
		document.setText(ar.getDocument());
		document.getDocument().addUndoableEditListener(documentUndoManager);
		documentName.setText(ar.getDocumentName());
		documentName.getDocument().addUndoableEditListener(documentNameUndoManager);
		
		changeInputBoxState(true);
	}
	protected void lhsdoubleclick() {
		int row = lhs.getSelectedRow();
		AnnotationRule ar = model.getAnnotationRule(model.getCurrentRule());
		AnnotationLine al = ar.getLines().get(row);
		DefBase dbase = (DefBase)lhs.getModel().getValueAt(row, 4);
		String prompt =  "当前行意义=\""+ al.getExplanation() +"\"\n";
		prompt = prompt +"原始rule文本=\""+ dbase.toString() +"\"";
		String text = JOptionPane.showInputDialog(null, prompt,al.getExplanation());
		if(text!=null)al.setExplanation(text);
		model.getCurrentRule().notifyListener();
		fillRuleDetail();
	}
	//设置某个规则选中-
	public void selectedRule(String ruleName) {
		DefRule dr = model.getRuleByName(ruleName);
		setCurrentSelectRuleName(ruleName);
		model.setCurrentRule(dr);
		fillRuleDetail();
	}
	public void setCurrentSelectRuleName(String ruleName) {
		this.currentSelectRuleName = ruleName;
	}
	public String getCurrentSelectRuleName() {
		if(this.currentSelectRuleName != null) {
			return this.currentSelectRuleName;
		}
		return "";
	}
	public void setCurrentSelectRuleContent(String ruleContent) {
		this.currentSelectRuleContent = ruleContent;
	}
	public String getCurrentSelectRuleContent() {
		return currentSelectRuleContent;
	}
	private void changeInputBoxState(boolean state) {
		lhsEditor.setEditable(state);
		rhsEditor.setEditable(state);
		document.setEditable(state);
		documentName.setEditable(state);
		rulemeaning.setEditable(state);
	}
	
	private boolean compile() {
		byte[] bs = getCurrentSelectRuleContent().getBytes();
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newByteArrayResource(bs), ResourceType.DRL);
	
		if (kbuilder.hasErrors()) {
			Iterator<KnowledgeBuilderError> erritr = kbuilder.getErrors().iterator();
			StringBuffer sb = new StringBuffer();
			while (erritr.hasNext()) {
				KnowledgeBuilderError e = erritr.next();
				sb.append(e.toString() + "\n");
			}
			JOptionPane.showMessageDialog(dialog, sb.toString());
			log.info(sb.toString());
			return false;
		} else {
			log.info("No error found!");
			return true;
		}
	}
	private void saveDrlFile() {
		String ruleContent = "package test;\r\n" + 
				"import qmes.model.*;\r\n" + 
				"import husky.service.*;\n\n"
				+ "rule \""+getCurrentSelectRuleName()+"\"\n"
						+ lhsEditor.getText()+"\n\tthen\n"+rhsEditor.getText()+"end";
		setCurrentSelectRuleContent(ruleContent);
		String text = getCurrentSelectRuleContent();
		String filename = getCurrentSelectRuleName()+".drl";
		if(compile()) {
			File out = new File(basepath+java.io.File.separator+filename);
			try {
				FileOutputStream fos = new FileOutputStream(out);
				fos.write(text.getBytes());
				fos.close();
				log.info("设置被选中的行"+getCurrentSelectRuleName());
				updateList(getCurrentSelectRuleName(),null,true);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	protected void lhsedit() {
		int row = lhs.getSelectedRow();
		int column = lhs.getSelectedColumn();	
		if(row>=0 && (column==1 || column==2)) {
			String value = (String)lhs.getModel().getValueAt(row, column);
			AnnotationRule ar = model.getAnnotationRule(model.getCurrentRule());
			AnnotationLine al = ar.getLines().get(row);
			log.info("LHS TableChange:row="+row+",col="+column+",value="+value+",type="+value.getClass().getSimpleName());
			if(value!=null) {
				//MODEL is IN LAST COLUMN
				DefBase dbase = (DefBase)lhs.getModel().getValueAt(row, 4);
				if(column==1) {	//MUST BE true OR false
					al.setNecessary(Boolean.valueOf(value));	
				}else if(column==2) {
					al.setPower(Integer.valueOf(value));
				}
				model.getCurrentRule().notifyListener();
			}
		}
	}
	private void searchRule(String keyword,String lhsKeyword,String rhsKeyword) {
		try {
			RuleSearch rSearch = new RuleSearch(model.getRuleSearchTempSubPath());
			List<Document> docslhs = new ArrayList<Document>();;
			List<Document> docsrhs = new ArrayList<Document>();;
			List<Document> docsRuleName = new ArrayList<Document>();;
			List<Document> docs = new ArrayList<Document>();
			
			ArrayList<String> afList = new ArrayList<String>();
			ArrayList<String> qList = new ArrayList<String>();
			if(keyword.length()>0) {
				afList.add("ruleName");
				qList.add(keyword);
			}
			if(lhsKeyword.length()>0) {
				afList.add("lhs");
				qList.add(lhsKeyword);
			}
			if(rhsKeyword.length()>0) {
				afList.add("rhs");
				qList.add(rhsKeyword);
			}
			log.info("开始检索:"+qList.toString());
			int n = afList.size();
			List<String> searchRule=new ArrayList<>();
			if(n>0) {
				String[] field = new String[n];
				String[] queryString = new String[n];
				
				for(int i=0; i<afList.size();i++) {
					field[i] = afList.get(i).toString();
					queryString[i] = qList.get(i).toString();
				}
				docsRuleName = rSearch.search(field, queryString);
				docs.addAll(docsRuleName);
				
				log.info("检索出文档数："+docs.size());
				
				if (docs.size() > 0) {
					for (int i = 0; i < docs.size(); i++) {
						Document doc = docs.get(i);
						String ruleName = doc.get("ruleName");
						searchRule.add(ruleName);
					}
				}
				updateList("",searchRule,false);
			}else{
				updateList("", null,false);
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
}
