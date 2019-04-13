package qmes.base;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BASEUI {
	
	private static final Logger log = LoggerFactory.getLogger(BASEUI.class);
	
	public static void initCombo(Object[] values, JComboBox box) {
		box.removeAllItems();
		box.addItem("");
		for(int k=0 ; k<values.length ; k++) box.addItem(values[k]);
	}
	
	public static void setCombo(Object text, JComboBox combo) {
		if(text==null)combo.setSelectedIndex(0);
		else {
			boolean found =false;
			for(int i=0;i<combo.getItemCount();i++) {
				if(combo.getItemAt(i).equals(text)) {
					combo.setSelectedIndex(i);
					found = true;
				}
			}
			if(found==false)combo.setSelectedIndex(0);
		}
	}
	
	public static DefaultTableModel cleanTable(JTable table) {
		DefaultTableModel dtm = (DefaultTableModel)table.getModel();
		for(int i=dtm.getRowCount()-1;i>=0;i--) {
			dtm.removeRow(i);
		}
		return dtm;
	}
	
	public static JLabel alabel(String text) {
		JLabel label = new JLabel();
		label.setText(text);
		return label;
	}
	
	public static String nbsp(int count) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<count;i++)sb.append("&nbsp;");
		return sb.toString();
	}
	
	public static Object[] getTreatTimes() {
		return new Object[] {
				0d,-1d,-2d,-3d,-4d,-5d,-6d,-7d,-8d,-9d,-10d,-11d,-12d,
				-13d,-14d,-15d,-16d,-17d,-18d,-19d,-20d,-21d,-22d,-23d,-24d,
				-36d,-48d,-60d,-72d
		};
	}
	
	public static String[] readProperty(Class clazz) {
		Field[] fs = clazz.getDeclaredFields();
		String[] properties = new String[fs.length];
		for(int i=0;i<fs.length;i++) {
			properties[i] = fs[i].getName();
		}
		return properties;
	}
	
	public static String stringArrayToString(String[] list) {
		if(list!=null && list.length>0) {
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<list.length;i++) {
				sb.append(list[i]+" ");
			}
			return sb.toString();
		}
		return "";
	}
	
	public static String stringArrayToString(List<String> list) {
		if(list!=null && list.size()>0) {
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<list.size();i++) {
				sb.append(list.get(i)+" ");
			}
			return sb.toString();
		}
		return "";
	}
	
	
	public static String[] stringToStringArray(String string) {
		if(string!=null) {
			String[] array = string.split(" ");
			return array;
		}
		return new String[0];
	}
	
	public static void openFileInExplorer(JFrame frame, String path) {
		Properties props = System.getProperties();
		String osName = props.getProperty("os.name");
		try {
			if (osName.toUpperCase().startsWith("WINDOWS")) {
				String cmd = "explorer  " + path;
				Runtime.getRuntime().exec(cmd);
			} else if (osName.toUpperCase().startsWith("MAC")) {
				String cmd = "/usr/bin/open " + path;
				Runtime.getRuntime().exec(cmd);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			JOptionPane.showMessageDialog(frame, "无法打开数据目录" + path);
		}

	}
	
	public static void promptError(JFrame frame) {
		JOptionPane.showMessageDialog(frame, CONST.ERR_PROMPT);
	}
}
