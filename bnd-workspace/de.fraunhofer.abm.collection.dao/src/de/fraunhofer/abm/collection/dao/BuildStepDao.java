package de.fraunhofer.abm.collection.dao;

import de.fraunhofer.abm.domain.BuildStepDTO;

public interface BuildStepDao {

    public BuildStepDTO findByVersion(String id);
    public void save(BuildStepDTO buildStep);
    public void update(BuildStepDTO buildStep);
    public void delete(String id);
}
