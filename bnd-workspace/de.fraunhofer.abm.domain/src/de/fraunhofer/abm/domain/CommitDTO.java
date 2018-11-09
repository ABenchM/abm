package de.fraunhofer.abm.domain;

import java.util.Date;

public class CommitDTO {
    public String id;
    public String versionId;
    public RepositoryDTO repository;
    public String commitId;
    public Date creationDate;
    public String message;
    public boolean selectProject;
}
