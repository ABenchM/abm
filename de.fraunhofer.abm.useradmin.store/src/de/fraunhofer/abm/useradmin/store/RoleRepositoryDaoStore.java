package de.fraunhofer.abm.useradmin.store;

import java.util.Dictionary;
import java.util.List;

import org.apache.felix.useradmin.RoleRepositoryStore;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.useradmin.dao.RoleDao;
import de.fraunhofer.abm.useradmin.dto.RoleDTO;

@Component(service={RoleRepositoryStore.class,UserAdminListener.class,ManagedService.class}, configurationPid = "de.fraunhofer.abm.useradmin.dao.store")
public class RoleRepositoryDaoStore implements RoleRepositoryStore, UserAdminListener, ManagedService {

    private static final transient Logger logger = LoggerFactory.getLogger(RoleRepositoryDaoStore.class);

    @Reference
    private RoleDao roleDao;

    private RoleMapper mapper = new RoleMapper();

    @Override
    public Role addRole(String name, int type) throws Exception {
        logger.debug("Saving role {},{}", name, type);
        RoleDTO dto = new RoleDTO();
        dto.name = name;
        dto.type = type;
        roleDao.save(dto);
        return mapper.toRole(dto);
    }

    @Override
    public Role getRoleByName(String name) throws Exception {
        logger.debug("Selecting role {}", name);
        Role role = null;
        RoleDTO dto = roleDao.findByName(name);
        if(dto != null) {
            role = mapper.toRole(dto);
        }
        return role;
    }

    @Override
    public Role[] getRoles(String filterValue) throws Exception {
        logger.debug("Selecting all roles {}", filterValue);
        List<RoleDTO> dtos = roleDao.select();
        Role[] roles = new Role[dtos.size()];
        for (int i = 0; i < roles.length; i++) {
            RoleDTO dto = dtos.get(i);
            roles[i] = mapper.toRole(dto);
        }
        return roles;
    }

    @Override
    public Role removeRole(String name) throws Exception {
        logger.debug("Deleting role {}", name);
        Role role = getRoleByName(name);
        roleDao.delete(name);
        return role;
    }

    @Override
    public void roleChanged(UserAdminEvent event) {
        if(event.getType() == UserAdminEvent.ROLE_CHANGED) {
            Role role = event.getRole();
            logger.debug("Role changed: {}", role.getName());
            RoleDTO dto = mapper.toDto(role);
            roleDao.update(dto);
        }
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        logger.info("Configuration changed: {}", properties.toString());
    }
}
