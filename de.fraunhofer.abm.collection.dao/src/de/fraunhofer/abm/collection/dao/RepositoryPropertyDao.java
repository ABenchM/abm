package de.fraunhofer.abm.collection.dao;

import java.util.List;

import de.fraunhofer.abm.domain.RepositoryPropertyDTO;

public interface RepositoryPropertyDao {
    public List<RepositoryPropertyDTO> findByRepository(String repositoryId);
    public RepositoryPropertyDTO findById(String id);
    public void save(RepositoryPropertyDTO prop);
    public void update(RepositoryPropertyDTO prop);
    public void delete(String id);
}
