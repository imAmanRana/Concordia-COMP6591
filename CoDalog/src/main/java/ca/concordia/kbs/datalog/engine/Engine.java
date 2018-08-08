/**
 * 
 */
package ca.concordia.kbs.datalog.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.concordia.kbs.datalog.Codalog;
import ca.concordia.kbs.datalog.exception.CoDalogException;
import ca.concordia.kbs.datalog.syntax.BuiltInOperator;
import ca.concordia.kbs.datalog.syntax.Expression;
import ca.concordia.kbs.datalog.syntax.Rule;

/**
 * @author AmanRana
 *
 */
public interface Engine {

	/**
	 * Reordering to put negated literals, build-In at the end for evaluation
	 * 
	 * @param goals
	 * @return list of reordered goals
	 */
	default List<Expression> reorder(List<Expression> goals) {
		List<Expression> orderedGoals = new ArrayList<>(goals.size());
		for (Expression exp : goals) {
			if (!exp.isNegation()
					&& !(exp.isBuiltIn() && !exp.getPredicate().equals(BuiltInOperator.EQUAL.getValue()))) {
				orderedGoals.add(exp);
			}
		}

		for (Expression exp : goals) {
			if ((exp.isBuiltIn() && !exp.getPredicate().equals(BuiltInOperator.EQUAL.getValue())) || exp.isNegation()) {
				orderedGoals.add(exp);
			}
		}
		return orderedGoals;
	}

	/**
	 * Searches in the list for the relevant predicates required to evaluate the
	 * goals
	 * 
	 * @param codalog
	 * @param goals
	 * @return set of relevant predicates
	 */
	default Set<String> fetchRelevantPredicates(Codalog codalog, List<Expression> goals) {
		Expression expression;
		Set<String> relevantPredicates = new HashSet<>();
		List<Expression> goalsCopy = new LinkedList<>(goals);
		while (!goalsCopy.isEmpty()) {
			expression = ((LinkedList<Expression>) goalsCopy).poll();
			if (!relevantPredicates.contains(expression.getPredicate())) {
				relevantPredicates.add(expression.getPredicate());
				for (Rule rule : codalog.getIdb().getIdb()) {
					if (expression.getPredicate().equals(rule.getHead().getPredicate())) {
						goalsCopy.addAll(rule.getBody());
					}
				}
			}
		}
		return relevantPredicates;
	}

	default List<Collection<Rule>> stratifyRules(Set<Rule> rules) throws CoDalogException {
		ArrayList<Collection<Rule>> stratifiedRules = new ArrayList<>();
		Map<String, Integer> stratMap = new HashMap<>();
		Iterator<Rule> rulesItr = rules.iterator();
		String predicate;
		Integer strat;
		Rule rule;
		while (rulesItr.hasNext()) {
			rule = rulesItr.next();
			predicate = rule.getHead().getPredicate();
			strat = stratMap.get(predicate);
			if (strat == null) {
				strat = dfs(rule.getHead(), rules, 0, new ArrayList<Expression>());
				stratMap.put(predicate, strat);
			}
			while (strat >= stratifiedRules.size()) {
				stratifiedRules.add(new ArrayList<>());
			}
			
			stratifiedRules.get(strat).add(rule);

		}
		if(Codalog.getInstance().getEngineType().equals(EngineType.SEMI_NAIVE))
			stratifiedRules.add(rules);
		return stratifiedRules;
	}

	default Integer dfs(Expression head, Set<Rule> rules, int level, List<Expression> traversedExpression)
			throws CoDalogException {
		String predicate = head.getPredicate();

		// check for negative recursion
		boolean negated = head.isNegation();
		StringBuilder route = new StringBuilder(predicate); // for error reporting
		for (int i = traversedExpression.size() - 1; i >= 0; i--) {
			Expression e = traversedExpression.get(i);
			route.append(e.isNegation() ? " <- ~" : " <- ").append(e.getPredicate());
			if (e.getPredicate().equals(predicate)) {
				if (negated) {
					throw new CoDalogException(
							"Negative Recursion for path [" + route.toString() + "]. Program cannot be Stratified");
				}
				return 0;
			}
			if (e.isNegation()) {
				negated = true;
			}
		}
		traversedExpression.add(head);

		// actual dfs
		int stratum = 0;
		for (Rule rule : rules) {
			if (rule.getHead().getPredicate().equals(predicate)) {
				for (Expression exp : rule.getBody()) {
					int x = dfs(exp, rules, level + 1, traversedExpression);
					if (exp.isNegation())
						x++;
					if (x > stratum) {
						stratum = x;
					}
				}
			}
		}
		traversedExpression.remove(traversedExpression.size() - 1);

		return stratum;
	}

	default Set<Rule> fetchRelevantRules(List<Rule> rules, Set<String> relevantPredicates) {
		Set<Rule> relevantRules = new HashSet<>();
		for (Rule rule : rules) {
			if (relevantPredicates.contains(rule.getHead().getPredicate())) {
				relevantRules.add(rule);
			}
		}
		return relevantRules;

	}

	default Map<String, List<Rule>> dependentRules(Collection<Rule> stratifiedRule) {

		Map<String, List<Rule>> dependentRules = new HashMap<>();
		Rule rule;

		Iterator<Rule> ruleItr = stratifiedRule.iterator();
		while (ruleItr.hasNext()) {
			rule = ruleItr.next();
			for (Expression body : rule.getBody()) {
				List<Rule> dependants = dependentRules.get(body.getPredicate());
				if (dependants == null) {
					dependants = new ArrayList<>();
					dependentRules.put(body.getPredicate(), dependants);
				}
				if (!dependants.contains(rule)) {
					dependants.add(rule);
				}
			}
		}
		return dependentRules;
	}

	default Collection<Rule> getDependentRules(Set<Expression> facts, Map<String, List<Rule>> dependentRules) {
		Set<Rule> dependantRules = new HashSet<>();
		for (Expression exp : facts) {
			Collection<Rule> rules = dependentRules.get(exp.getPredicate());
			if (rules != null) {
				dependantRules.addAll(rules);
			}
		}
		return dependantRules;
	}

	default Collection<Map<String, String>> matchGoals(List<Expression> body,
			Map<String, Set<Expression>> relevantFacts, Map<String, String> bindings) {

		Collection<Map<String, String>> output = new ArrayList<>();
		Expression goal = body.get(0); // get the 1st goal

		boolean lastGoal = (body.size() == 1);

		if (goal.isBuiltIn()) {
			Map<String, String> newBindings;
			if (bindings == null)
				newBindings = new HashMap<>();
			else
				newBindings = new HashMap<>(bindings);
			boolean eval = goal.evalBuiltIn(newBindings);
			if (eval && !goal.isNegation() || !eval && goal.isNegation()) {
				if (lastGoal) {
					return Collections.singletonList(newBindings);
				} else {
					return matchGoals(body.subList(1, body.size()), relevantFacts, newBindings);
				}
			}
			return Collections.emptyList();
		}

		if (goal.isNegation()) {
			if (bindings != null) {
				goal = goal.substitute(bindings);
			}
			for (Expression fact : (relevantFacts.get(goal.getPredicate()) == null ? Collections.<Expression>emptySet()
					: relevantFacts.get(goal.getPredicate()))) {
				Map<String, String> newBindings = new HashMap<>(bindings);
				if (fact.performUnification(goal, newBindings)) {
					return Collections.emptyList();
				}
			}
			// not found
			if (lastGoal) {
				output.add(bindings);
			} else {
				output.addAll(matchGoals(body.subList(1, body.size()), relevantFacts, bindings));
			}
		} else {
			for (Expression fact : (relevantFacts.get(goal.getPredicate()) == null ? Collections.<Expression>emptySet()
					: relevantFacts.get(goal.getPredicate()))) {
				Map<String, String> variableBindings;
				if (bindings == null)
					variableBindings = new HashMap<>();
				else
					variableBindings = new HashMap<>(bindings);
				if (fact.performUnification(goal, variableBindings)) {
					if (lastGoal) {
						output.add(variableBindings);
					} else {
						// More goals to match. Recurse with the remaining goals.
						output.addAll(matchGoals(body.subList(1, body.size()), relevantFacts, variableBindings));
					}
				}
			}
		}
		return output;
	}

	public Collection<? extends Expression> matchRule(Map<String, Set<Expression>> relevantFacts, final Rule rule);

	public Map<List<Expression>, Collection<Map<String, String>>> query(Codalog codalog, List<List<Expression>> goals)
			throws CoDalogException;

	public void generateNewFacts(Codalog codalog) throws CoDalogException;
}
