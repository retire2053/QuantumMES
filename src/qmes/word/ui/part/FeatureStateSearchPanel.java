package qmes.word.ui.part;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;
import qmes.word.def.Word;
import qmes.word.search.Searcher;
import qmes.word.storage.WordStorage;

public class FeatureStateSearchPanel extends JPanel{
	
	private static final Logger log = LoggerFactory.getLogger(FeatureStateSearchPanel.class);
	
	private JTextField name = new JTextField();
	private JTextField group = new JTextField();
	
	private JButton clean = new JButton("清空");
	private JButton search = new JButton("搜索");
	
	private Model model = null;
	private List<FeatureName> featurenames = new ArrayList<FeatureName>();
	
	
	public FeatureStateSearchPanel(Model model) {
		
		this.model = model;
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		this.add(BASEUI.alabel("特征名"));
		this.add(name);
		this.add(BASEUI.alabel("组名"));
		this.add(group);
		this.add(clean);
		this.add(search);
		
		initValues();
		initListeners();
	}
	
	private void initValues() {
	}
	
	private void initListeners() {
		
		clean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				group.setText("");
			}
		});
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		name.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if(e.getKeyCode()==10)search();
			}
			
		});
		group.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if(e.getKeyCode()==10)search();
			}
			
		});
		
	}
	
	private void search() {
		try {
			Searcher searcher = new Searcher(model.getSearchSingleWordTempSubPath());
			List<Query> queries = new ArrayList<Query>();
			if (searcher != null) {

				queries.add(searcher.createQuery(CONST.SEARCH_FN_OR_FS, CONST.SEARCH_FEATURE_STATE));

				if (name.getText().trim().length() > 0) {
					Builder b = new BooleanQuery.Builder();
					b.add(searcher.createQuery(CONST.SEARCH_FEATURE_STATE, name.getText().trim()),
							BooleanClause.Occur.SHOULD);
					b.add(searcher.createQuery(CONST.SEARCH_FEATURE_STATE_SYNONYM, name.getText().trim()),
							BooleanClause.Occur.SHOULD);
					queries.add(b.build());
				}
				if (group.getText().trim().length() > 0) {
					queries.add(searcher.createQuery(CONST.SEARCH_GROUP, group.getText().trim()));
				}

				List<FeatureState> fss = new ArrayList<FeatureState>();
				WordStorage ws = model.getWordStorage(CONST.NAMESPACE, CONST.TYPE_FEATURE_STATES);

				if (queries.size() == 0) {
					List<Word> words = ws.listWords();
					for (Word w : words) {
						fss.add((FeatureState) w);
					}
				} else if (queries.size() > 0) {
					Set<String> repeatFS = new HashSet<String>();	//这个地方因为暂时还不能很好掌握Lucene Delete
					List<Document> documents = searcher.search(queries);
					for (int i = 0; i < documents.size(); i++) {
						String fn = documents.get(i).get(CONST.SEARCH_FEATURE_STATE);
						if(!repeatFS.contains(fn)) {
							FeatureState w = (FeatureState) ws.findWord(fn);
							if (w != null) {
								fss.add(w);
								repeatFS.add(fn);
							}
						}
					}
				}

				if (listeners != null) {
					for (ObjectListener al : listeners) {
						al.action(fss);
					}
				}

			} else {
				BASEUI.promptError(null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			BASEUI.promptError(null);
		}

	}
	
	private List<ObjectListener> listeners = new ArrayList<ObjectListener>();
	public void addActionListener(ObjectListener al) {
		listeners.add(al);
	}
	
	
	

}
