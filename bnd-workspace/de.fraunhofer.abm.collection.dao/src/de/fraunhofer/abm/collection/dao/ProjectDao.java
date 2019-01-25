package de.fraunhofer.abm.collection.dao;

import java.util.List;

import de.fraunhofer.abm.domain.ProjectObjectDTO;

public interface ProjectDao {

	void save(ProjectObjectDTO project);

	void delete(ProjectObjectDTO result);

}
