package de.fraunhofer.abm.domain;

import java.util.Date;
import java.util.List;

public class VersionDTO {

    public String id;
    public String name;
    public String derivedFrom;
    public String collectionId;
    public List<CommitDTO> commits;
    public List<ProjectObjectDTO> projects;
    public int number;
    public Date creationDate;
    public String comment;
    public boolean frozen = false;
    public boolean filtered = false;
    public String doi;
    public boolean privateStatus;
}