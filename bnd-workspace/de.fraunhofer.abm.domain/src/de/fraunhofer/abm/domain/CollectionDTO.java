package de.fraunhofer.abm.domain;

import java.util.Date;
import java.util.List;

public class CollectionDTO {
    public String id;
    public String user;
    public String name;
    public String description;
    public boolean privateStatus;
    public Date creation_date;
    public List<VersionDTO> versions;
}
