package de.fraunhofer.abm.collection.dao;

import de.fraunhofer.abm.domain.CommitDTO;

public interface CommitDao {

    //    public List<CollectionDTO> select();
    //    public List<CollectionDTO> findByUser(String user);
    public CommitDTO findById(String id);
   
    //    public void save(CollectionDTO collection);
    public void update(CommitDTO commit);
    public void delete(String id);
}
