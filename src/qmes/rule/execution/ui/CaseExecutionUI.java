package qmes.rule.execution.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.annotation.AnnotationRule;
import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.cases.def.CaseDef;
import qmes.cases.ui.CaseInfoPanel;
import qmes.core.Model;
import qmes.rule.def.DefRule;
import qmes.rule.execution.result.MatchResult;
import qmes.rule.execution.result.RuleExplanation;
import qmes.rule.execution.result.StackedMatchResult;
import qmes.rule.search.RuleSearch;
import qmes.rule.ui.RuleManage;

public class CaseExecutionUI {
	
	private static final Logger log = LoggerFactory.getLogger(CaseExecutionUI.class);
	
	private Model model = null;
	private StackedMatchResult smr = null;
	
	private JDialog dialog = null;
	private JFrame frame = null;
	
	private int WIDTH = 0;
	private int HEIGHT = 0;
	
	RuleExplanation explanation = null;

	public void createAndShowGUI(JFrame parent,CaseDef casedef, Model model, StackedMatchResult smr) {

		this.model = model;
		this.smr = smr;
		this.frame = parent;
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		WIDTH = (int)d.getWidth();
		HEIGHT = (int)d.getHeight();
		
		Container container = null;

		dialog = new JDialog(parent, "智能建议系统");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JDialog.setDefaultLookAndFeelDecorated(true);
		container = dialog.getContentPane();

		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		
		container.add(new CaseInfoPanel(casedef, model,(int)(WIDTH*0.382),HEIGHT));
		
		JTabbedPane jtp = new JTabbedPane();
		jtp.addTab("运营结果", createOperationPage());
		jtp.addTab("用户结果", createUserPage());
		jtp.addTab("搜索规则", createSearchPanel());
		
		container.add(jtp);
		jtp.setPreferredSize(new Dimension((int)(WIDTH*0.618), HEIGHT));
		container.add(Box.createHorizontalStrut(5));
		
		initValues();
		initListeners();

		dialog.setBounds(0, 0, WIDTH, (int)d.getHeight());
		dialog.setVisible(true);

	}
	
	private JTable prematch = new JTable();
	private JEditorPane ep = new JEditorPane();
	
	private JPanel createOperationPage() {

		JPanel oppanel = new JPanel();
		oppanel.setLayout(new BoxLayout(oppanel, BoxLayout.Y_AXIS));
		
		JScrollPane jsp1 = new JScrollPane(prematch);
		jsp1.setPreferredSize(new Dimension(800, 150));
		
		JScrollPane jsp2 = new JScrollPane(ep);
		ep.setContentType("text/html");
		jsp2.setPreferredSize(new Dimension(800, 300));
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		bottom.add(Box.createHorizontalGlue());
		oppanel.add(jsp1);
		oppanel.add(Box.createVerticalStrut(5));
		oppanel.add(jsp2);
		oppanel.add(Box.createVerticalStrut(5));
		oppanel.add(bottom);
		oppanel.add(Box.createVerticalStrut(5));
		
		return oppanel;
	}
	
	private JEditorPane userep = new JEditorPane();
	
	private JPanel createUserPage() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JScrollPane jsp = new JScrollPane(userep);
		userep.setContentType("text/html");
		
		panel.add(Box.createVerticalStrut(5));
		panel.add(jsp);
		panel.add(Box.createVerticalStrut(5));

		return panel;
	}
	
	JTextField keyword = new JTextField();
	JTextField lhsKeyword = new JTextField();
	JTextField rhsKeyword = new JTextField();
	JButton searchbutton = new JButton("搜索规则");
	JTable searchruletable = new JTable();
	JTextArea rulecontent = new JTextArea();
	JButton editthisrule = new JButton("编辑这个规则");
	
	private JPanel createSearchPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel search2 = new JPanel();
		search2.setLayout(new BoxLayout(search2, BoxLayout.X_AXIS));
		search2.setBorder(BorderFactory.createTitledBorder("搜索引擎"));
		search2.add(BASEUI.alabel("规则名称"));
		search2.add(keyword);
		search2.add(BASEUI.alabel("LHS"));
		search2.add(lhsKeyword);
		search2.add(BASEUI.alabel("RHS"));
		search2.add(rhsKeyword);
		search2.add(searchbutton);
		
		JPanel tablepanel = new JPanel();
		tablepanel.setLayout(new BoxLayout(tablepanel, BoxLayout.X_AXIS));
		JScrollPane jsp = new JScrollPane(searchruletable);
		tablepanel.add(jsp);
		
		JScrollPane jsp2 = new JScrollPane(rulecontent);
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		bottom.add(Box.createHorizontalGlue());
		bottom.add(editthisrule);
		
		
		search2.setPreferredSize(new Dimension((int)(WIDTH*0.6), 50));
		tablepanel.setPreferredSize(new Dimension((int)(WIDTH*0.6), (int)(HEIGHT*0.5)));
		jsp2.setPreferredSize(new Dimension((int)(WIDTH*0.6), (int)(HEIGHT*0.5)));
		
		panel.add(search2);
		panel.add(tablepanel);
		panel.add(jsp2);
		panel.add(bottom);
		
		return panel;
	}
	
	private void initListeners() {
		
		prematch.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int row = prematch.getSelectedRow();
				MatchResult mr = (MatchResult)prematch.getValueAt(row, 0);
				DefRule rule = mr.getRule();
				if(e.getClickCount()==2) {
					RuleManage rm = new RuleManage();
					rm.createAndShowGUI(model, frame, model.getRuleFileSubPath(), rule.getName());
				}else if(e.getClickCount()==1) {
					String html = explanation.showRuleWithAnnotation(rule, mr);
					ep.setText(html);
				}
			}
		});
		
		searchbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("start searchRule");
				searchRule(keyword.getText(),lhsKeyword.getText(),rhsKeyword.getText());
			}
		});
		
		searchruletable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = searchruletable.getSelectedRow();
				String rulename = (String)searchruletable.getValueAt(row, 0);
				DefRule dr = model.getRuleByName(rulename);
				rulecontent.setText(dr.toString());
			}
		});
		
		editthisrule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = searchruletable.getSelectedRow();
				if(row>=0) {
					String rulename = (String)searchruletable.getValueAt(row, 0);
					RuleManage rm = new RuleManage();
					rm.createAndShowGUI(model, frame, model.getRuleFileSubPath(), rulename);
				}
			}
		});
		
	}
	
	private void initValues() {
		
		explanation = new RuleExplanation(model);
		
		DefaultTableModel dtm = new DefaultTableModel(new String[] {"规则名称","匹配类型", "匹配得分", "总分", "匹配率", "规则解释"}, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		prematch.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		prematch.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		appendMRS(dtm, CONST.MATCH_TYPE_TAG,smr.getTagMatch());
		appendMRS(dtm, CONST.MATCH_TYPE_TRIGGER,smr.getTriggered());
		appendMRS(dtm, CONST.MATCH_TYPE_PREMATCH,smr.getPrematch());		
		
		prematch.setModel(dtm);
		prematch.getColumnModel().getColumn(0).setPreferredWidth(200);
		prematch.getColumnModel().getColumn(1).setPreferredWidth(60);
		prematch.getColumnModel().getColumn(2).setPreferredWidth(60);
		prematch.getColumnModel().getColumn(3).setPreferredWidth(60);
		prematch.getColumnModel().getColumn(4).setPreferredWidth(60);
		prematch.getColumnModel().getColumn(5).setPreferredWidth(300);
		
		String exp = explanation.showStackedMatchResult(smr);
		userep.setText(exp);
		
		updateSearchResultList(null);
		
	}
	
	private void appendMRS(DefaultTableModel dtm, String type, List<MatchResult> mrs) {
		if (mrs != null && mrs.size() > 0) {
			for (int i = 0; i < mrs.size(); i++) {
				MatchResult mr = mrs.get(i);
				AnnotationRule ar = model.getAnnotationRule(mr.getRule());
				String ratio = "0%";
				if(mr.getTotalscore()!=0) {
					int v = (mr.getValue()*100/mr.getTotalscore());
					ratio = v+"%";
				}
				dtm.addRow(new Object[] { mr, type, mr.getValue(), mr.getTotalscore(), ratio, ar.getMeaning() });
			}
		}
	}
	
	/**
	 * 这段代码和RuleManager中的代码有所重复，需要优化
	 * @param keyword
	 * @param lhsKeyword
	 * @param rhsKeyword
	 */
	private void searchRule(String keyword,String lhsKeyword,String rhsKeyword) {
		try {
			RuleSearch rSearch = new RuleSearch(model.getRuleSearchTempSubPath());
			List<Document> docslhs = new ArrayList<Document>();;
			List<Document> docsrhs = new ArrayList<Document>();;
			List<Document> docsRuleName = new ArrayList<Document>();;
			List<Document> docs = new ArrayList<Document>();
			
			ArrayList<String> afList = new ArrayList<String>();
			ArrayList<String> qList = new ArrayList<String>();
			if(keyword.length()>0) {
				afList.add("ruleName");
				qList.add(keyword);
			}
			if(lhsKeyword.length()>0) {
				afList.add("lhs");
				qList.add(lhsKeyword);
			}
			if(rhsKeyword.length()>0) {
				afList.add("rhs");
				qList.add(rhsKeyword);
			}
			log.info("开始检索:"+qList.toString());
			int n = afList.size();
			List<String> searchRule=new ArrayList<>();
			if(n>0) {
				String[] field = new String[n];
				String[] queryString = new String[n];
				
				for(int i=0; i<afList.size();i++) {
					field[i] = afList.get(i).toString();
					queryString[i] = qList.get(i).toString();
				}
				docsRuleName = rSearch.search(field, queryString);
				docs.addAll(docsRuleName);
				
				log.info("检索出文档数："+docs.size());
				
				if (docs.size() > 0) {
					for (int i = 0; i < docs.size(); i++) {
						Document doc = docs.get(i);
						String ruleName = doc.get("ruleName");
						searchRule.add(ruleName);
					}
				}
				updateSearchResultList(searchRule);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	private void updateSearchResultList(List<String> ruleresult) {
		DefaultTableModel dtm = new DefaultTableModel(new String[] {"规则名称"}, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		if(ruleresult!=null && ruleresult.size()>0) {
			for(int i=0;i<ruleresult.size();i++) {
				dtm.addRow(new Object[] {ruleresult.get(i)});
			}
		}
		
		searchruletable.setModel(dtm);
		
		searchruletable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		searchruletable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		searchruletable.getColumnModel().getColumn(0).setPreferredWidth(600);
	}

}