package de.fraunhofer.abm.collection.dao;

import de.fraunhofer.abm.domain.VersionDTO;

public interface VersionDao {

    public VersionDTO findById(String id);
    public void save(VersionDTO version);
    public void update(VersionDTO version);
    public void delete(String id);

}
