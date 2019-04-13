package qmes.rule.execution.result;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StackedMatchResult {
	
	private static final Logger log = LoggerFactory.getLogger(StackedMatchResult.class);

	private List<MatchResult> tagMatch = new ArrayList<MatchResult>();
	private List<MatchResult> triggered = new ArrayList<MatchResult>();
	private List<MatchResult> prematch = new ArrayList<MatchResult>();

	public static int TYPE_TAG_MATCH = 1;
	public static int TYPE_TRIGGERED = 2;
	public static int TYPE_PREMATCH = 3;
	
	private boolean containsError = false;
	
	public StackedMatchResult() {
		
		log.info("start to generate StackedMatchResult which is for TreeTable presentation");
		
		clear();
	}
	
	public boolean isContainsError() { return containsError;}
	public void setContainsError(boolean b) { this.containsError = b;}

	public String getDesc(int type) {
		if (type == TYPE_TAG_MATCH)
			return "标签符合的规则";
		else if (type == TYPE_TRIGGERED)
			return "完全匹配的规则";
		else if (type == TYPE_PREMATCH)
			return "预匹配规则";
		else
			return "";
	}

	public List<MatchResult> getTagMatch() {
		return tagMatch;
	}

	public List<MatchResult> getTriggered() {
		return triggered;
	}

	public List<MatchResult> getPrematch() {
		return prematch;
	}

	public void clear() {
		tagMatch.clear();
		triggered.clear();
		prematch.clear();
	}

	public void addMatchResult(List<MatchResult> mrs) {
		if (mrs != null && mrs.size() > 0) {
			for (int i = 0; i < mrs.size(); i++) {
				MatchResult mr = mrs.get(i);

				if (mr == null || mr.getRule().getLhs().getObjectList().size() == 0)
					continue;
				if (mr.getRule().getLhs() == null || mr.getRule().getLhs().getObjectList().size() == 0)
					continue;

				if (mr.isMatched()) {
					triggered.add(mr);
				} else {
					//TODO add tag match
					prematch.add(mr);
				}
			}
		}
	}

}
