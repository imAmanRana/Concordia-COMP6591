/**
 * 
 */
package ca.concordia.kbs.datalog.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ca.concordia.kbs.datalog.Codalog;
import ca.concordia.kbs.datalog.Main;
import ca.concordia.kbs.datalog.constants.Constants;
import ca.concordia.kbs.datalog.exception.CoDalogException;
import ca.concordia.kbs.datalog.syntax.Expression;
import ca.concordia.kbs.datalog.syntax.Rule;
import ca.concordia.kbs.datalog.utils.Utils;

public class NaiveEngine implements Engine {

	boolean printIntermediateResultsToFile;

	public NaiveEngine(boolean printIntermediateResultsToFile) {
		this.printIntermediateResultsToFile = printIntermediateResultsToFile;
	}

	@Override
	public Map<List<Expression>, Collection<Map<String, String>>> query(Codalog codalog,
			List<List<Expression>> queryList) {
		Map<List<Expression>, Collection<Map<String, String>>> map = new LinkedHashMap<>();
		for (int i = 0; i < queryList.size(); i++) {
			List<Expression> goals = queryList.get(i);
			List<Expression> orderedGoals = reorder(goals);
			Set<String> relevantPredicates = fetchRelevantPredicates(codalog, goals);

			Map<String, Set<Expression>> relevantFacts = new HashMap<>();
			// build relevant facts database
			for (String predicate : relevantPredicates) {
				Set<Expression> matchingExp = codalog.getEdb().getFacts(predicate);
				if (matchingExp != null)
					relevantFacts.put(predicate, matchingExp);
			}

			// match the Query goals with relevant Facts
			map.put(goals, matchGoals(orderedGoals, relevantFacts, null));
		}
		return map;
	}

	@Override
	public void generateNewFacts(Codalog codalog) throws CoDalogException {

		List<Collection<Rule>> stratifiedRules = stratifyRules(new HashSet<Rule>(codalog.getIdb().getIdb()));
		long initialSize = 0;
		long newSize = 0;
		//long startTime = System.currentTimeMillis();
		do {
			initialSize = codalog.getEdb().getEdbSize();
			for (int k = 0; k < stratifiedRules.size(); k++) {
				Collection<Rule> stratifiedRule = stratifiedRules.get(k);
				if (!stratifiedRule.isEmpty()) {

					Set<Expression> expt = naiveSearch(codalog.getEdb().getEdbMap(), stratifiedRule);

					if (printIntermediateResultsToFile && expt != null) {
						List<String> newDerivedFacts = new ArrayList<>();
						newDerivedFacts.add("*** New Facts Generated in this Iteration ***");
						newDerivedFacts.addAll(expt.stream().map(idb -> idb.toString()).collect(Collectors.toList()));
						newDerivedFacts.add("\n");
						try {
							Utils.writeToFile(newDerivedFacts,
									Main.getInstance().getProp().getProperty(Constants.OUTPUT_INTERMEDIATE_RESULT_FILE),
									true);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					codalog.getEdb().addAllFacts(expt);
				}
			}
			newSize = codalog.getEdb().getEdbSize();
		} while (initialSize != newSize);
		//long endTime = System.currentTimeMillis();
		//System.out.println("Time Taken : " + (endTime - startTime));
	}

	private Set<Expression> naiveSearch(Map<String, Set<Expression>> facts, Collection<Rule> stratifiedRule) {
		Set<Expression> newFacts = new HashSet<>();
		if (stratifiedRule == null || stratifiedRule.isEmpty()) {
			return null;
		}

		for (Rule rule : stratifiedRule) {
			newFacts.addAll(matchRule(facts, rule));
		}

		return newFacts;
	}

	public Collection<? extends Expression> matchRule(Map<String, Set<Expression>> relevantFacts, final Rule rule) {
		Set<Expression> newFacts = new HashSet<>();
		if (rule.getBody().isEmpty()) {
			return newFacts;
		}

		Collection<Map<String, String>> answers = matchGoals(rule.getBody(), relevantFacts, null);

		Expression exp;
		for (Map<String, String> answer : answers) {
			exp = rule.getHead().substitute(answer);
			newFacts.add(exp);
		}

		return newFacts;
	}

}
