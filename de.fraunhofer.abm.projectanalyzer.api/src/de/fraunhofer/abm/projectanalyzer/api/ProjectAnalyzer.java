package de.fraunhofer.abm.projectanalyzer.api;

import java.io.File;
import java.util.List;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;

public interface ProjectAnalyzer {

    public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo, File directory);
}
