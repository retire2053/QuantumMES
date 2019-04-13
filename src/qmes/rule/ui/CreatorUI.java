package qmes.rule.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.word.def.FeatureName;
import qmes.word.storage.WordStorageHelper;

public class CreatorUI extends BASEUI implements CONST {

	private String basepath = null;
	private JDialog dialog = null;

	
	private Model model = null;
	
	private String templateHeader = "package 输入包名称\n"
			+ "import qmes.model.*;\n"
			+ "import husky.service.*;\n";
	private String ruleTemplate = "rule \"输入规则名称\"\n"
			+ "\twhen\n"
			+ "\t\t//这里开始写你的规则逻辑\n"
			+ "\t\t\n"
			+ "\tthen\n"
			+ "\t\t//这里写推断结果\n"
			+ "\t\t\n"
			+ "\t\tinsert(输入);\n"
			+ "\t\tUtility.helper(drools);\n"
			+ "end\n\n";
	
	private String templateContent = templateHeader + ruleTemplate;
	
	WordStorageHelper wsh = null;
	CascadeHelper ch = null;
	
	public void createAndShowGUI(Model model, JFrame parent, String basepath) {

		this.model = model;
		
		this.basepath = basepath;
		
		Container container = null;

		dialog = new JDialog(parent, "rule编辑器");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

		container.add(Box.createHorizontalStrut(5));
		container.add(createLeft());
		container.add(Box.createHorizontalStrut(5));
		container.add(createRight());
		container.add(Box.createHorizontalStrut(5));
		
		dialog.setBounds(170, 170, 1000, 600);
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
	
	JButton create = new JButton("新建");
	JButton compile = new JButton("检查错误");
	JButton save = new JButton("另存为...");
	JButton appendRule = new JButton("追加规则模板");
	JTextArea editor = new JTextArea();
	
	JButton delete = new JButton("删除");
	JComboBox exists = new JComboBox();
	
	JTextArea errors = new JTextArea();
	
	private JPanel createLeft() {
		JPanel left = new JPanel();
		
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.add(Box.createVerticalStrut(5));
		
		JPanel leftup = new JPanel();
		leftup.setLayout(new BoxLayout(leftup, BoxLayout.X_AXIS));
		leftup.add(create);
		leftup.add(compile);
		leftup.add(save);
		leftup.add(appendRule);
		
		left.add(leftup);
		
		JScrollPane jsp = new JScrollPane(editor);
		jsp.setRowHeaderView(new LineNumberHeaderView(CONST.DEFAULT_FONT));
		left.add(jsp);
		editor.setRows(30);
		editor.setFont(CONST.DEFAULT_FONT);
		editor.setTabSize(4);
		editor.setText(templateContent);
		highlight();
		
		left.add(Box.createVerticalStrut(5));
		
		JPanel leftbottom = new JPanel();
		leftbottom.setLayout(new BoxLayout(leftbottom, BoxLayout.X_AXIS));
		leftbottom.add(alabel("已经保存的.drl文件"));
		leftbottom.add(exists);
		leftbottom.add(delete);
		left.add(leftbottom);
		left.add(Box.createVerticalStrut(5));
		
		left.add(new JScrollPane(errors));
		errors.setRows(6);
		errors.setFont(CONST.DEFAULT_FONT);
		left.setPreferredSize(new Dimension(750, 0));
		return left;
	}
	
	JComboBox classcombo = new JComboBox();
	JComboBox featurenames = new JComboBox();
	JComboBox groups = new JComboBox();
	JComboBox featurestates = new JComboBox();
	JTextField fsexample = new JTextField();
	
	private JPanel createRight() {
		
		
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.add(Box.createVerticalStrut(5));
		
		
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(BASEUI.alabel("选择类"));
		p1.add(classcombo);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(BASEUI.alabel("选择特征名"));
		p2.add(featurenames);
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		p3.add(BASEUI.alabel("选择组名"));
		p3.add(groups);
		
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
		p4.add(BASEUI.alabel("选择特征值"));
		p4.add(featurestates);
		
		right.add(p1);
		right.add(Box.createVerticalStrut(3));
		right.add(p2);
		right.add(Box.createVerticalStrut(3));
		right.add(p3);
		right.add(Box.createVerticalStrut(3));
		right.add(p4);
		right.add(Box.createVerticalStrut(3));
		right.add(BASEUI.alabel("特征值 输出样例"));
		right.add(fsexample);
		right.add(Box.createVerticalStrut(3));
		
		right.add(Box.createVerticalStrut(200));

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
		
		
		
		exists.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					loadDrlContent();
					errors.setText("");
				}catch(Exception ex) {
					ex.printStackTrace();
				}
				
			}
		});
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.setTabSize(4);
				editor.setText(templateContent);
				highlight();
			}
		});
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteDrlFile();
				loadDrlFileList();
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(errors.getText().length()==0) {
					compile();
				}
				if(errors.getText().equals("No error found!")) {
					System.out.println(exists.getSelectedItem());
					addDrlFile();
					loadDrlFileList();
				}else {
					JOptionPane.showMessageDialog(dialog, "规则有误，请检查后再保存");
				}
				
			}
		});
		compile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compile();
			}
		});
		appendRule.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.append(ruleTemplate);
				highlight();
				
			}
		});
		
	}
	
	private void initValues() {
		
		loadDrlFileList();
		
		classcombo.addItem("");
		for(int i=0;i<CONST.classes.length;i++) {
			classcombo.addItem(CONST.classes[i]);
		}
	}
	
	private void addDrlFile() {
		String text = editor.getText();
		String filename = "";
		if(exists.getSelectedIndex()>0) {
			File selectedItem = (File)exists.getSelectedItem();
			filename = selectedItem.getName();
		}
		String input = JOptionPane.showInputDialog(dialog, "请输入.drl文件名称",filename);
		File out = new File(basepath+java.io.File.separator+input);
		try {
			FileOutputStream fos = new FileOutputStream(out);
			fos.write(text.getBytes());
			fos.close();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void deleteDrlFile() {
		if(exists.getSelectedItem() instanceof File) {
			File f = (File)exists.getSelectedItem();
			f.delete();
		}
	}
	private void loadDrlFileList() {
		exists.removeAllItems();
		exists.addItem("");
		File f = new File(this.basepath);
		if(f.isDirectory()) {
			File[] fs = f.listFiles();
			for(int i=0;i<fs.length;i++) {
				if(fs[i].isFile() && fs[i].getName().toLowerCase().endsWith(".drl")) {
					exists.addItem(fs[i]);
				}
			}
		}
		
	}
	private void loadDrlContent() throws Exception{
		if(exists.getSelectedItem() instanceof File) {
			File f = (File)exists.getSelectedItem();
			FileReader fr = new FileReader((f));
			BufferedReader br = new BufferedReader(fr);
			String s;
			StringBuffer sb = new StringBuffer();
            while ((s = br.readLine() )!=null) {
                sb.append(s+"\n");
             }
            fr.close();
			
			editor.setText(sb.toString());
		}
	}
	
	private void compile() {
		
		byte[] bs = editor.getText().getBytes();
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newByteArrayResource(bs), ResourceType.DRL);
	
		if (kbuilder.hasErrors()) {
			Iterator<KnowledgeBuilderError> erritr = kbuilder.getErrors().iterator();
			StringBuffer sb = new StringBuffer();
			while (erritr.hasNext()) {
				KnowledgeBuilderError e = erritr.next();
				sb.append(e.toString() + "\n");
			}
			errors.setText(sb.toString());
		} else {
			errors.setText("No error found!");
		}

	}
	
	private void highlight() {
		Highlighter highlighter = editor.getHighlighter();
		DefaultHighlighter.DefaultHighlightPainter p = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
		String keyWord = "输入";
		String text = editor.getText();
		int pos = 0;
		while ((pos = text.indexOf(keyWord,pos)) >= 0) {
			try {
				highlighter.addHighlight(pos, pos+keyWord.length(), p);
				pos += keyWord.length();
			}catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
	}
	

}