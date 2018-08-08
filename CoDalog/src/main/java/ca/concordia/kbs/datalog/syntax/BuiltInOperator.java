/**
 * 
 */
package ca.concordia.kbs.datalog.syntax;

/**
 * @author AmanRana
 *
 */
public enum BuiltInOperator {
	
	EQUAL("="),
	NOT_EQUAL("!="),
	NOT_EQUAL_NEW("<>"),
	LESS_THAN("<"),
	LESS_THAN_EQUAL_TO("<="),
	GREATER_THAN(">"),
	GREATER_THAN_EQUAL_TO(">=");
	
	
	private String value;
	
	
	BuiltInOperator(String value){
		this.setValue(value);
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
