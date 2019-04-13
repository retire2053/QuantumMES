package qmes.rule.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.word.def.FeatureName;
import qmes.word.storage.WordStorageHelper;

public class CreateRule extends BASEUI implements CONST {

	private String basepath = null;
	private JDialog dialog = null;
	
	private Model model = null;
	
	private RuleManage parentObj = null;
	
	
	WordStorageHelper wsh = null;
	CascadeHelper ch = null;
	
	public void createAndShowGUI(Model model, JDialog parent, String basepath,RuleManage _parent) {

		this.model = model;
		
		this.basepath = basepath;
		this.parentObj = _parent;
		
		Container container = null;

		dialog = new JDialog(parent, "创建规则-设置规则名称");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();
		

	
		
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

		container.add(Box.createHorizontalStrut(5));
		container.add(createRight());
		container.add(Box.createHorizontalStrut(5));
		
		dialog.setBounds(170, 170, 1000, 150);
		dialog.setVisible(true);
		
		initValues();
		initListeners();
		
		ch = new CascadeHelper(model);
		ch.setControls(featurenames, groups, featurestates);
		
		ch.addStateActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println();
				
			}
		});
	}
	
	JButton confirm = new JButton("确认");
	

	JComboBox classcombo = new JComboBox();
	JComboBox featurenames = new JComboBox();
	JComboBox groups = new JComboBox();
	JComboBox featurestates = new JComboBox();
	JTextField ruleNo = new JTextField();
	
	private JPanel createRight() {
		
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.add(Box.createVerticalStrut(5));
		
		JPanel search = new JPanel();
		search.setLayout(new BoxLayout(search, BoxLayout.X_AXIS));
		search.add(BASEUI.alabel("选择类"));
		search.add(classcombo);
	
		
		
		JPanel search2 = new JPanel();
		search2.setLayout(new BoxLayout(search2, BoxLayout.X_AXIS));
		
		search2.add(BASEUI.alabel("选择特征名"));
		search2.add(featurenames);
		search2.add(BASEUI.alabel("选择组名"));
		search2.add(groups);
		
		search2.add(BASEUI.alabel("选择特征值"));
		search2.add(featurestates);
		
		search2.add(BASEUI.alabel("输入规则编号"));
		search2.add(ruleNo);
		
		
		right.add(search);
		right.add(Box.createVerticalStrut(5));
		right.add(search2);

		JPanel confirmMenu = new JPanel();
		confirmMenu.add(confirm);
		right.add(Box.createVerticalStrut(5));
		right.add(confirmMenu);
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
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createRule();
			}
		});
	}
	
	private void initValues() {

		classcombo.addItem("");
		for(int i=0;i<CONST.classes.length;i++) {
			classcombo.addItem(CONST.classes[i]);
		}
	}
	
	private String addDrlFile(String names2,String ruleClass,String ruleFeaturename,String ruleFeatruestate,String ruleNumber) {
		String ruleName = ruleClass+CONST.RULE_NAME_DELIMITER+ruleFeaturename+CONST.RULE_NAME_DELIMITER+ruleFeatruestate+CONST.RULE_NAME_DELIMITER+ruleNumber;
		String ruleContent = "package common;\n" + 
				"import qmes.model.*;\n" + 
				"import husky.service.*;\n\n"
				+ "rule \""+ruleName
				+ "\"\n"
				+ "\twhen\n"
				+ "\t\t\n"
				+ "\tthen\n"
				+ "\t\t\n"
				+ "end";
		
		String filename = ruleName+".drl";
		String filedir = basepath;//后续考虑是否需要根据命名空间分目录
		File out = new File(filedir+java.io.File.separator+filename);
		//判断文件是否存在
		if(out.exists()) {
			return "该规则名称已存在";
		}
		try {
			FileOutputStream fos = new FileOutputStream(out);
			fos.write(ruleContent.getBytes());
			parentObj.updateList(ruleName,null,true);
			fos.close();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
	private void createRule() {
		
		String ruleClass = "";
		if(classcombo.getSelectedIndex()>0) {
			Class clazz = (Class)classcombo.getSelectedItem();
			ruleClass = clazz.getSimpleName();
		}
		String ruleFeaturename = "";
		if(featurenames.getSelectedIndex()>0) {
			ruleFeaturename = featurenames.getSelectedItem().toString();
		}
		String ruleFeatruestate = "";
		if(featurestates.getSelectedIndex()>0) {
			ruleFeatruestate = featurestates.getSelectedItem().toString();
		}
		String ruleNumber = "";
		if(ruleNo.getText().length()>0) {
			ruleNumber = ruleNo.getText();
		}
		if(ruleClass.length()>0 && ruleFeaturename.length()>0 && ruleFeatruestate.length()>0) {
			String addResult = addDrlFile(CONST.NAMESPACE,ruleClass,ruleFeaturename,ruleFeatruestate,ruleNumber);
			if(addResult.length()>0) {
				JOptionPane.showMessageDialog(dialog, addResult);
			}else {
				dialog.dispose();
			}
		}
	}
}