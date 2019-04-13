package qmes.core;

import java.util.Properties;

import javax.swing.UIManager;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.CONST;

public class QMES {
	
	private static final Logger log = LoggerFactory.getLogger(QMES.class);

	public static void main(String[] args) throws Exception {

		for (int i = 0; i < args.length; i++) {
			log.info("args[{}]={}", i, args[i]);
		}

		if (args.length < 1) {
			log.error("Please specify first arg with base directory for storage");
			System.exit(0);
		}else {
			String basedir = args[0];
			log.info("take \"{}\" as base directory", basedir);
			
			java.io.File basedirfile = new java.io.File(basedir);
			if(!basedirfile.exists() || !basedirfile.isDirectory()) {
				log.info("Please specify a valid directory for storage");
				System.exit(0);
			}else {
				String[] subdirlist = new String[] {
						CONST.RULE_ANNOTATION_SUB_PATH, CONST.CASE_SUB_PATH, CONST.CONSEQUENCE_EMULATOR_TEMP_SUB_PATH,
						CONST.INDICATOR_DEF_SUB_PATH, CONST.NAMESPACE_STORAGE_SUB_PATH, CONST.RULE_FILE_SUB_PATH,
						CONST.RULE_SEARCH_TEMP_SUB_PATH, CONST.RULE_TEMP_SUB_PATH,CONST.NLP_SUB_PATH,
						CONST.SEARCH_SINGLEWORD_SUB_PATH, CONST.SEARCH_TEMP_SUB_PATH, CONST.WORD_STORAGE_SUB_PATH
				};
				for(int i=0;i<subdirlist.length;i++) {
					new java.io.File(basedirfile, subdirlist[i]).mkdir();
				}
			}
			
			PropertyConfigurator.configure(QMES.class.getResource("/log4j.properties"));

			Properties props = System.getProperties();
			String osName = props.getProperty("os.name");
			String osArch = props.getProperty("os.arch");
			String osVersion = props.getProperty("os.version");
			log.info("os name={}, os arch={}, os.version={}", osName, osArch, osVersion);

			if (osName.toUpperCase().startsWith("WINDOWS")) {
				String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
				UIManager.setLookAndFeel(lookAndFeel);
			}

			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						
						log.info("start to run application");
						MainFrame s2 = new MainFrame(new Model(basedir));
						s2.createAndShowGUI();
					} catch (Exception ex) {
						ex.printStackTrace();
						log.error(ex.getMessage());
					}
				}
			});

		}
	}
}
