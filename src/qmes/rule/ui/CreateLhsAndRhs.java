package qmes.rule.ui;

import java.awt.Container;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.model.HuskyObject;
import qmes.word.search.ui.SearchPanel;
import qmes.word.search.ui.SelectionListener;
import qmes.word.storage.WordStorageHelper;

public class CreateLhsAndRhs extends BASEUI implements CONST {

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

		dialog = new JDialog(parent, "创建规则-模糊搜索特征");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();
		
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

		container.add(Box.createHorizontalStrut(5));
		container.add(createContent());
		container.add(Box.createHorizontalStrut(5));
		
		dialog.setBounds(170, 170, 1000, 380);
		dialog.setVisible(true);
		
		initValues();
		initListeners();
	}
	
	private JPanel createContent() {
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(Box.createVerticalStrut(5));
		SearchPanel sPanel = new SearchPanel();
		sPanel.setShowState(true, true, false);
		sPanel.create(model);
		
		sPanel.addSelectedListener(new SelectionListener() {
			@Override
			public void updateRhs(String rhs) {
				// TODO 自动生成的方法存根
				rhs = "\t\t"+rhs+"\n";
				parentObj.rhsEditor.append(rhs);
			}
			
			@Override
			public void updateObject(HuskyObject ho) {
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void updateLhs(String lhs) {
				// TODO 自动生成的方法存根
				lhs = "\t\t"+lhs+"\n";
				parentObj.lhsEditor.append(lhs);
			}
			
			@Override
			public void select(String namespace, String clazz, String featurename, String featurestate) {
				// TODO 自动生成的方法存根
				
			}
		});
		content.add(sPanel);
		
		return content;
	}
	
	private void initListeners() {
		
	}
	
	private void initValues() {
		
	}

}