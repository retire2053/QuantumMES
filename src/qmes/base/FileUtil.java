package qmes.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileUtil {
	
	public static List<String> loadLines(File path) throws Exception {
		List<String> lines = new ArrayList<String>();
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String s;
		while ((s = br.readLine()) != null) {
			lines.add(s);
		}
		fr.close();
		return lines;
	}

	public static String loadFileContent(File path) throws Exception {

		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String s;
		StringBuffer sb = new StringBuffer();
		while ((s = br.readLine()) != null) {
			sb.append(s+"\n");
		}
		fr.close();
		return sb.toString();
	}
	
	public static void saveFileContent(File path, String content)throws Exception{
		
		FileWriter fw = new FileWriter(path);
		fw.write(content);
		fw.close();
	}
	
	
	public static File saveAs(JDialog parent) {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(false);
		jfc.showSaveDialog(parent);
		File file = jfc.getSelectedFile();
		return file;
	}
	
	public static File saveAs(JDialog parent, String pattern, String desc) {
		JFileChooser jfc = new JFileChooser();
		FileFilter ff = new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName();
				return f.isDirectory() || name.toLowerCase().endsWith(pattern);
			}
			public String getDescription() {
				return desc;
			}
		};
		jfc.addChoosableFileFilter(ff);
		jfc.setFileFilter(ff);
		jfc.setMultiSelectionEnabled(false);
		jfc.showSaveDialog(parent);
		File file = jfc.getSelectedFile();
		return file;
	}
	
	public static File open(JDialog parent) {
		JFileChooser fc = new JFileChooser();
		int ret= fc.showOpenDialog(parent);
		if(ret==fc.APPROVE_OPTION) {
			File path = fc.getSelectedFile();
			return path;
		}
		return null;
	}
	
	public static File open(JDialog parent, String pattern, String desc) {
		JFileChooser fc = new JFileChooser();
		FileFilter ff = new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName();
				return f.isDirectory() || name.toLowerCase().endsWith(pattern);
			}
			public String getDescription() {
				return desc;
			}
		};
		fc.addChoosableFileFilter(ff);
		fc.setFileFilter(ff);
		int ret= fc.showOpenDialog(parent);
		if(ret==fc.APPROVE_OPTION) {
			File path = fc.getSelectedFile();
			return path;
		}
		return null;
	}
	
	public static File copyResourceToTemp(InputStream is, String name) throws Exception{
		
		String tempDir = System.getProperty("java.io.tmpdir");
		File file = new File(tempDir, name);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] bs = new byte[4096];
		while (true) {
			int count = is.read(bs);
			if (count >= 0) {
				fos.write(bs, 0, count);
			} else
				break;
		}
		fos.close();
		return file;
	}
	
	
}
