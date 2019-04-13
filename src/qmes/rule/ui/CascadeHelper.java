package qmes.rule.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import qmes.base.CONST;
import qmes.core.Model;
import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;
import qmes.word.storage.WordStorageHelper;

public class CascadeHelper {
	
	private String namespace = null;
	private Class clazz = null;
	private Model model = null;
	public CascadeHelper(Model model) {
		this.model = model;
	}
	
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
		if(namespace!=null) {
			wsh = new WordStorageHelper(model, namespace);
		}else {
			wsh = null;
		}
	}
	
	private JComboBox featurenames;
	private JComboBox groups;
	private JComboBox featurestates;
	
	WordStorageHelper wsh = null;
	
	public void setControls(JComboBox featurenames, JComboBox groups, JComboBox featurestates) {
		this.featurenames = featurenames;
		this.featurestates = featurestates;
		this.groups = groups;
		
		initValues();
		initListeners();
	}
	
	private void initValues() {
		
	}
	
	private void initListeners() {
		if(featurenames!=null)
		featurenames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(featurenames.getSelectedIndex()>0 && wsh!=null) {
					FeatureName fn = (FeatureName)featurenames.getSelectedItem();
					
					if(groups!=null) {
						groups.removeAllItems();
						groups.addItem("");
						List<String> gps = wsh.listGroupNames(fn);
						if(gps.size()>0) {
							for(int i=0;i<gps.size();i++) {
								groups.addItem(gps.get(i));
							}
						}
						groups.addItem(CONST.EXIST_GROUP);
						groups.addItem(CONST.NONE_GROUP);
					}
					
				}
			}
		});
		
		if(groups!=null)
		groups.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(groups.getSelectedIndex()>0 && wsh!=null && featurenames.getSelectedIndex()>0) {
					List<FeatureState> fss = null;
					String group = (String)groups.getSelectedItem();
					if(CONST.EXIST_GROUP.equals(group)) {
						fss = wsh.listExistState();
					}else if(CONST.NONE_GROUP.equals(group)) {
						FeatureName fn = (FeatureName)featurenames.getSelectedItem();
						fss = wsh.listFeatureStatesByBinding(fn);
					}else {
						fss = wsh.listFeatureStatesByGroup(group);
					}
					
					featurestates.removeAllItems();
					featurestates.addItem("");
					if(fss.size()>0) {
						for(int i=0;i<fss.size();i++) {
							featurestates.addItem(fss.get(i));
						}
					}
				}
			}
		});
		
		if(featurestates!=null)
		featurestates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(featurestates.getSelectedIndex()>0 && wsh!=null && clazz!=null && featurenames.getSelectedIndex()>0) {
					FeatureState fs = (FeatureState)featurestates.getSelectedItem();
					FeatureName fn = (FeatureName)featurenames.getSelectedItem();
					StringBuffer sb = new StringBuffer();
					sb.append(clazz.getSimpleName());
					sb.append("(name==\""+fn.getValue()+"\",");
					sb.append("state==\""+fs.getValue()+"\",time==0)");
					if(listeners.size()>0) {
						for(int i=0;i<listeners.size();i++) {
							listeners.get(i).actionPerformed(new ActionEvent(featurestates, 0, sb.toString()));
						}
					}
					
					Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable tText = new StringSelection(sb.toString());
					clip.setContents(tText, null);
				}
			}
		});
		
		
	}
	
	public void load() {
		featurenames.removeAllItems();
		featurenames.addItem("");
		
		List<FeatureName> fns = wsh.listFeatureNameWithClass(clazz.getName());
		for(int i=0;i<fns.size();i++) {
			featurenames.addItem(fns.get(i));
		}
	}
	
	private List<ActionListener> listeners = new ArrayList<ActionListener>();
	public void addStateActionListener(ActionListener l) {
		listeners.add(l);
	}

	

}
