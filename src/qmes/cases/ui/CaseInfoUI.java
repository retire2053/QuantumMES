package qmes.cases.ui;

import java.awt.Container;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.cases.def.CaseDef;
import qmes.core.Model;

public class CaseInfoUI {
	
	private static final Logger log = LoggerFactory.getLogger(CaseInfoUI.class);
	
	public void createAndShowGUI(JFrame parent, Model model, CaseDef casedef) {
		
		JDialog dialog = null;
		Container container = null;

		dialog = new JDialog(parent, "查看案例 - \""+casedef.getName()+"\"");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(Box.createVerticalStrut(10));
		
		container.add(new CaseInfoPanel(casedef, model,600,400));
		container.add(Box.createVerticalStrut(10));

		dialog.setBounds(300, 300, 800, 550);
		dialog.setVisible(true);
	}

}


