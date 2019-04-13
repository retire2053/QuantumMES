package qmes.rule.execution.emulate;

import java.util.ArrayList;
import java.util.List;

import qmes.model.HuskyObject;
import qmes.rule.execution.emulate.UtilityEmulator;

/**
 * Emulator是一个模板类，其内容被拆解，在ConsequenceEmulator中被使用到
 * 本类只是用在文本的检查上，而并不被直接调用
 * @author retire2053
 *
 */

public class Emulator {
	
	UtilityEmulator Utility = new UtilityEmulator();
	
	private void run(String drools) {
		

		
	}
	
	private List<HuskyObject> list = new ArrayList<HuskyObject>();
	
	private void insert(HuskyObject o) { list.add(o);}
	public List<HuskyObject> getObject(){ return list;}
}
