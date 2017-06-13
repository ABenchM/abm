package de.fraunhofer.abm.domain;

import java.util.Date;
import java.util.List;

public class VersionDTO {

    public String id;
    public String collectionId;
    public List<CommitDTO> commits;
    public int number;
    public Date creationDate;
    public String comment;
    public boolean frozen = false;
}
