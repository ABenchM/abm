package de.fraunhofer.abm.domain;


import java.util.Date;

import java.util.UUID;

public class HermesResultDTO {
	public String id = UUID.randomUUID().toString();
    public Date date;
    public String dir;
    public String status;
    public String error;
    public String stackTrace;
    public String versionId;

}
