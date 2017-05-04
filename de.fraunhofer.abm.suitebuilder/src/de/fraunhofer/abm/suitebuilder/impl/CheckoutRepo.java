package de.fraunhofer.abm.suitebuilder.impl;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.AbstractBuildStep;
import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.collection.dao.CommitDao;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.repoarchive.api.RepoArchive;
import de.fraunhofer.abm.scm.api.SCM;

public class CheckoutRepo extends AbstractBuildStep<Boolean> {

    private static final transient Logger logger = LoggerFactory.getLogger(CheckoutRepo.class);

    private File repoDir;
    private List<SCM> scms;
    private CommitDTO commit;
    private CommitDao commitDao;
    private RepoArchive repoArchive;

    public CheckoutRepo(RepositoryDTO repository, File repoDir) {
        super(repository);
        this.repoDir = repoDir;
        name = "Checkout repository from VCS";
    }

    @Override
    public Boolean execute() {
        setStatus(STATUS.IN_PROGRESS);
        SCM scm = getSCM(repository);
        logger.info("Cloning repo {} at {} with {}", repository.id, repository.repositoryUrl, scm.getClass().getName());
        try {
            String latestCommitSha = "";
            if(repoArchive != null && repoArchive.exists(repository.id)) {
                logger.info("Using repo from archive");
                repoArchive.retrieve(repository.id, repoDir);
                latestCommitSha = scm.update(repository, repoDir);
            } else {
                logger.info("RepoArchive not available or repo not archived");
                latestCommitSha = scm.clone(repository, repoDir);
                if(repoArchive != null) {
                    repoArchive.archive(repository.id, repoDir);
                }
            }

            if("HEAD".equalsIgnoreCase(commit.commitId)) {
                // change "HEAD" to the actual commit SHA, so that this build is reproducible
                commit.commitId = latestCommitSha;
                commitDao.update(commit);
            } else {
                // if the commit is not set to HEAD, we have to switch to the actual commit,
                // otherwise cloning should be enough, since a clone always points to the latest commit
                logger.info("Checking out commit {}", commit.commitId);
                scm.checkout(repository, repoDir, commit.commitId);
            }
            setStatus(STATUS.SUCCESS);
            return true;
        } catch (Throwable t) {
            logger.error("Couldn't checkout repository " + repository.name, t);
            errorOutput = BuildUtils.createErrorString("Couldn't checkout repository " + repository.name, t);
            setThrowable(t);
            setStatus(STATUS.FAILED);
            return false;
        }
    }

    private SCM getSCM(RepositoryDTO repo) {
        for (SCM scm : scms) {
            if(scm.supports().equals(repo.repositoryType)) {
                return scm;
            }
        }
        throw new NoSuchElementException("No handler for repo type " + repo.repositoryType + " found. " + repo.repositoryUrl);
    }

    public void setScms(List<SCM> scms) {
        this.scms = scms;
    }

    public void setCommit(CommitDTO commit) {
        this.commit = commit;
    }

    public void setCommitDao(CommitDao commitDao) {
        this.commitDao = commitDao;
    }

    public void setRepoArchive(RepoArchive repoArchive) {
        this.repoArchive = repoArchive;
    }
}
