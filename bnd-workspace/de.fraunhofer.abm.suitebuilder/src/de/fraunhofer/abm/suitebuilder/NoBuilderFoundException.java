package de.fraunhofer.abm.suitebuilder;

import java.util.NoSuchElementException;

public class NoBuilderFoundException extends NoSuchElementException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoBuilderFoundException(String msg) {
        super(msg);
    }

}
