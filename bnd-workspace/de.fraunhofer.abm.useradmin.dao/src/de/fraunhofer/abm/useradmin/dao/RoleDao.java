package de.fraunhofer.abm.useradmin.dao;

import java.util.List;

import de.fraunhofer.abm.useradmin.dto.RoleDTO;

public interface RoleDao {
    public List<RoleDTO> select();

    public RoleDTO findByName(String name);

    public void save(RoleDTO data);

    public void update(RoleDTO data);

    public void delete(String name) ;

}
