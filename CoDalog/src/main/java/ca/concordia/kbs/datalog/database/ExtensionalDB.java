/**
 * 
 */
package ca.concordia.kbs.datalog.database;

import java.util.Map;
import java.util.Set;

import ca.concordia.kbs.datalog.syntax.Expression;

/**
 * @author AmanRana
 *
 */
public interface ExtensionalDB{

	public Set<Expression> getAllFacts();
	
	public void addFact(Expression fact);
	
	public void empty();
	
	public Set<Expression> remove(Expression fact);
	
	public void removeAll(Set<Expression> facts);
	
	public Set<Expression> getFacts(String predicate);
	
	public long getEdbSize();

	public void addAllFacts(Map<String, Set<Expression>> newFacts);

	public void addAllFacts(Set<Expression> newFacts);

	public Map<String, Set<Expression>> getEdbMap();

	public Map<String, Set<Expression>> getEdbMapCopy();
	
}
