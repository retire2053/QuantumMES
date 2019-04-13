package qmes.base;

import java.awt.Font;

import org.apache.lucene.document.Field;

import qmes.model.Deed;
import qmes.model.Demand;
import qmes.model.Diagnose;
import qmes.model.Expect;
import qmes.model.IndicatorS;
import qmes.model.IndicatorV;
import qmes.model.Monitoring;
import qmes.model.NeedMoreIndicator;
import qmes.model.NeedMoreInformation;
import qmes.model.Patient;
import qmes.model.Question;
import qmes.model.Symptom;
import qmes.model.TreatCandidate;
import qmes.model.TreatHistory;
import qmes.model.TreatOngoing;

public interface CONST {
	
	public static String NAMESPACE = "common";
	
	public static String INDICATOR_DEF_SUB_PATH = "indicatordef";

	public static String RULE_ANNOTATION_SUB_PATH = "annotation";
	
	public static String RULE_FILE_SUB_PATH = "rule";
	
	public static String RULE_TEMP_SUB_PATH = "ruletemp";
	
	public static String WORD_STORAGE_SUB_PATH = "word";
	
	public static String NAMESPACE_STORAGE_SUB_PATH = "namespace";
	
	public static String CONSEQUENCE_EMULATOR_TEMP_SUB_PATH = "emulator";
	
	public static String CASE_SUB_PATH = "case";
	
	public static String SEARCH_TEMP_SUB_PATH = "searchtemp";
	
	public static String SEARCH_SINGLEWORD_SUB_PATH = "searchsingleword";
	
	public static String RULE_SEARCH_TEMP_SUB_PATH = "rulesearchtemp";
	
	public static String NLP_SUB_PATH = "scope";
	
	public static Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 11);
	public static Font DEFAULT_FONT_BIG = new Font(Font.MONOSPACED, Font.PLAIN, 13);
	
	
	public static String TYPE_FEATURE_NAMES = "featurename";
	public static String TYPE_FEATURE_STATES = "featurestate";
	
	public static String RULE_NAME_DELIMITER = "-";
	
	//IndicatorV必须是第一个，CasesUI中有代码依赖于这个顺序
	public static Class[] classes = new Class[] {
			IndicatorV.class,	//定量指标
			IndicatorS.class,	//定性指标
			Diagnose.class,		//诊断
			Patient.class,		//患者人口信息

			TreatHistory.class,	//诊疗历史
			Monitoring.class,	//随访监控
			TreatCandidate.class,	//诊疗候选方案
			TreatOngoing.class,		//正在治疗中的诊疗方案

			Deed.class,		//患者行为
			Demand.class,	//患者诉求
			Expect.class,	//预后
			
			NeedMoreIndicator.class,		//需要补充检查
			NeedMoreInformation.class,	//需要补充信息
			Symptom.class,				//症状
			Question.class,				//患者问题
		};
	public static int EXTENDED = classes.length;
	
	public static boolean singletime[] = new boolean[] {
			true, true, true, true, 
			false, false, false, false,
			true, true, true,
			true, true, true, true,
	};
	
	public static int order[] = new int[] {
			4,3,2,1,
			5,6,7,8,
			9,10,11,
			12,13,14,15
	};
	
	public static String[] meanings  = new String[] {
			"定量指标", "定性指标","诊断","人口信息",
			"治疗历史", "监测复查", "备选治疗","进行中治疗",
			"行为特征","特殊需求","预测",
			"补充检查","补充信息","症状","患者提问"
	};
	
	public static String[] allProperties = new String[] {
			"state",
			"unit",
			"value",
			"spec",
	};
	
	public static Class[] timespanClasses = new Class[] {
			TreatHistory.class, Monitoring.class, TreatCandidate.class, TreatOngoing.class
	};
	
	//临时解决办法，Unit需要可以配置出来
	public static String[] units = new String[] {
			"U", "cps", "IU", "ng", "umol"};
	
	
	public static String SEARCH_CONTENT = "content";
	public static String SEARCH_FN_OR_FS = "fn_or_fs";
	public static String SEARCH_FEATURE_NAME = "featurename";
	public static String SEARCH_FEATURE_STATE = "featurestate";
	public static String SEARCH_FEATURE_NAME_SYNONYM = "featurenamesynonym";
	public static String SEARCH_FEATURE_STATE_SYNONYM = "featurestatesynonym";
	public static String SEARCH_GROUP = "group";
	public static String SEARCH_CLASS = "clazz";
	public static String SEARCH_NAMESPACE = "namespace";
	
	
	public static String NONE_GROUP = "[不分组]";
	public static String EXIST_GROUP = "[存在/不存在]";

	public static String EXIST_GROUP_VALUE_EXIST = "存在";
	public static String EXIST_GROUP_VALUE_NON_EXIST = "不存在";
	
	public static String ERR_PROMPT = "在执行中遇到问题，请查看错误日志";
	
	public static String MATCH_TYPE_TAG = "标签匹配";
	public static String MATCH_TYPE_TRIGGER = "触发匹配";
	public static String MATCH_TYPE_PREMATCH = "模糊匹配";
	
	
}
