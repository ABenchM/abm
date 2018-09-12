package de.fraunhofer.abm.app.controllers;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.dto.DTO;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.domain.CollectionDTO;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.user")
public class UserAdminController implements REST {

    private static final transient Logger logger = LoggerFactory.getLogger(UserAdminController.class);

    @Reference
    private UserAdmin userAdmin;

    @Reference
    private Authorizer authorizer;

    @Reference
    private CollectionDao collectionDao;
    
    public AbmUser getUser(RESTRequest rr, String name) {
        authorizer.requireRole("UserAdmin");

        logger.debug("Looking for user {} in {}", name, userAdmin.getClass().getName());
        AbmUser subject = null;
        Role role = userAdmin.getRole(name);
        if (role != null) {
            logger.debug("Found {} - {}", role.getName(), role.getType());
            subject = new AbmUser();
            subject.name = role.getName();
            subject.type = role.getType();
            subject.properties = toMap(role.getProperties());
        } else {
            logger.debug("User {} does not exist", name);
        }
        return subject;
    }

    interface UserRequest extends RESTRequest {
        AbmUser _body();
    }

    public void postUser(UserRequest ur) {
        authorizer.requireRole("UserAdmin");
        AbmUser subject = ur._body();
        logger.debug("Creating user {}", subject.name);
        userAdmin.createRole(subject.name, Role.USER);
    }

    public void deleteUser(String name) {
        authorizer.requireRole("UserAdmin");

        logger.debug("Deleting user {}", name);
        userAdmin.removeRole(name);
    }
    

    public static class AbmUser extends DTO {
        public String name;
        public int type;
        @SuppressWarnings("rawtypes")
        public Map properties;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Map toMap(Dictionary dict) {
        Map map = new HashMap<>();
        Enumeration keys = dict.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            map.put(key, dict.get(key));
        }
        return map;
    }
}
