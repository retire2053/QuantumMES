package qmes.word.search.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.word.search.HintHelper;
import qmes.word.search.Searcher;
import qmes.word.search.TrinityIndexer;

public class SearchPanel extends JPanel {

	private static final Logger log = LoggerFactory.getLogger(SearchPanel.class);

	private JTextField search = new JTextField();
	private JCheckBox onlyshowx = new JCheckBox("仅仅显示主词结果");
	private JButton action = new JButton("搜索");
	private JButton rebuildindex = new JButton("重建索引");
	private JTable result = new JTable();
	private JTextField lhshint = new JTextField();
	private JTextField rhshint = new JTextField();
	private JTextField casehint = new JTextField();
	private JButton lhsconfirm = new JButton("确认");
	private JButton rhsconfirm = new JButton("确认");
	
	private Model model = null;
	
	private boolean isShowLhs = true;
	private boolean isShowRhs = true;
	private boolean isShowCase = true;
	
	private HintHelper hh = null;
	
	public void setShowState(boolean showLhs, boolean showRhs, boolean showCase) {
		this.isShowLhs = showLhs;
		this.isShowRhs = showRhs;
		this.isShowCase = showCase;
	}

	public void create(Model model) {

		this.model = model;
		
		hh = new HintHelper(model);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(search);
		panel.add(action);
		panel.add(rebuildindex);
		
		add(panel);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(onlyshowx);
		onlyshowx.setSelected(false);
		p2.add(Box.createHorizontalGlue());
		add(p2);
		
		add(new JScrollPane(result));
		
		if(isShowLhs) {
			JPanel p3 = new JPanel();
			p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
			p3.add(BASEUI.alabel("给LHS的建议"));
			p3.add(lhshint);
			p3.add(lhsconfirm);
			add(p3);
		}
		
		if(isShowRhs) {
			JPanel p4 = new JPanel();
			p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
			p4.add(BASEUI.alabel("给RHS的建议"));
			p4.add(rhshint);
			p4.add(rhsconfirm);
			add(p4);
		}
		
		if(isShowCase) {
			JPanel p5 = new JPanel();
			p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
			p5.add(BASEUI.alabel("给案例的建议"));
			p5.add(casehint);
			add(p5);
			
		}
		
		
		initValues();
		initListeners();

	}

	private void initValues() {

		String[] names = new String[] { "命名空间", "大类", "特征名", "组", "特征值", "特征名被匹配词", "特征值被匹配词", "" };
		int[] width = new int[] { 100, 200, 150, 120, 150, 150, 150, 10 };
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				if ("x".equals(table.getValueAt(row, 7))) {
					setForeground(Color.BLACK);
				} else {
					setForeground(Color.LIGHT_GRAY);
				}
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};

		result.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		result.setModel(new DefaultTableModel(names, 0) {
			public boolean isCellEditable(int row, int column) {return false;}
		});
		result.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i = 0; i < names.length; i++) {
			result.getColumnModel().getColumn(i).setPreferredWidth(width[i]);
			result.getColumnModel().getColumn(i).setCellRenderer(tcr);
		}

	}
	
	private List<SelectionListener> listeners = new ArrayList<SelectionListener>();
	public void addSelectedListener(SelectionListener sl) {
		listeners.add(sl);
	}
	
	private void initListeners() {
		
		lhsconfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lhshint.getText()!=null && lhshint.getText().trim().length()>0) {
					if(listeners.size()>0) {
						for(int i=0;i<listeners.size();i++) {
							try {
								SelectionListener sl = listeners.get(i);
								sl.updateLhs(lhshint.getText().trim());
							}catch(Exception ex) {
								ex.printStackTrace();
								log.error(ex.getMessage());
							}
						}
					}
				}
			}
		});
		
		rhsconfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rhshint.getText()!=null && rhshint.getText().trim().length()>0) {
					if(listeners.size()>0) {
						for(int i=0;i<listeners.size();i++) {
							try {
								SelectionListener sl = listeners.get(i);
								sl.updateRhs(rhshint.getText().trim());
							}catch(Exception ex) {
								ex.printStackTrace();
								log.error(ex.getMessage());
							}
						}
					}
				}
			}
		});
		
		result.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				
				int rows[] = result.getSelectedRows();
				
				if(rows!=null && rows.length>=2) {
					
					int row = rows[0];
					String namespace = (String)result.getValueAt(row, 0);
					String clazz = (String)result.getValueAt(row, 1);
					String fn = (String)result.getValueAt(row, 2);
					String group = (String)result.getValueAt(row, 3);
					
					String[] fss =  new String[rows.length];
					fss[0] = (String)result.getValueAt(row, 4);
					
					boolean same = true;
					
					for(int i=1;i<rows.length;i++) {
						fss[i] = (String)result.getValueAt(rows[i], 4);
						if(!namespace.equals(result.getValueAt(rows[i], 0)) ||
								!clazz.equals(result.getValueAt(rows[i],1)) ||
								!fn.equals(result.getValueAt(rows[i],2)) ||
								!group.equals(result.getValueAt(rows[i],3))){
							same=false;
						}
					}
					
					String[] s = clazz.split("\\.");
					clazz = s[s.length-1];
					
					if(same) {
						lhshint.setText(hh.getHintForLhs(clazz, fn, fss));
						rhshint.setText(hh.getHintForRhs(clazz, fn, fss));
					}else {
						lhshint.setText(hh.getHintForLhs(clazz, fn, fss[0]));
						rhshint.setText(hh.getHintForRhs(clazz, fn, fss[0]));
					}
					notifyevent(namespace, clazz, fn, fss[0]);

					
				}else {
					int row = result.getSelectedRow();
					String namespace = (String)result.getValueAt(row, 0);
					String clazz = (String)result.getValueAt(row, 1);
					String[] s = clazz.split("\\.");
					clazz = s[s.length-1];
					String fn = (String)result.getValueAt(row, 2);
					String fs = (String)result.getValueAt(row, 4 );
					
					lhshint.setText(hh.getHintForLhs(clazz, fn, fs));
					rhshint.setText(hh.getHintForRhs(clazz, fn, fs));
					
					//generate event for listeners
					notifyevent(namespace, clazz, fn, fs);
				}
			}
		});
		
		

		search.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				String text = search.getText();
				if (e.getKeyCode() == 10 && text.trim().length() > 0) {
					searchaction(text);
				}
			}
		});

		action.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = search.getText();
				if (text == null || text.trim().length() == 0) {
					JOptionPane.showMessageDialog(getParent(), "请先输入一个可能的特征名");
				} else {
					searchaction(text);
				}
			}
		});
		
		rebuildindex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TrinityIndexer indexer = null;
				try {
					indexer = new TrinityIndexer(model, model.getSearchTempSubPath());
					indexer.deleteAll();
					indexer.createIndex();
					indexer.close();
					JOptionPane.showMessageDialog(null, "成功重新建设索引");
				} catch (Exception ex) {
					ex.printStackTrace();
					log.error(ex.getMessage());
					JOptionPane.showMessageDialog(null, ex.getMessage());
					try {
						if (indexer != null)
							indexer.close();
					} catch (Exception ex2) {
						ex2.printStackTrace();
						log.error(ex2.getMessage());
						JOptionPane.showMessageDialog(null, ex2.getMessage());
					}
				}
			}
		});
	}
	
	private void notifyevent(String namespace, String clazz, String fn, String fs) {
		if (listeners.size() > 0) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					SelectionListener sl = listeners.get(i);
					sl.select(namespace, clazz, fn, fs);
				} catch (Exception ex) {
					ex.printStackTrace();
					log.error(ex.getMessage());
				}
			}
		}
	}

	private void searchaction(String text) {
		try {
			Searcher s = new Searcher(model.getSearchTempSubPath());
			List<Document> docs = s.search(CONST.SEARCH_CONTENT, text);
			DefaultTableModel dtm = BASEUI.cleanTable(result);

			if (docs.size() > 0) {
				for (int i = 0; i < docs.size(); i++) {
					Document doc = docs.get(i);
					String clazz = doc.get(CONST.SEARCH_CLASS);
					String fn = doc.get(CONST.SEARCH_FEATURE_NAME);
					String fs = doc.get(CONST.SEARCH_FEATURE_STATE);
					String ns = doc.get(CONST.SEARCH_NAMESPACE);
					String fnword = doc.get(CONST.SEARCH_FEATURE_NAME_SYNONYM);
					String fsword = doc.get(CONST.SEARCH_FEATURE_STATE_SYNONYM);
					String group = doc.get(CONST.SEARCH_GROUP);
					String main = "";
					if (fn.equals(fnword))
						main = "x";
					
					if(onlyshowx.isSelected()) {
						if("x".equals(main)) {
							dtm.addRow(new Object[] { ns, clazz, fn, group, fs, fnword, fsword, main });
						}
					}else {
						dtm.addRow(new Object[] { ns, clazz, fn, group, fs, fnword, fsword, main });
					}
				}
			}
			result.setModel(dtm);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}
	

}
