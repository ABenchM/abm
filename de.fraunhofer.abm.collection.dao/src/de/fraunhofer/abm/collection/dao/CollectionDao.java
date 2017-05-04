package de.fraunhofer.abm.collection.dao;

import java.util.List;

import de.fraunhofer.abm.domain.CollectionDTO;

public interface CollectionDao {

    public List<CollectionDTO> select();
    public List<CollectionDTO> findByUser(String user);
    public CollectionDTO findByCommit(String id);
    public CollectionDTO findById(String id);
    public void save(CollectionDTO collection);
    public void update(CollectionDTO collection);
    public void delete(String id);
}
