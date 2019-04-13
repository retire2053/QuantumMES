package qmes.base;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import qmes.rule.ui.LineNumberHeaderView;

public class TextEditUI {

	JTextArea infotext = new JTextArea();
	private File path = null;
	private boolean editting = false;
	JDialog dialog = null;

	public void createAndShowGUI(JFrame parent, String content) {
		createAndShowGUI(parent, (File)null);
		editting = true;
		infotext.setText(content);
		updateStatus();
	}
	
	public void createAndShowGUI(JFrame parent, File path) {
	
		Container container = null;
		
		this.path = path;

		dialog = new JDialog(parent, (path==null?"[New File]":path.getAbsolutePath()));
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		JScrollPane jsp = new JScrollPane(infotext);
		jsp.setRowHeaderView(new LineNumberHeaderView(CONST.DEFAULT_FONT_BIG));
		container.add(jsp);
		infotext.setText("");
		infotext.setFont(CONST.DEFAULT_FONT_BIG);
		
		JPanel bottom = createControls();
		
		container.add(bottom);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		int x = (int) (d.getWidth() / 8);
		int y = (int) (d.getHeight() / 8);
		int width = 5 * x;
		int height = 5 * y;
		
		dialog.setBounds(x, y, width, height);
		dialog.setVisible(true);

		initListeners();
		initValues();
		
	}
	
	private JButton sort = new JButton("排序");
	private JButton saveas = new JButton("另存为...");
	private JButton save = new JButton("保存");
	private JPanel createControls() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(sort);
		panel.add(save);
		panel.add(saveas);
		return panel;
	}
	
	private void initListeners() {
		
		sort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] lines = infotext.getText().split("\n");
				List<String> list = new ArrayList<String>();
				for(String s: lines)list.add(s);
				list.sort(new Comparator<String>() {
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});
				
				StringBuffer sb = new StringBuffer();
				for(String s: list)sb.append(s+"\n");
				infotext.setText(sb.toString());
				editting = true;
				updateStatus();
			}
		});
		
		infotext.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				if(editting==false) {
					editting = true;
					updateStatus();
				}
			}
			
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					FileUtil.saveFileContent(path, infotext.getText());
					JOptionPane.showMessageDialog(dialog, "文件\""+path.getAbsolutePath()+"已经保存");
					
					editting = false;
					updateStatus();
				}catch(Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(dialog, "文件保存出错，请查看日志");
				}
			}
		});
		
		saveas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				File file = FileUtil.saveAs(dialog);
				if(file!=null) {
					try {
						
						FileUtil.saveFileContent(file, infotext.getText());
						JOptionPane.showMessageDialog(dialog, "文件\""+file.getAbsolutePath()+"已经保存");
						
						editting = false;
						path = file;	//更新到新的path上
						updateStatus();
					}catch(Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(dialog, "文件保存出错，请查看日志");
					}
				}
			}
			
		});
		
	}
	
	private void updateStatus() {
		
		if(editting && path==null) {
			dialog.setTitle("[新文件] **");
		}else if(editting && path!=null) {
			dialog.setTitle(path.getAbsolutePath()+" **");
		}else if(!editting && path==null) {
			dialog.setTitle("[新文件]");
		}else if(!editting && path!=null) {
			dialog.setTitle(path.getAbsolutePath());
		}
		
		if(path==null) {
			save.setEnabled(false);
		}else {
			save.setEnabled(true);
		}
	}
	
	private void initValues() {
		
		updateStatus();
		if(path!=null) {
			try {
				String content = FileUtil.loadFileContent(path);
				infotext.setText(content);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void openFileAndEdit(JFrame frame) {
		JFileChooser fc = new JFileChooser();
		int ret= fc.showOpenDialog(frame);
		if(ret==fc.APPROVE_OPTION) {
			File path = fc.getSelectedFile();
			TextEditUI ui = new TextEditUI();
			ui.createAndShowGUI(frame, path);
		}
	}
	
	public static void createNewFile(JFrame frame) {
		TextEditUI ui = new TextEditUI();
		ui.createAndShowGUI(frame, (File)null);

	}

}