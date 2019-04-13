package qmes.base;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.WriterAppender;

public class LogUtil {
	
	private StringWriter error = null;
	private StringWriter info = null;
	
	private WriterAppender errorAppender = null;
	private WriterAppender infoAppender = null;

	public LogUtil() {

		error = new StringWriter();
		info = new StringWriter();

		PatternLayout pl = new PatternLayout();
		pl.setConversionPattern("%d%-5p[%c.%M][line:%L]-%m%n");
		

		errorAppender = new WriterAppender(pl, new PrintWriter(error));
		errorAppender.setEncoding("UTF-8");
		errorAppender.setThreshold(Priority.ERROR);
		
		Logger.getRootLogger().addAppender(errorAppender);
		
		infoAppender = new WriterAppender(pl, new PrintWriter(info));
		infoAppender.setEncoding("UTF-8");
		infoAppender.setThreshold(Priority.INFO);
		
		Logger.getRootLogger().addAppender(infoAppender);

	}

	public String getError() {
		return error.toString();
	}
	
	public String getInfo() {
		return info.toString();
	}
}
