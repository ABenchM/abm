package de.fraunhofer.abm.builder.api;

import java.io.File;
import java.util.List;

import de.fraunhofer.abm.domain.RepositoryDTO;

public interface ProjectBuilder {

    public void init(RepositoryDTO repo, File repoDir);
    public List<File> build(RepositoryDTO repo, File repoDir) throws Exception;
    public List<BuildStep<?>> getBuildSteps();
    public <T> BuildStep<T> addBuildStep(BuildStep<T> buildStep);
    public void addBuildProgressListener(BuildProgressListener bpl);
    public void removeBuildProgressListener(BuildProgressListener bpl);
}
