package de.fraunhofer.abm.builder.api;

import java.io.File;

import de.fraunhofer.abm.domain.RepositoryDTO;

public interface ProjectBuilderFactory {

    /**
     * Creates a ProjectBuilder for the given repository and directory. If this factory
     * cannot provide a suitable builder, <code>null</code> is returned
     * @param repo
     * @param repoDir
     * @return A ProjectBuilder instance or <code>null</code>
     */
    public ProjectBuilder createProjectBuilder(RepositoryDTO repo, File repoDir);
}
