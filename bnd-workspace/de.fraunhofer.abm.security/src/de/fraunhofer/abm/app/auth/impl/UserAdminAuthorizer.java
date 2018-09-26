package de.fraunhofer.abm.app.auth.impl;

import java.util.ArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;

@Component
public class UserAdminAuthorizer implements Authorizer {

    @Reference
    private UserAdmin userAdmin;

    @Override
    public void requireRole(String role) throws SecurityException {
        String username = SecurityContext.getInstance().getUser();
        if ("anonymous".equals(username)) {
            throw new SecurityException("Anonymous user is not allowed");
        } else {
            User user = (User) userAdmin.getRole(username);
            if (user == null) {
                throw new SecurityException(username + " does not exist");
            }
            if(!userAdmin.getAuthorization(user).hasRole(role)) {
                throw new SecurityException(username + " does not have role " + role);
            }
        }
    }
    
    @Override
    public void requireRoles(ArrayList<String> roles) throws SecurityException {
    	String username = SecurityContext.getInstance().getUser();
    	Boolean validUser = false;
    	for( String  role : roles) {
    		if ("anonymous".equals(username)) {
                throw new SecurityException("Anonymous user is not allowed");
            } else {
                User user = (User) userAdmin.getRole(username);
                if (user == null) {
                    throw new SecurityException(username + " does not exist");
                }
                if(userAdmin.getAuthorization(user).hasRole(role)) {
                    validUser = true;
                }
            }
    	}
    	if(!validUser) {
    		throw new SecurityException(username + " does not have role " );
    	}
    	
    }
    
    public void requireUser(String user) throws SecurityException {
    	String username = SecurityContext.getInstance().getUser();
    	if(!user.equals(username)){
    		throw new SecurityException(username + " cannot perform actions on " + user + "'s data.");
    	}
    }
}
