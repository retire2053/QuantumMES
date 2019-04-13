package qmes.base;

import java.io.InputStream;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReleaseNote {

	private static final Logger log = LoggerFactory.getLogger(ReleaseNote.class);

	public void showReleaseNote(JFrame parent) {

		try {
			log.info("launch release note");
			InputStream is = ReleaseNote.class.getResourceAsStream("/releasenote.txt");
			byte[] bs = new byte[4096];
			is.read(bs);
			is.close();

			String releasenote = new String(bs);

			InfoUI iui = new InfoUI();
			iui.createAndShowGUI(parent, releasenote, "关于Q-MES", InfoUI.TYPE_TEXT);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}

	public ReleaseNote() {

	}
}
