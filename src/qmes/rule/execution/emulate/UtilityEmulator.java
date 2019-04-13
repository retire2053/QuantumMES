package qmes.rule.execution.emulate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本类模拟husky.service中的Utility特性，使得ConsequenceEmulator得以正常运行
 * @author retire2053
 *
 */

public class UtilityEmulator {
	
	private static final Logger log = LoggerFactory.getLogger(UtilityEmulator.class);
	
	public void helper(String drool) {
		log.info("emulate is being executed. input param=\"{}\"", drool);
		
	}
}
