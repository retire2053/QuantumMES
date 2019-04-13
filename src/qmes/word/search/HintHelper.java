package qmes.word.search;



import java.lang.reflect.Field;

import qmes.core.Model;
import qmes.model.IndicatorTS;

public class HintHelper {
	
	SearchIndicatorSHelper indicatorhelper = null;
	public HintHelper(Model model) {
		indicatorhelper = new SearchIndicatorSHelper(model);
	}

	public String getHintForLhs(String clazz, String featurename, String[] fss) {
		if (IndicatorTS.class.getSimpleName().equals(clazz)) {
			return indicatorhelper.createTSRHS(featurename, fss[0]);
		}else {
			StringBuffer sb = new StringBuffer();
			sb.append(clazz + "(name==\"" + featurename + "\", state");
			for(int i=0;i<fss.length;i++) {
				if(i>0)sb.append("||");
				sb.append("==\""+fss[i]+"\"");
			}
			try {
				Field[] fields = Class.forName("qmes.model."+clazz).getDeclaredFields();
				for(Field f:fields) {
					if(f.getName().equals("time") || f.getName().equals("stime") || f.getName().equals("etime")) {
						sb.append(","+f.getName().toString()+"==0.0");
					}
				}
			} catch (SecurityException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			sb.append(")\n");
			return sb.toString();
		}
	}
	
	public String getHintForRhs(String clazz, String featurename, String[] fss) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < fss.length; i++) {
			sb.append(getHintForRhs(clazz, featurename, fss[i]));
		}
		return sb.toString();
	}
	
	public String getHintForLhs(String clazz, String featurename, String featurestate) {
		if (IndicatorTS.class.getSimpleName().equals(clazz)) {
			return indicatorhelper.createTSLHS(featurename, featurestate);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(clazz + "(name==\"" + featurename + "\", state==\""+featurestate+"\"");
			
			try {
				Field[] fields = Class.forName("qmes.model."+clazz).getDeclaredFields();
				for(Field f:fields) {
					if(f.getName().equals("time") || f.getName().equals("stime") || f.getName().equals("etime")) {
						sb.append(","+f.getName().toString()+"==0.0");
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			sb.append(")\n");
			return sb.toString();
		}
	}

	public String getHintForRhs(String clazz, String featurename, String featurestate) {
		if (IndicatorTS.class.getSimpleName().equals(clazz)) {
			return indicatorhelper.createTSRHS(featurename, featurestate);
		} else {
			String suffix = String.valueOf((int) (Math.random() * 100));
			return clazz + " a" + suffix + " = new " + clazz + "(); a" + suffix + ".setName(\"" + featurename + "\"); a"
					+ suffix + ".setState(\"" + featurestate + "\"); insert(a" + suffix + ");";
		}
	}
	
}
