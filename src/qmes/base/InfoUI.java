package qmes.base;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InfoUI extends BASEUI implements CONST {

	JTextArea infotext = new JTextArea();
	JEditorPane ep = new JEditorPane();
	
	public static int TYPE_HTML = 1;
	public static int TYPE_TEXT = 0;
	
	public void createAndShowGUI(JFrame parent, String text, String title, int type) {

		JDialog dialog = null;
		Container container = null;

		dialog = new JDialog(parent, title);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		if(type==TYPE_HTML) {
			container.add(new JScrollPane(ep));
			ep.setContentType("text/html");
			ep.setText(text);
		}else if(type==TYPE_TEXT) {
			container.add(new JScrollPane(infotext));
			infotext.setText(text);
		}else {
			System.out.println("not implemented yet.");
		}
		

		dialog.setLocation(400, 300);
		dialog.setBounds(200, 200, 800, 480);
		// dialog.pack();
		dialog.setVisible(true);

	}

}