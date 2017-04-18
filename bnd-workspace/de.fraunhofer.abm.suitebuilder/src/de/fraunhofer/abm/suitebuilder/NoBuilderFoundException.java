package de.fraunhofer.abm.suitebuilder;

import java.util.NoSuchElementException;

public class NoBuilderFoundException extends NoSuchElementException {

    public NoBuilderFoundException(String msg) {
        super(msg);
    }

}
