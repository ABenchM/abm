package de.fraunhofer.abm.collection.dao;

import java.util.List;

import de.fraunhofer.abm.domain.RepositoryDTO;

public interface RepositoryDao {

    public List<RepositoryDTO> select();
    public List<RepositoryDTO> findByCollection(String collectionId);
    public RepositoryDTO findById(String id);
    public List<RepositoryDTO> findByVersion(String versionId);
    public void save(RepositoryDTO repo);
    public void update(RepositoryDTO repo);
    public void delete(String id);
}
