package qmes.word.ui;

import java.awt.Container;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import qmes.base.CONST;
import qmes.core.Model;
import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;
import qmes.word.storage.FeatureNameStorage;
import qmes.word.storage.FeatureStateStorage;
import qmes.word.ui.part.FeatureNameSearchPanel;
import qmes.word.ui.part.FeatureNameTable;
import qmes.word.ui.part.FeatureStateSearchPanel;
import qmes.word.ui.part.FeatureStateTable;
import qmes.word.ui.part.ObjectListener;

public class WordUI2 {
	
	
	private JFrame frame = null;
	private JDialog dialog = null;
	
	FeatureNameSearchPanel fnsp;
	FeatureNameTable fnt ;
	
	FeatureStateSearchPanel fssp;
	FeatureStateTable fst;
	
	Model model;
	FeatureNameStorage wsfn = null;
	FeatureStateStorage wsfs = null;
	
	public void createAndShowGUI(JFrame parent, Model model) {

		this.model = model;
		wsfn = (FeatureNameStorage)model.getWordStorage(CONST.NAMESPACE, CONST.TYPE_FEATURE_NAMES);
		wsfs = (FeatureStateStorage)model.getWordStorage(CONST.NAMESPACE, CONST.TYPE_FEATURE_STATES);
		
		frame = parent;
		dialog = new JDialog(parent, "三元组管理");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		Container container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		JTabbedPane tp = new JTabbedPane();
		
		JPanel fnpanel = new JPanel();
		fnpanel.setLayout(new BoxLayout(fnpanel,BoxLayout.Y_AXIS));
		fnsp = new FeatureNameSearchPanel(model);
		fnt = new FeatureNameTable(wsfn, wsfs);
		fnpanel.add(fnsp);
		fnpanel.add(fnt);
		
		JPanel fspanel = new JPanel();
		fspanel.setLayout(new BoxLayout(fspanel,BoxLayout.Y_AXIS));
		fssp = new FeatureStateSearchPanel(model);
		fst = new FeatureStateTable(wsfn, wsfs);
		fspanel.add(fssp);
		fspanel.add(fst);
		
		tp.addTab("特征名", fnpanel);
		tp.addTab("特征值", fspanel);
		container.add(tp);
		
		initListeners();
		initValues();

		dialog.setBounds(200, 200, 800, 480);
		dialog.setVisible(true);

	}
	

	private void initListeners() {
		fnsp.addActionListener(new ObjectListener() {

			public void action(Object object) {
				List<FeatureName> fns = (List<FeatureName>)object;
				fnt.updateData(wsfn, fns);
				
			}
			
		});
		
		fssp.addActionListener(new ObjectListener() {

			public void action(Object object) {
				List<FeatureState> fss = (List<FeatureState>)object;
				fst.updateData(wsfs, fss);
				
			}
			
		});
	}
	
	private void initValues() {
		fnt.updateData(wsfn, wsfn.getFeatureNames());
		fst.updateData(wsfs,  wsfs.getFeatureState());
		
	}
	
	


}
