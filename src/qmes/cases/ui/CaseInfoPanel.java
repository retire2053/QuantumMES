package qmes.cases.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.BaseTable;
import qmes.base.CONST;
import qmes.cases.def.CaseDef;
import qmes.cases.def.CaseHelper;
import qmes.core.Model;
import qmes.model.HuskyObject;
import qmes.nlp.ui.MatchableHelper;
import qmes.rule.execution.ExecutionProcess;

public class CaseInfoPanel extends JPanel {

	private static final Logger log = LoggerFactory.getLogger(CaseInfoPanel.class);
	
	private MatchableHelper matchableHelper = null;

	public CaseInfoPanel(CaseDef casedef, Model model,int width, int height) {
		this.model = model;
		this.casedef = casedef;
		this.width = width;
		this.height = height;
		
		matchableHelper = new MatchableHelper(model);
		
		createBasicInfo();
	}

	private Model model;
	private CaseDef casedef;
	private CaseHelper helper = new CaseHelper();
	
	private int width = 600;
	private int height = 400;

	public void setCase(CaseDef casedef) {
		this.casedef = casedef;
	}

	public CaseDef getCase() {
		return casedef;
	}

	JTextField name = new JTextField();
	JTextField taginput = new JTextField();
	JTextField remarks = new JTextField();
	JTextArea document = new JTextArea();
	JTable huskylist = new BaseTable();
	JTable calcTable = new BaseTable();
	JTable parentTable = new BaseTable();
	JButton export = new JButton("导出案例");

	private void createBasicInfo() {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(Box.createHorizontalStrut(5));
		p1.add(BASEUI.alabel("案例名称"));
		p1.add(name);
		p1.add(BASEUI.alabel("案例标签"));
		p1.add(taginput);
		p1.add(Box.createHorizontalStrut(5));
		
		JPanel pa = new JPanel();
		pa.setLayout(new BoxLayout(pa, BoxLayout.X_AXIS));
		pa.add(Box.createHorizontalStrut(5));
		pa.add(BASEUI.alabel("特征文本"));
		pa.add(Box.createHorizontalGlue());

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(Box.createHorizontalStrut(5));
		p2.add(BASEUI.alabel("案例备注信息"));
		p2.add(remarks);
		p2.add(Box.createHorizontalStrut(5));

		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
		p4.add(Box.createHorizontalStrut(5));
		p4.add(BASEUI.alabel("创建的标准化输入"));
		p4.add(Box.createHorizontalGlue());

		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		p3.add(Box.createHorizontalStrut(5));
		JScrollPane jsp1 = new JScrollPane(huskylist);
		huskylist.setFont(CONST.DEFAULT_FONT);
		p3.add(jsp1);
		jsp1.setPreferredSize(new Dimension(this.width, 400));
		p3.add(Box.createHorizontalStrut(5));

		JPanel p8 = new JPanel();
		p8.setLayout(new BoxLayout(p8, BoxLayout.X_AXIS));
		p8.add(Box.createHorizontalStrut(5));
		JScrollPane jsp2 = new JScrollPane(calcTable);
		p8.add(jsp2);
		calcTable.setFont(CONST.DEFAULT_FONT);
		jsp2.setPreferredSize(new Dimension(this.width, 130));
		p8.add(Box.createHorizontalStrut(5));

		JPanel p9 = new JPanel();
		p9.setLayout(new BoxLayout(p9, BoxLayout.X_AXIS));
		p9.add(Box.createHorizontalStrut(5));
		JScrollPane jsp3 = new JScrollPane(parentTable);
		p9.add(jsp3);
		parentTable.setFont(CONST.DEFAULT_FONT);
		jsp3.setPreferredSize(new Dimension(this.width, 130));
		p9.add(Box.createHorizontalStrut(5));

		JPanel p6 = new JPanel();
		p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));
		p6.add(Box.createHorizontalStrut(5));
		p6.add(BASEUI.alabel("案例的原始文档"));
		p6.add(Box.createHorizontalGlue());
		p6.add(Box.createHorizontalStrut(5));

		JPanel p5 = new JPanel();
		p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
		p5.add(Box.createHorizontalStrut(5));
		p5.add(new JScrollPane(document));
		document.setRows(10);
		document.setFont(CONST.DEFAULT_FONT);
		document.setPreferredSize(new Dimension(WIDTH-20, 200));
		document.setLineWrap(true);
		document.setWrapStyleWord(true);
		p5.add(Box.createHorizontalStrut(5));
		
		JPanel p10 = new JPanel();
		p10.setLayout(new BoxLayout(p10, BoxLayout.X_AXIS));
		p10.add(Box.createHorizontalGlue());
		p10.add(export);
		p10.add(Box.createHorizontalStrut(5));

		add(p1);
		add(p2);
		add(pa);
		add(p4);
		add(p3);
		add(p8);
		add(p9);
		add(p6);
		add(p5);
		add(p10);

		updateUIWithCurrentCase();
		
		export.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String ext = ".case";
				CaseHelper ch = new CaseHelper();
				JFileChooser jfc = new JFileChooser();
				jfc.setMultiSelectionEnabled(false);
				jfc.showSaveDialog(null);
				File file = jfc.getSelectedFile();
				
				if(file==null)return;
				
				if (!file.getName().toLowerCase().endsWith(ext)) {
					file = new File(file.getAbsolutePath() + ext);
				}
				if (ch.toFile(file, casedef)) {
					String parentPath = file.getParentFile().toString();
					BASEUI.openFileInExplorer(null, parentPath);
				} else {
					BASEUI.promptError(null);
				}
			}
			
			
		});
	}

	private void updateUIWithCurrentCase() {

		name.setEnabled(false);
		taginput.setEnabled(false);
		remarks.setEnabled(false);
		document.setEnabled(false);
		huskylist.setEnabled(true);

		name.setText(casedef.getName());
		taginput.setText(BASEUI.stringArrayToString(casedef.getTags()));
		remarks.setText(casedef.getRemarks());
		document.setText(casedef.getDocument());

		DefaultTableModel dtm = new DefaultTableModel(new String[] { "已经输入的特征明细" }, 0);

		DefaultTableModel calcModel = new DefaultTableModel(new String[] { "根据区间化和时序化衍生的特征明细" }, 0);

		DefaultTableModel parentModel = new DefaultTableModel(new String[] { "根据特征名的上级词衍生的特征明细" }, 0);

		if (casedef.getHuskys().size() > 0) {
			try {
				List<HuskyObject> hos = helper.wrapper2Husky(casedef.getHuskys());
				helper.sortHuskyList(hos);
				
				for (int i = 0; i < hos.size(); i++) {
					HuskyObject ho = (HuskyObject) hos.get(i);
					dtm.addRow(new Object[] {matchableHelper.translateHuskyObject(ho)});
				}

				ExecutionProcess ep = new ExecutionProcess(model);
				List<HuskyObject> hos2 = ep.calcTS(hos);
				if (hos2 == null || hos2.size() == 0) {
					calcModel.addRow(new Object[] { "没有生成区间和时序衍生特征" });
				} else {
					for (int i = 0; i < hos2.size(); i++) {
						HuskyObject ho = (HuskyObject) hos2.get(i);
						calcModel.addRow(new Object[] { matchableHelper.translateHuskyObject(ho) });
					}
				}

				List<HuskyObject> hos3 = ep.generateParents4FN(hos, CONST.NAMESPACE);
				hos3.addAll(ep.generateParents4FS(hos, CONST.NAMESPACE));
				if (hos3 == null || hos3.size() == 0) {
					parentModel.addRow(new Object[] {"没有上级词对象生成" });

				} else {
					for (int i = 0; i < hos3.size(); i++) {
						HuskyObject ho = (HuskyObject) hos3.get(i);
						parentModel.addRow(new Object[] { matchableHelper.translateHuskyObject(ho) });
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}
		huskylist.setModel(dtm);
		huskylist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		huskylist.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		huskylist.getColumnModel().getColumn(0).setPreferredWidth(600);

		calcTable.setModel(calcModel);
		calcTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		calcTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		calcTable.getColumnModel().getColumn(0).setPreferredWidth(600);

		parentTable.setModel(parentModel);
		parentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		parentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		parentTable.getColumnModel().getColumn(0).setPreferredWidth(600);

	}
}
