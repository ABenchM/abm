package de.fraunhofer.abm.collection.dao;

import de.fraunhofer.abm.domain.HermesBuildDTO;

public interface HermesBuildDao {
	
	  public HermesBuildDTO findByVersion(String id);
	    public void save(HermesBuildDTO hermesBuild);
	    public void update(HermesBuildDTO hermesBuild);
	    public void delete(String id);

}
