package de.fraunhofer.abm.collection.dao;

import de.fraunhofer.abm.domain.BuildResultDTO;
import java.util.List;

public interface BuildResultDao {

    public BuildResultDTO findById(String buildResultId);
    public BuildResultDTO findByVersion(String id);
    public List<BuildResultDTO> findRunning(String user);
    public void save(BuildResultDTO buildResult);
    public void update(BuildResultDTO buildResult);
    public void delete(String id);
}
