package de.fraunhofer.abm.useradmin.store;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.service.component.annotations.Reference;

import de.fraunhofer.abm.useradmin.dao.RoleDao;
import de.fraunhofer.abm.useradmin.dto.RoleDTO;
import junit.framework.TestCase;

public class AddRoleTest extends TestCase {

	@Reference
	private RoleDao roleDao;
	
	@Test
	public void testAddRole() throws Exception {
		String newRole = "TestRole";
		RoleDTO data = new RoleDTO();
		data.name = newRole;
		data.type = 2;
        roleDao.save(data);
        RoleDTO newData = roleDao.findByName(newRole);
        Assert.assertEquals(data.name, newData.name);
        Assert.assertEquals(data.type, newData.type);
        roleDao.delete(newRole);
	}
	
}
