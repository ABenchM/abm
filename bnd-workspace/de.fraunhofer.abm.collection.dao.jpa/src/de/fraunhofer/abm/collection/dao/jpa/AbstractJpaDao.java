package de.fraunhofer.abm.collection.dao.jpa;

import javax.persistence.EntityManager;

public abstract class AbstractJpaDao {
//    protected void attachRepositories(JpaVersion version) {
//        for (JpaCommit commit : version.commits) {
//            attachRepository(commit);
//        }
//    }

//    protected void attachRepository(JpaCommit commit) {
//        JpaRepository repo = commit.repository;
//        JpaRepository existingRepo = getEntityManager().find(JpaRepository.class, repo.id);
//        if (existingRepo != null) {
//            existingRepo.id = repo.id;
//            existingRepo.remoteId = repo.remoteId;
//            existingRepo.repositoryUrl = repo.repositoryUrl;
//            existingRepo.commitsUrl = repo.commitsUrl;
//            existingRepo.contentsUrl = repo.contentsUrl;
//            existingRepo.contributorsUrl = repo.contributorsUrl;
//            existingRepo.creationDate = repo.creationDate;
//            existingRepo.description = repo.description;
//            existingRepo.forks = repo.forks;
//            existingRepo.hasDownloads = repo.hasDownloads;
//            existingRepo.hasWiki = repo.hasWiki;
//            existingRepo.htmlUrl = repo.htmlUrl;
//            existingRepo.isPrivate = repo.isPrivate;
//            existingRepo.issuesUrl = repo.issuesUrl;
//            existingRepo.latestUpdate = repo.latestUpdate;
//            existingRepo.license = repo.license;
//            existingRepo.name = repo.name;
//            existingRepo.openIssues = repo.openIssues;
//            existingRepo.owner = repo.owner;
//            existingRepo.ownerType = repo.ownerType;
//            existingRepo.releasesUrl = repo.releasesUrl;
//            existingRepo.score = repo.score;
//            existingRepo.size = repo.size;
//            existingRepo.starred = repo.starred;
//            existingRepo.watched = repo.watched;
//            commit.repository = existingRepo;
//        }
//    }

    protected abstract EntityManager getEntityManager();
}
