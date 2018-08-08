/**
 * 
 */
package ca.concordia.kbs.datalog.database;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.kbs.datalog.syntax.Expression;

/**
 * @author AmanRana
 *
 */
public class QueryDB {
	
	private List<List<Expression>> query;
	
	public QueryDB() {
		this.setQuery(new ArrayList<>());
	}

	/**
	 * @return the query
	 */
	public List<List<Expression>> getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(List<List<Expression>> query) {
		this.query = query;
	}
}
