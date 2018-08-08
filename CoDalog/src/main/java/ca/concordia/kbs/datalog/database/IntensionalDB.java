/**
 * 
 */
package ca.concordia.kbs.datalog.database;

import java.util.ArrayList;
import java.util.List;

import ca.concordia.kbs.datalog.syntax.Rule;

/**
 * @author AmanRana
 *
 */
public class IntensionalDB {

	private List<Rule> idb;
	
	public IntensionalDB() {
		this.setIdb(new ArrayList<>());
	}

	/**
	 * @return the idb
	 */
	public List<Rule> getIdb() {
		return idb;
	}

	/**
	 * @param idb the idb to set
	 */
	public void setIdb(List<Rule> idb) {
		this.idb = idb;
	}
	
}
