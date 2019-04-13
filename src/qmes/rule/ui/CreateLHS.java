package qmes.rule.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.word.def.FeatureName;
import qmes.word.storage.WordStorageHelper;

public class CreateLHS extends BASEUI implements CONST {

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

		dialog = new JDialog(parent, "创建规则-添加LHS");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();
		
		
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

		container.add(Box.createHorizontalStrut(5));
		container.add(createRight());
		container.add(Box.createHorizontalStrut(5));
		
		dialog.setBounds(170, 170, 1000, 180);
		dialog.setVisible(true);
		
		initValues();
		initListeners();
		
		ch = new CascadeHelper(model);
		ch.setControls(featurenames, groups, featurestates);
		
		ch.addStateActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				fsexample.setText(e.getActionCommand());
			}
		});
	}
	
	JButton confirm = new JButton("确认");
	
	JComboBox classcombo = new JComboBox();
	JComboBox featurenames = new JComboBox();
	JComboBox groups = new JComboBox();
	JComboBox featurestates = new JComboBox();
	JTextField fsexample = new JTextField();
	
	public JRadioButton yes = new JRadioButton("是");
	public JRadioButton no = new JRadioButton("否",true);
	public JTextField weights = new JTextField();
	public JTextField explanation = new JTextField();
	
	
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
		
		/*JPanel search3 = new JPanel();
		search3.setLayout(new BoxLayout(search3, BoxLayout.X_AXIS));
		search3.add(BASEUI.alabel("是否必须"));
		search3.add(yes);
		search3.add(no);
		ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(yes);
		bGroup.add(no);
		search3.add(BASEUI.alabel("权重"));
		
		search3.add(weights);
		search3.add(BASEUI.alabel("解释"));
		search3.add(explanation);*/
		
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
		result.add(fsexample);
		
		right.add(search);
		right.add(Box.createVerticalStrut(5));
		right.add(search2);
		right.add(Box.createVerticalStrut(5));
//		right.add(search3);
		right.add(Box.createVerticalStrut(5));
		right.add(result);
		

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
				appendLHS();
			}
		});
	}
	
	private void initValues() {

		classcombo.addItem("");
		for(int i=0;i<CONST.classes.length;i++) {
			classcombo.addItem(CONST.classes[i]);
		}
	}
	
	
	private void appendLHS() {
		String ruleContent = fsexample.getText();
		ruleContent = "\t\t"+ruleContent+"\n";
		parentObj.lhsEditor.append(ruleContent);
		
	}
}