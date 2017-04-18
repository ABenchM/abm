package de.fraunhofer.abm.useradmin.store;

import java.util.Enumeration;

import org.apache.felix.useradmin.RoleFactory;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

import de.fraunhofer.abm.useradmin.dto.RoleDTO;

public class RoleMapper {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public RoleDTO toDto(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.name = role.getName();
        dto.type = role.getType();

        // properties
        Enumeration keys = role.getProperties().keys();
        while(keys.hasMoreElements()) {
            Object key = keys.nextElement();
            dto.properties.put(key, role.getProperties().get(key));
        }

        // credentials
        if(role.getType() == Role.USER) {
            User user = (User) role;
            keys = user.getCredentials().keys();
            while(keys.hasMoreElements()) {
                Object key = keys.nextElement();
                dto.credentials.put(key, user.getCredentials().get(key));
            }
        }

        // members
        if(role.getType() == Role.GROUP) {
            Group group = (Group) role;
            if(group.getMembers() != null) {
                for (Role child : group.getMembers()) {
                    dto.basicMembers.add(toDto(child));
                }
            }
            if(group.getRequiredMembers() != null) {
                for (Role child : group.getRequiredMembers()) {
                    dto.requiredMembers.add(toDto(child));
                }
            }
        }

        return dto;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Role toRole(RoleDTO dto) {
        // role
        Role role = RoleFactory.createRole(dto.type, dto.name);

        // properties
        Enumeration keys = dto.properties.keys();
        while(keys.hasMoreElements()) {
            Object key = keys.nextElement();
            role.getProperties().put(key, dto.properties.get(key));
        }

        // credentials
        if(dto.type == Role.USER) {
            User user = (User) role;
            keys = dto.credentials.keys();
            while(keys.hasMoreElements()) {
                Object key = keys.nextElement();
                user.getCredentials().put(key, dto.credentials.get(key));
            }
        }

        // members
        if(dto.type == Role.GROUP) {
            Group group = (Group) role;
            for (RoleDTO childDTO : dto.basicMembers) {
                group.addMember(toRole(childDTO));
            }
            for (RoleDTO childDTO : dto.requiredMembers) {
                group.addRequiredMember(toRole(childDTO));
            }
        }

        return role;
    }
}
