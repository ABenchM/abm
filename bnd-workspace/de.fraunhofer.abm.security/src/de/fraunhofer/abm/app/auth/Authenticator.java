package de.fraunhofer.abm.app.auth;

public interface Authenticator {

    public boolean authenticate(String user, String password) throws Exception;
}
