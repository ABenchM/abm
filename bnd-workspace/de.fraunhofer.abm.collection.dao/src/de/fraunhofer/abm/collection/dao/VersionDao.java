package de.fraunhofer.abm.collection.dao;


import java.util.List;

import de.fraunhofer.abm.domain.VersionDTO;

public interface VersionDao {

    public VersionDTO findById(String id);
    public void save(VersionDTO version);
    public void update(VersionDTO version);
    public void delete(String id);
    public List<VersionDTO> findByCollectionId(String collectionId);
    public boolean findProjectByVersionId(String versionId, String projectId);

}
