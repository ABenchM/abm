package de.fraunhofer.abm.collection.dao;



import de.fraunhofer.abm.domain.FilterStatusDTO;

public interface FilterStatusDao {

	public FilterStatusDTO findFilters(String versionId);
	public void addFilter(FilterStatusDTO filterDTO );
	public void dropFilters(String versionId);
	
}
