

call add_column('version','name','varchar(255)');

call add_column('version','derivedFrom','varchar(255)');

call add_column('collection','isActive','tinyint(4)');

CREATE TABLE IF NOT EXISTS `project` (
  `id` varchar(255) NOT NULL,
  `version_id` varchar(255) NOT NULL,
  `project_id` varchar(255) NOT NULL,
  `source` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`version_id`) REFERENCES `version` (`id`)
);

call add_column('user','firstname','varchar(255)');

call add_column('user','lastname','varchar(255)');

call add_column('user','password','varchar(255)');

call add_column('user','affiliation','varchar(255)');

call add_column('user','locked','tinyint(4)');

call add_column('user','email','varchar(255)');


