package de.fraunhofer.abm.collection.dao;

import de.fraunhofer.abm.domain.ProjectBuildDTO;

public interface ProjectBuildDao {

    public ProjectBuildDTO findByVersion(String id);
    public void save(ProjectBuildDTO projectBuild);
    public void update(ProjectBuildDTO projectBuild);
    public void delete(String id);
}
