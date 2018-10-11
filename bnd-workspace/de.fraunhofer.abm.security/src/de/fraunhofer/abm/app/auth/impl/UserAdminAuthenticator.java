package de.fraunhofer.abm.app.auth.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authenticator;
import de.fraunhofer.abm.app.auth.Password;
import de.fraunhofer.abm.collection.dao.UserDao;

@Component
public class UserAdminAuthenticator implements Authenticator {

    private static final transient Logger logger = LoggerFactory.getLogger(UserAdminAuthenticator.class);

    @Reference
    private UserAdmin userAdmin;
    
    @Reference
	private UserDao userDao;


    @Override
    public boolean authenticate(String username, String password) throws Exception {
        boolean success = false;
        User user = (User) userAdmin.getRole(username);
        if(user == null ) {
            success = false;
        } else {
            String saltAndPass = (String) user.getCredentials().get("password");
            success = Password.check(password, saltAndPass);
            boolean isLocked = userDao.checkApproved(username);
            if(success && isLocked) {
                logger.debug("User {} successfully logged in", username);
            } else {
                logger.debug("Wrong credentials for user {}", username);
            }
        }
        return success;
    }

}
