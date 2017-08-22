package de.fraunhofer.abm.collection.dao;


import de.fraunhofer.abm.domain.HermesStepDTO;

public interface HermesStepDao {
	
	public HermesStepDTO findByVersion(String id);
	public void save(HermesStepDTO hermesStep);
    public void update(HermesStepDTO hermesStep);
    public void delete(String id);
	

}
