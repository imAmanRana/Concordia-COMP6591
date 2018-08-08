/**
 * 
 */
package ca.concordia.kbs.datalog.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ca.concordia.kbs.datalog.syntax.Expression;

/**
 * @author AmanRana
 *
 */
public class BasicExtensionalDB implements ExtensionalDB {

	private Map<String, Set<Expression>> edb;
	private long size;

	public BasicExtensionalDB() {
		edb = new HashMap<>();
		size = 0;
	}

	@Override
	public Set<Expression> getAllFacts() {
		Set<Expression> expressionSet = new HashSet<>();
		Iterator<String> itr = edb.keySet().iterator();
		while (itr.hasNext()) {
			expressionSet.addAll(edb.get(itr.next()));
		}
		return expressionSet;
	}

	@Override
	public void addFact(Expression fact) {

		if (fact == null)
			return;

		if (edb.containsKey(fact.getPredicate())) {
			if (edb.get(fact.getPredicate()).add(fact))
				size++;
		} else {
			Set<Expression> factSet = new HashSet<>();
			if (factSet.add(fact))
				size++;
			edb.put(fact.getPredicate(), factSet);
		}
	}

	@Override
	public void empty() {
		edb.clear();
	}

	@Override
	public Set<Expression> remove(Expression fact) {
		size -= edb.get(fact.getPredicate()).size();
		return edb.remove(fact.getPredicate());
	}

	@Override
	public void removeAll(Set<Expression> facts) {
		Iterator<Expression> itr = facts.iterator();
		Expression exp;
		while (itr.hasNext()) {
			exp = itr.next();
			edb.remove(exp.getPredicate());
		}
		size = 0;
	}

	@Override
	public Set<Expression> getFacts(String predicate) {
		return edb.get(predicate);
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @return the edb
	 */
	public Map<String, Set<Expression>> getEdb() {
		return edb;
	}

	@Override
	public long getEdbSize() {
		return getSize();
	}

	@Override
	public void addAllFacts(Map<String, Set<Expression>> newFacts) {

		if (newFacts == null)
			return;
		Iterator<String> itr = newFacts.keySet().iterator();
		String predicate;
		while (itr.hasNext()) {
			predicate = itr.next();
			if (edb.containsKey(predicate)) {
				size -= edb.get(predicate).size();
				edb.get(predicate).addAll(newFacts.get(predicate));
				size += edb.get(predicate).size();
			} else {
				edb.put(predicate, newFacts.get(predicate));
				size += edb.get(predicate).size();
			}
		}

	}

	@Override
	public Map<String, Set<Expression>> getEdbMap() {
		return edb;
	}

	@Override
	public void addAllFacts(Set<Expression> newFacts) {

		if (newFacts == null) {
			return;
		}

		for (Expression exp : newFacts) {
			addFact(exp);
		}

	}

	@Override
	public Map<String, Set<Expression>> getEdbMapCopy() {

		Map<String, Set<Expression>> edbCopy = new HashMap<>();
		Iterator<String> itr = edb.keySet().iterator();
		String key;
		while (itr.hasNext()) {
			key = itr.next();
			if (edbCopy.containsKey(key)) {
				edbCopy.get(key).addAll(edb.get(key));
			} else {
				edbCopy.put(key, edb.get(key));
			}
		}
		return edbCopy;
	}
}
