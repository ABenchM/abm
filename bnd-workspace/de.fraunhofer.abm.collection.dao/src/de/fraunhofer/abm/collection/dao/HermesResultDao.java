package de.fraunhofer.abm.collection.dao;

import java.util.List;


import de.fraunhofer.abm.domain.HermesResultDTO;

public interface HermesResultDao {

	public HermesResultDTO findById(String hermesResultID);
    public HermesResultDTO findByVersion(String id);
    public List<HermesResultDTO> findRunning(String user);
    public void save(HermesResultDTO hermesResult);
    public void update(HermesResultDTO hermesResult);
    public void delete(String id);
	
}
