package de.fraunhofer.abm.collection.dao;

import java.util.List;

import de.fraunhofer.abm.domain.CollectionDTO;

public interface CollectionDao {

    public List<CollectionDTO> select();
    public List<CollectionDTO> findByUser(String user);
    public CollectionDTO findByCommit(String id);
    public CollectionDTO findById(String id);
    public List<CollectionDTO> findPublicId(String id);
    public List<CollectionDTO> findPrivateId(String id, String user);
    public List<CollectionDTO> findPublic();
    public List<CollectionDTO> findPublic(String keywords);
    public void save(CollectionDTO collection);
    public void update(CollectionDTO collection);
    public void delete(String id);
	void updateUserPublicCollections(String user);
	void deletePrivateCollections(String user);
	
}
