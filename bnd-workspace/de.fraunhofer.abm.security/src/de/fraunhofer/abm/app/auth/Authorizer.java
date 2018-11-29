package de.fraunhofer.abm.app.auth;

import java.util.ArrayList;

public interface Authorizer {

    public void requireRole(String role) throws SecurityException;
    public void requireUser(String user) throws SecurityException;
    public void requireRoles(ArrayList<String> role) throws SecurityException;
}
