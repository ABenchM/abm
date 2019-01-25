package de.fraunhofer.abm.collection.dao;

import java.util.List;

import de.fraunhofer.abm.domain.ProjectObjectDTO;

public interface ProjectDao {

	void save(ProjectObjectDTO project);

	void delete(ProjectObjectDTO projectId);
	
	public List<ProjectObjectDTO> findproject(String projectId);

}
