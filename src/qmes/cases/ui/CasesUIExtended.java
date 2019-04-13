package qmes.cases.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.core.Model;
import qmes.rule.ui.CascadeHelper;
import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;

public class CasesUIExtended {
	
	private static final Logger log = LoggerFactory.getLogger(CasesUIExtended.class);

	private Model model = null;
	private JDialog dialog = null;
	public CasesUIExtended(JDialog dialog, Model model) {
		this.dialog = dialog;
		this.model = model;
	}
	
	protected final JComboBox[] featurename = new JComboBox[CONST.EXTENDED];
	protected final JComboBox[] featuregroup = new JComboBox[CONST.EXTENDED];
	protected final JComboBox[] featurevalue = new JComboBox[CONST.EXTENDED];
	protected final JComboBox[] time = new JComboBox[CONST.EXTENDED];
	protected final JComboBox[] timefrom = new JComboBox[CONST.EXTENDED];
	protected final JComboBox[] timeto = new JComboBox[CONST.EXTENDED];
	protected final JIndexButton[] button = new JIndexButton[CONST.EXTENDED];
	
	protected CascadeHelper cascadeHelper[] = new CascadeHelper[CONST.EXTENDED];
	
	//必须从1开始，因为要越过0=IndicatorV
	private int START_INDEX = 1;
	
	protected void createExtendedUI(JPanel panel) {
		
		for(int i=START_INDEX;i<CONST.EXTENDED;i++) {
			JPanel subpanel = new JPanel();
			subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.X_AXIS));
			subpanel.add(BASEUI.alabel(CONST.classes[i].getSimpleName()));
			
			featurename[i] = new JComboBox();
			featuregroup[i] = new JComboBox();
			featurevalue[i] = new JComboBox();
			
			subpanel.add(featurename[i]);
			subpanel.add(BASEUI.alabel("组"));
			subpanel.add(featuregroup[i]);
			subpanel.add(BASEUI.alabel("值"));
			subpanel.add(featurevalue[i]);
			if(CONST.singletime[i]) {
				
				time[i] = new JComboBox();
				
				subpanel.add(BASEUI.alabel("时间"));
				subpanel.add(time[i]);
				BASEUI.initCombo(BASEUI.getTreatTimes(), time[i]);
			}else {
				
				timefrom[i] = new JComboBox();
				timeto[i] = new JComboBox();
				
				subpanel.add(BASEUI.alabel("从"));
				subpanel.add(timefrom[i]);
				subpanel.add(BASEUI.alabel("到"));
				subpanel.add(timeto[i]);
				
				BASEUI.initCombo(BASEUI.getTreatTimes(), timefrom[i]);
				BASEUI.initCombo(BASEUI.getTreatTimes(), timeto[i]);
			}
			button[i] = new JIndexButton("创建"+CONST.meanings[i], i);
			subpanel.add(button[i]);
			
			cascadeHelper[i] = new CascadeHelper(model);
			cascadeHelper[i].setClazz(CONST.classes[i]);
			cascadeHelper[i].setNamespace("common");
			cascadeHelper[i].setControls(featurename[i], featuregroup[i], featurevalue[i]);
			cascadeHelper[i].load();
			
			panel.add(subpanel);
			
			button[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JIndexButton button = (JIndexButton) e.getSource();
					int i = button.getIndex();
					log.info("\"add [{}]\" button is clicked", CONST.meanings[i]);

					int index = featurename[i].getSelectedIndex();
					int index3 = featurevalue[i].getSelectedIndex();
					if (index > 0 && index3 > 0) {
						try {
							Object object = CONST.classes[i].newInstance();
							Method setName = CONST.classes[i].getMethod("setName", new Class[] { String.class });
							Method setState = CONST.classes[i].getMethod("setState", new Class[] { String.class });
							
							FeatureName fn = (FeatureName)featurename[i].getSelectedItem();
							FeatureState fs = (FeatureState)featurevalue[i].getSelectedItem();
							setName.invoke(object, new Object[] { fn.getValue() });
							setState.invoke(object, new Object[] { fs.getValue() });

							if (CONST.singletime[i]) {
								Method setTime = CONST.classes[i].getMethod("setTime", new Class[] { double.class });
								setTime.invoke(object, new Object[] { time[i].getSelectedItem() });
							} else {
								Method setTimefrom = CONST.classes[i].getMethod("setStime", new Class[] { double.class });
								setTimefrom.invoke(object, new Object[] { timefrom[i].getSelectedItem() });
								Method setTimeto = CONST.classes[i].getMethod("setEtime", new Class[] { double.class });
								setTimeto.invoke(object, new Object[] { timeto[i].getSelectedItem() });
							}
							
							if (listeners.size() > 0) {
								for (int k = 0; k < listeners.size(); k++) {
									ActionEvent event = new ActionEvent(object, 0, "");
									listeners.get(k).actionPerformed(event);
								}

							}
						} catch (Exception ex) {
							ex.printStackTrace();
							log.error(ex.getMessage());
						}
					}

				}
			});
		}
		
	}
	
	protected void reload() {
		if (cascadeHelper != null) {
			for (int i = START_INDEX; i < cascadeHelper.length; i++) {
				cascadeHelper[i].load();
			}

		}
	}
	
	private List<ActionListener> listeners = new ArrayList<ActionListener>();
	protected void addActionListener(ActionListener al) {
		listeners.add(al);
	}
	
	
}

class JIndexButton extends JButton{
	public JIndexButton(String title, int index) {
		super(title);
		setIndex(index);
	}
	
	private int index = -1;
	public int getIndex() {return index;}
	public void setIndex(int index) {
		this.index = index;
	}
}
