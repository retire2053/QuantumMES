package qmes.rule.def.lhs;

import java.util.Comparator;
import java.util.List;

import qmes.model.HuskyObject;

public class LineMatchLevel {
	
	public static int LEVEL_NO_MATCH = 0;
	public static  int LEVEL_MATCH_CLASS = 1;
	public static  int LEVEL_MATCH_CLASS_FN = 2;
	public static  int LEVEL_MATCH_CLASS_FN_FS = 3;
	public static  int LEVEL_MATCH_CLASS_FN_FS_TIME = 4;

	public LineMatchLevel() {
		
	}
	
	private int level;
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	private HuskyObject input;

	public HuskyObject getInput() {
		return input;
	}

	public void setInput(HuskyObject input) {
		this.input = input;
	}
	
	private static Comparator<LineMatchLevel> COMPARATOR = new Comparator<LineMatchLevel>() {
		public int compare(LineMatchLevel o1, LineMatchLevel o2) {
			return o1.getLevel() - o2.getLevel();
		}
		
	};
	
	public static void sort(List<LineMatchLevel> matchresult) {
		matchresult.sort(COMPARATOR);
	}

	
}
