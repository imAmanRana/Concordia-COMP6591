/**
 * 
 */
package ca.concordia.kbs.datalog.engine;

/**
 * @author AmanRana
 *
 */
public enum EngineType {

	NAIVE("naive"),
	SEMI_NAIVE("seminaive");
	
	private String engineType;
	
	private EngineType(String engineType) {
		this.setEngineType(engineType);
	}

	/**
	 * @return the engineType
	 */
	public String getEngineType() {
		return engineType;
	}

	/**
	 * @param engineType the engineType to set
	 */
	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}
}
