package husky.service;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.spi.KnowledgeHelper;

public class Utility {

	public static List<String> rules = new ArrayList<String>();

	public static void helper(final KnowledgeHelper drools, final String message) {

		System.out.println(message);
		rules.add(drools.getRule().getName());

	}

	public static void helper(final KnowledgeHelper drools) {
		rules.add(drools.getRule().getName());
	}
}