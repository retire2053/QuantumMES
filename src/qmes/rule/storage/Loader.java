package qmes.rule.storage;

import static org.drools.compiler.compiler.DRLFactory.buildParser;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.drools.compiler.lang.DRL6Parser;
import org.drools.compiler.lang.DRLParser;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.rule.def.DefAttribute;
import qmes.rule.def.DefConstraint;
import qmes.rule.def.DefExists;
import qmes.rule.def.DefGlobal;
import qmes.rule.def.DefImport;
import qmes.rule.def.DefLhs;
import qmes.rule.def.DefNot;
import qmes.rule.def.DefObject;
import qmes.rule.def.DefOr;
import qmes.rule.def.DefPackage;
import qmes.rule.def.DefRule;

public class Loader {
	
	private static final Logger log = LoggerFactory.getLogger(Loader.class);

	private DRLParser parser;

	public DefPackage generatePackage(String path) throws Exception {
		
		log.info("load .drl packages from path {}", path);
		
		final PackageDescr pkg = (PackageDescr) parseResource("compilationUnit", path);

		DefPackage dp = new DefPackage();

		dp.setName(pkg.getName());
		List<ImportDescr> ids = pkg.getImports();
		for (int i = 0; i < ids.size(); i++) {
			dp.addImport(new DefImport(ids.get(i).getTarget()));
		}

		List<GlobalDescr> gd = pkg.getGlobals();
		for (int i = 0; i < gd.size(); i++) {
			dp.addGlobal(new DefGlobal(gd.get(i).getType(), gd.get(i).getIdentifier()));
		}

		List<RuleDescr> rules = pkg.getRules();

		for (int i = 0; i < rules.size(); i++) {
			
			RuleDescr rule = rules.get(i);
			DefRule dr = new DefRule();
			
			log.info("loading rule {} from path {}",rule.getName(), path);

			dp.addRule(dr);
			dr.setParent(dp);
			
			Iterator<String> itr = rule.getAttributes().keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				AttributeDescr ad = rule.getAttributes().get(key);
				DefAttribute da = new DefAttribute(ad.getName(), ad.getValue(), ad.getType());
				dr.getAttributes().put(key, da);
			}

			dr.setName(rule.getName());
			dr.setSalience(rule.getSalience());
			dr.setConsequence((String) rule.getConsequence());

			DefLhs lhs = new DefLhs();
			dr.setLhs(lhs);
			AndDescr and = rule.getLhs();

			List<BaseDescr> patterns = and.getDescrs();
			for (int k = 0; k < patterns.size(); k++) {

				if (patterns.get(k) instanceof PatternDescr) {
					lhs.addObject(loadPattern(patterns.get(k)));
				} else if (patterns.get(k) instanceof NotDescr) {
					lhs.addObject(loadNot(patterns.get(k)));
				} else if (patterns.get(k) instanceof ExistsDescr) {
					lhs.addObject(loadExists(patterns.get(k)));
				} else if(patterns.get(k) instanceof OrDescr) {
					lhs.addObject(loadOr(patterns.get(k)));
				}else{
					
					log.error("Not implemented yet, while parse object {}", patterns.get(k).getClass().getSimpleName());
					throw new Exception("Not implemented yet");
				}
			}
			
			System.out.println(dr);
		}
		return dp;

	}

	private DefExists loadExists(Object input) {
		DefExists defe = new DefExists();

		ExistsDescr ed = (ExistsDescr) input;
		BaseDescr bd = (BaseDescr) ed.getDescrs().get(0);
		if (bd instanceof PatternDescr) {
			DefObject defo = loadPattern(bd);
			defe.setInner(defo);
		} else {
			log.error("not catch type {} while loadding ExistsDescr" ,bd.getClass().getName());
		}
		return defe;
	}
	
	private DefOr loadOr(Object input) {
		DefOr defor = new DefOr();
		OrDescr od = (OrDescr) input;
		List<BaseDescr> dbases = od.getDescrs();
		for (int i = 0; i < dbases.size(); i++) {
			BaseDescr bd = (BaseDescr) dbases.get(i);
			if (bd instanceof PatternDescr) {
				defor.getObjects().add(loadPattern(bd));
			} else if (bd instanceof NotDescr) {
				defor.getObjects().add(loadNot(bd));
			} else if (bd instanceof ExistsDescr) {
				defor.getObjects().add(loadExists(bd));
			} else if (bd instanceof OrDescr) {
				defor.getObjects().add(loadOr(bd));
			} else {
				log.error("Not implemented yet, while parse object {}", input);
			}
		}
		return defor;

	}

	private DefNot loadNot(Object input) {
		DefNot defn = new DefNot();

		NotDescr note = (NotDescr) input;
		BaseDescr bd = (BaseDescr) note.getDescrs().get(0);
		if (bd instanceof ExistsDescr) {
			DefExists defe = loadExists(bd);
			defn.setInner(defe);
		} else if (bd instanceof PatternDescr) {
			DefObject defo = loadPattern(bd);
			defn.setInner(defo);
		} else {
			log.error("not catch type {} while loadding NotDescr", bd.getClass().getName());
		}

		return defn;
	}

	private DefObject loadPattern(Object input) {
		DefObject defo = new DefObject();
		PatternDescr element = (PatternDescr) input;

		defo.setObjectType(element.getObjectType());
		defo.setIdentifier(element.getIdentifier());

		List e = element.getBehaviors();
		for (int x = 0; x < e.size(); x++) {
			BehaviorDescr bd = (BehaviorDescr) e.get(x);
		}

		List constraints = element.getConstraint().getDescrs();
		for (int p = 0; p < constraints.size(); p++) {
			DefConstraint dc = new DefConstraint();
			ExprConstraintDescr ec = (ExprConstraintDescr) constraints.get(p);
			dc.setExpression(ec.getExpression());
			defo.addConstraint(dc);
		}
		return defo;

	}

	private Object parse(final String parserRuleName, final String text) throws Exception {
		return execParser(parserRuleName, new ANTLRStringStream(text));
	}

	private Object parseResource(final String parserRuleName, String path) throws Exception {

		final Reader reader = new FileReader(new File(path));
		final StringBuilder text = new StringBuilder();

		final char[] buf = new char[1024];
		int len = 0;
		while ((len = reader.read(buf)) >= 0) {
			text.append(buf, 0, len);
		}
		reader.close();
		return parse(parserRuleName, text.toString());
		
	}

	public Object execParser(String testRuleName, CharStream charStream) {
		try {
			createParser(charStream);

			Method ruleName = null;
			Object[] params = null;
			for (Method method : DRL6Parser.class.getMethods()) {
				if (method.getName().equals(testRuleName)) {
					ruleName = method;
					Class<?>[] parameterTypes = method.getParameterTypes();
					params = new Object[parameterTypes.length];
				}
			}

			Object ruleReturn = ruleName.invoke(parser, params);

			if (parser.hasErrors()) {
				log.error(parser.getErrorMessages().toString());
			}

			return ruleReturn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void createParser(CharStream charStream) {
		parser = buildParser(charStream, LanguageLevelOption.DRL6);
	}

}
