/**
 * 
 */
package ca.concordia.kbs.datalog.exception;

/**
 * Datalog Exception
 * @author AmanRana
 *
 */
public class CoDalogException extends Exception {

	private String message;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public CoDalogException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
		this.setMessage(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CoDalogException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
		this.setMessage(message);
	}

	/**
	 * @param cause
	 */
	public CoDalogException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
