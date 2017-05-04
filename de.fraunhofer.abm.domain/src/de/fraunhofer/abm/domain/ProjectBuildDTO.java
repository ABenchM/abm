package de.fraunhofer.abm.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectBuildDTO {
    public String id = UUID.randomUUID().toString();
    public List<BuildStepDTO> buildSteps = new ArrayList<>();
    public String repositoryId;
    public RepositoryDTO repository;
    public String buildResultId;

    // this is needed for the build process and is not stored in the DB
    public CommitDTO commit;
}
