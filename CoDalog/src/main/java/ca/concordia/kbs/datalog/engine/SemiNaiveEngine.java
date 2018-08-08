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

/**
 * @author AmanRana
 *
 */
public class SemiNaiveEngine implements Engine {

	boolean printIntermediateResultsToFile;

	public SemiNaiveEngine(boolean printIntermediateResultsToFile) {
		this.printIntermediateResultsToFile = printIntermediateResultsToFile;
	}

	@Override
	public Map<List<Expression>, Collection<Map<String, String>>> query(Codalog codalog,
			List<List<Expression>> queryList) throws CoDalogException {
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
			map.put(goals, matchGoals(orderedGoals, relevantFacts, null));
		}
		return map;

	}

	@Override
	public void generateNewFacts(Codalog codalog) throws CoDalogException {
		//long startTime = System.currentTimeMillis();
		buildDatabase(codalog.getEdb().getEdbMap(), new HashSet<Rule>(codalog.getIdb().getIdb()));
		//long endTime = System.currentTimeMillis();
		//System.out.println((endTime - startTime));
	}

	private void buildDatabase(Map<String, Set<Expression>> edbFacts, Set<Rule> relevantRules) throws CoDalogException {
		List<Collection<Rule>> stratifiedRules = stratifyRules(relevantRules);
		for (int k = 0; k < stratifiedRules.size(); k++) {
			Collection<Rule> stratifiedRule = stratifiedRules.get(k);
			semiNaiveSearch(edbFacts, stratifiedRule);
		}
	}

	private Map<String, Set<Expression>> semiNaiveSearch(Map<String, Set<Expression>> edbFacts,
			Collection<Rule> stratifiedRule) {

		Map<String, Set<Expression>> edbFactsCopy = new HashMap<>();
		Map<String, Set<Expression>> edbFactsPlusNewFacts = new HashMap<>();

		if (stratifiedRule == null || stratifiedRule.isEmpty())
			return edbFacts;

		Utils.addAllFacts(edbFactsCopy, edbFacts);
		Utils.addAllFacts(edbFactsPlusNewFacts, edbFacts);
		Map<String, List<Rule>> dependentRules = dependentRules(stratifiedRule);

		Set<Expression> newFacts = new HashSet<>();
		do {
			newFacts.clear();

			for (Rule rule : stratifiedRule) {
				newFacts.addAll(matchRule(edbFactsPlusNewFacts, rule));
			}
			if (printIntermediateResultsToFile) {
				List<String> newDerivedFacts = new ArrayList<>();
				newDerivedFacts.add("*** New Facts Generated in this Iteration ***");
				newDerivedFacts.addAll(newFacts.stream().map(idb -> idb.toString()).collect(Collectors.toList()));
				newDerivedFacts.add("\n");
				try {
					Utils.writeToFile(newDerivedFacts,
							Main.getInstance().getProp().getProperty(Constants.OUTPUT_INTERMEDIATE_RESULT_FILE), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			stratifiedRule = getDependentRules(newFacts, dependentRules);

			edbFactsPlusNewFacts.clear();
			Utils.addAllFacts(edbFactsPlusNewFacts, edbFactsCopy);
			Utils.addAllFacts(edbFactsPlusNewFacts, newFacts);
			Utils.addAllFacts(edbFacts, newFacts);

		} while (!newFacts.isEmpty());
		return edbFacts;
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
			
			if (!relevantFacts.containsKey(exp.getPredicate())||(relevantFacts.containsKey(exp.getPredicate())
					&& !relevantFacts.get(exp.getPredicate()).contains(exp))) {
				newFacts.add(exp);
			} else {
				newFacts.remove(exp);
			}
			
			if(Codalog.getInstance().getEdb().getEdbMap().containsKey(exp.getPredicate()) && Codalog.getInstance().getEdb().getEdbMap().get(exp.getPredicate()).contains(exp)) {
				newFacts.remove(exp);
			}
		}

		return newFacts;
	}
}
