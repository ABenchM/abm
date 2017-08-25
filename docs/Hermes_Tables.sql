//Table to maintain the Hermes status for each version

create table hermes_result (
  id varchar(255) NOT NULL,
   date datetime DEFAULT NULL,
   dir varchar(255) DEFAULT NULL,
  error varchar(255) DEFAULT NULL,
  stackTrace  longtext,
   status varchar(255) DEFAULT NULL,
  versionId varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);

//Table to check the active filters for each version


//Table to check hermes steps

CREATE TABLE hermes_step (
   id varchar(255) NOT NULL,
   idx bigint(20) DEFAULT NULL,
   name varchar(255) DEFAULT NULL,
   status varchar(255) DEFAULT NULL,
   stderr longtext,
   stdout longtext,
   hermesBuild_id varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY FKhbuildid (hermesBuild_id),
  CONSTRAINT FKhbuildid FOREIGN KEY (hermesBuild_id) REFERENCES  hermes_build (id)
);

//Table for hermes Builder

CREATE TABLE  hermes_build (
   id varchar(255) NOT NULL,
   hermesResult_id varchar(255) DEFAULT NULL,
   PRIMARY KEY (id),
   KEY FKhresultid (hermesResult_id),
   CONSTRAINT FKhresultid FOREIGN KEY (hermesResult_id) REFERENCES hermes_result (id)
);

Create Table filter_status
( id varchar(255) NOT NULL, 
  filtername varchar(255) DEFAULT NULL,
  activate bit(1) DEFAULT NULL,
  versionid varchar(255) DEFAULT NULL,
  PRIMARY KEY(id)
);
