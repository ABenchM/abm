package de.fraunhofer.abm.projectanalysis;

import java.util.List;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;

public interface ProjectAnalysis {
    public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo) throws Exception;
}
