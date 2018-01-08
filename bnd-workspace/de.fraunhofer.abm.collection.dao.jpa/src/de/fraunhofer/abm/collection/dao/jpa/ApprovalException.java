package de.fraunhofer.abm.collection.dao.jpa;

public class ApprovalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApprovalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApprovalException(String message) {
		super(message);
	}

}
