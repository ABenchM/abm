package de.fraunhofer.abm.collection.dao;

import de.fraunhofer.abm.domain.BuildResultDTO;

public interface BuildResultDao {

    public BuildResultDTO findById(String buildResultId);
    public BuildResultDTO findByVersion(String id);
    public void save(BuildResultDTO buildResult);
    public void update(BuildResultDTO buildResult);
    public void delete(String id);
}
