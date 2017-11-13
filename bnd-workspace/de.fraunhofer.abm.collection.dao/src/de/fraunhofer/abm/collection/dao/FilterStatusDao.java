package de.fraunhofer.abm.collection.dao;



import java.util.List;

import de.fraunhofer.abm.domain.FilterStatusDTO;

public interface FilterStatusDao {

	public List<FilterStatusDTO> findFilters(String versionId);
	public void addFilter(FilterStatusDTO filterDTO );
	public void dropFilters(String versionId);
	public int findThreshold(String versionId,String filterName);
	
}
