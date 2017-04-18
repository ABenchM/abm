package de.fraunhofer.abm.scm.api;

import java.io.File;

import de.fraunhofer.abm.domain.RepositoryDTO;

public interface SCM {

    /**
     * Clones a given repository to the target directory and returns the lastest commit identifier.
     *
     * @param repo
     * @param targetDir
     * @return The ID of the laste commit
     * @throws Exception
     */
    public String clone(RepositoryDTO repo, File targetDir) throws Exception;

    /**
     * Checks out a particular commit, which is identified by the given SHA
     *
     * @param repo
     *            The repository, where you want to checkout a certain commit
     * @param repoDir
     *            The directory this repository has been cloned to
     * @param commitSha
     *            The SHA identifier of the commit to checkout
     * @throws Exception
     */
    public void checkout(RepositoryDTO repo, File repoDir, String commitSha) throws Exception;

    /**
     * Updates a repository, which has been checked out previously
     *
     * @param repo
     *            The repository, where you want to checkout a certain commit
     * @param repoDir
     *            The directory this repository has been cloned to
     * @return The ID of the laste commit
     * @throws Exception
     */
    public String update(RepositoryDTO repo, File repoDir) throws Exception;

    /**
     * @see {@link de.fraunhofer.abm.domain.RepositoryDTO#TYPE_CVS}
     * @see {@link de.fraunhofer.abm.domain.RepositoryDTO#TYPE_GIT}
     * @see {@link de.fraunhofer.abm.domain.RepositoryDTO#TYPE_HG}
     * @see {@link de.fraunhofer.abm.domain.RepositoryDTO#TYPE_SVN}
     */
    public String supports();
}
