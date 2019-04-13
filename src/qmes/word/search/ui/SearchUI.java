package qmes.word.search.ui;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.core.Model;
import qmes.model.HuskyObject;

public class SearchUI {
	
	private static final Logger log = LoggerFactory.getLogger(SearchUI.class);
	
	private JDialog dialog = null;
	
	public void createAndShowGUI(JFrame parent, Model model) {

		dialog = new JDialog(parent, "模糊搜索特征");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		Container container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		SearchPanel sp = new SearchPanel();
		sp.create(model);
		
		sp.addSelectedListener(new SelectionListener() {

			public void select(String namespace, String clazz, String featurename, String featurestate) {
				log.info("namespace={},clazz={},fn={},fs={}", namespace, clazz, featurename, featurestate);
				
			}

			public void updateLhs(String lhs) {
				log.info("lhs={}",lhs);
				
			}

			public void updateRhs(String rhs) {
				log.info("rhs={}", rhs);
				
			}

			public void updateObject(HuskyObject ho) {
				log.info("huskyobject={}", ho);
			}
			
		});
		
		container.add(sp);
		
		dialog.setBounds(200, 200, 800, 300);
		dialog.setVisible(true);

	}

}