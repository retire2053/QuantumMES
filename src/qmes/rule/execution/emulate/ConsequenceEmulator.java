package qmes.rule.execution.emulate;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.model.HuskyObject;
import qmes.rule.def.DefRule;
import qmes.rule.storage.RuleStorage;

/**
 * 
 * .drl文件中的一个rule的RHS的consequence中的文本，本质上是一段不完整的java代码
 * 本来的办法，就是将该java文件拼接完整，并且动态编译和执行，并将结束输出
 * 这样就可以在一个rule的LHS并不满足的情况下，得到其输出的结果
 * 
 * @author retire2053
 */

public class ConsequenceEmulator {

	private static final Logger log = LoggerFactory.getLogger(ConsequenceEmulator.class);

	private String tempbase = null;
	
	JavaCompiler jc = null;

	public ConsequenceEmulator(String tempbase) {
		this.tempbase = tempbase;
		jc = ToolProvider.getSystemJavaCompiler();
	}

	public List<HuskyObject> emulate(DefRule rule) {
		
		if(jc==null) {
			log.error("no java compiler defined in Emulator");
			return new ArrayList<HuskyObject>();
		}
		
		try {
			String deepdir = tempbase + java.io.File.separator + "tom" + java.io.File.separator + "rule"
					+ java.io.File.separator + "execution" + java.io.File.separator + "emulate"  ;
			String basedir = tempbase;
			cleanDir(basedir, deepdir);

			String javaSourceContent = generateJavaSource(rule);
			String javaSourcePath = tempbase + java.io.File.separator + "Emulator2.java";
			String javaClass1 = tempbase + java.io.File.separator + "Emulator2.class";
			String javaClass2 = deepdir + java.io.File.separator + "Emulator2.class";

			if (compile(javaSourcePath, javaSourceContent) == 0) {
				moveToPath(javaClass1, javaClass2);
				return executeMethod(tempbase, rule.getName());
			} else {
				log.error("error compiling generated java file {}", javaSourcePath);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	private String getPart1() {
		StringBuffer sb = new StringBuffer();
		sb.append("package qmes.rule.execution.emulate;\n");
		sb.append("import java.util.ArrayList;\n");
		sb.append("import java.util.List;\n");
		sb.append("import qmes.model.HuskyObject;\n");
		sb.append("import qmes.rule.execution.emulate.UtilityEmulator;\n");
		sb.append("import qmes.model.*;\n");
		sb.append("public class Emulator2 {\n");
		sb.append("\tUtilityEmulator Utility = new UtilityEmulator();\n");
		sb.append("\tpublic void execute(String drools) {\n");

		return sb.toString();

	}

	private String getPart2() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t}\n");
		sb.append("\tprivate List<HuskyObject> list = new ArrayList<HuskyObject>();\n");
		sb.append("\tprivate void insert(HuskyObject o) { list.add(o);}\n");
		sb.append("\tpublic List<HuskyObject> getObject(){ return list;}\n");
		sb.append("}\n");
		return sb.toString();
	}

	private String generateJavaSource(DefRule rule) {
		StringBuffer sb = new StringBuffer();
		sb.append(getPart1());
		sb.append((String) rule.getConsequence());
		sb.append(getPart2());
		return sb.toString();
	}

	private void cleanDir(String basedir, String deepdir) {

		log.info("clean directory files in {}", tempbase);

		try {

			File dir = new File(deepdir);
			if (!dir.exists())
				dir.mkdirs();

			File tempbasedir = new File(basedir);
			File[] fs = tempbasedir.listFiles();
			if (fs.length > 0) {
				for (int i = 0; i < fs.length; i++) {
					if (fs[i].exists() && fs[i].isFile()) {
						fs[i].delete();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}

	private int compile(String javaSourcePath, String javaSourceContent) {

		log.info("start to compile generated java source file {}", javaSourcePath);

		try {
			FileOutputStream fos = new FileOutputStream(new File(javaSourcePath));
			fos.write(javaSourceContent.getBytes());
			fos.close();

			if(jc!=null) {
				int result = jc.run(null, null, null, javaSourcePath);
				return result;
			}else {
				log.error("JAVA COMPILER is null, please change another compiler implementation");
				return -1;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
		return -1;
	}

	private void moveToPath(String javaClass1, String javaClass2) {

		log.info("copy file from {} to {}", javaClass1, javaClass2);

		try {
			File src = new File(javaClass1);
			File dst = new File(javaClass2);

			Files.copy(src.toPath(), dst.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}

	}

	private List<HuskyObject> executeMethod(String basedir, String rulename) {

		try {
			log.info("dynamically load class for qmes.rule.execution.emulate.Emulator2");

			URLClassLoader loader1 = new URLClassLoader(new URL[] { new File(basedir).toURL() },
					ConsequenceEmulator.class.getClassLoader());
			Class emulator2_class = loader1.loadClass("qmes.rule.execution.emulate.Emulator2");

			log.info("dynamically execute method for qmes.rule.execution.emulate.Emulator2");
			
			Method executeMethod = emulator2_class.getMethod("execute", new Class[] { String.class });
			Method getObjectMethod = emulator2_class.getMethod("getObject", new Class[] {});

			Object emulator2 = emulator2_class.newInstance();
			executeMethod.invoke(emulator2, new Object[] { rulename });
			List<HuskyObject> result = (List<HuskyObject>) getObjectMethod.invoke(emulator2, new Object[] {});

			if (result.size() > 0) {
				log.info("{} HuskyObject(s) are emulated from rule \"{}\"", result.size(),  rulename);
				for (int i = 0; i < result.size(); i++) {
					log.info("No.[{}] {}", (i + 1), result.get(i));
				}
				log.info("rule \"{}\" is successfully dynamically emulated", rulename);

			} else {
				log.info("no HuskyObject is emulated from rule \"{}\"", rulename);
			}
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
		return null;
	}

}
