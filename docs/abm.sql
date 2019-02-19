CREATE TABLE `build_result` (
  `id` varchar(255) NOT NULL,
  `date` datetime DEFAULT NULL,
  `dir` varchar(255) DEFAULT NULL,
  `error` varchar(255) DEFAULT NULL,
  `stackTrace` longtext,
  `status` varchar(255) DEFAULT NULL,
  `versionId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `project_build` (
  `id` varchar(255) NOT NULL,
  `repository` varchar(255) DEFAULT NULL,
  `buildResult_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrabxe1v42km5x7hf3qg5iioen` (`buildResult_id`),
  CONSTRAINT `FKrabxe1v42km5x7hf3qg5iioen` FOREIGN KEY (`buildResult_id`) REFERENCES `build_result` (`id`)
);


CREATE TABLE `build_step` (
  `id` varchar(255) NOT NULL,
  `idx` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `stderr` longtext,
  `stdout` longtext,
  `projectBuild_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmdtt5pljhk3fcdstx3ba25txq` (`projectBuild_id`),
  CONSTRAINT `FKmdtt5pljhk3fcdstx3ba25txq` FOREIGN KEY (`projectBuild_id`) REFERENCES `project_build` (`id`)
);

CREATE TABLE `build_artifacts` (
  `project_build_id` varchar(255) NOT NULL,
  `artifacts` varchar(255) DEFAULT NULL,
  KEY `FK1rog3xxl9ev15j4y6ytkafjbf` (`project_build_id`),
  CONSTRAINT `FK1rog3xxl9ev15j4y6ytkafjbf` FOREIGN KEY (`project_build_id`) REFERENCES `project_build` (`id`)
);


CREATE TABLE `collection` (
  `id` varchar(255) NOT NULL,
  `description` tinytext DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  `privateStatus` tinyint(4) DEFAULT '0',
   `isActive` tinyint(4) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE `version` (
  `id` varchar(255) NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `frozen` bit(1) DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  `collection_id` varchar(255) DEFAULT NULL,
  `privateStatus` tinyint(4) DEFAULT '0',
  `filtered` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `derivedFrom` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhwyps0yuo2dvxmfjyp34odxdk` (`collection_id`),
  CONSTRAINT `FKhwyps0yuo2dvxmfjyp34odxdk` FOREIGN KEY (`collection_id`) REFERENCES `collection` (`id`)
);

CREATE TABLE `collectionPin` (
  `user` varchar(255) DEFAULT NULL,
  `id` varchar(255) DEFAULT NULL
);

CREATE TABLE `repository` (
  `id` varchar(255) NOT NULL,
  `commits_url` varchar(255) DEFAULT NULL,
  `contents_url` varchar(255) DEFAULT NULL,
  `contributors_url` varchar(255) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `default_branch` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `forks` int(11) DEFAULT NULL,
  `has_downloads` bit(1) DEFAULT NULL,
  `has_wiki` bit(1) DEFAULT NULL,
  `html_url` varchar(255) DEFAULT NULL,
  `is_private` bit(1) DEFAULT NULL,
  `issues_url` varchar(255) DEFAULT NULL,
  `latest_update` varchar(255) DEFAULT NULL,
  `license` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `open_issues` int(11) DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `owner_type` varchar(255) DEFAULT NULL,
  `releases_url` varchar(255) DEFAULT NULL,
  `remote_id` int(11) DEFAULT NULL,
  `repository_type` varchar(255) DEFAULT NULL,
  `repository_url` varchar(255) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `starred` int(11) DEFAULT NULL,
  `watched` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `filterPin` (
  `user` varchar(255) DEFAULT NULL,
  `id` varchar(255) DEFAULT NULL
);

CREATE TABLE `filter_status` (
  `id` varchar(255) NOT NULL,
  `filtername` varchar(255) DEFAULT NULL,
  `activate` bit(1) DEFAULT NULL,
  `versionid` varchar(255) DEFAULT NULL,
  `threshold` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `repository_property` (
  `id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  `repository_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbjsprrr8yoxhwb9jbmacjbnnf` (`repository_id`),
  CONSTRAINT `FKbjsprrr8yoxhwb9jbmacjbnnf` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`)
);

CREATE TABLE `hermes_result` (
  `id` varchar(255) NOT NULL,
  `date` datetime DEFAULT NULL,
  `dir` varchar(255) DEFAULT NULL,
  `error` varchar(255) DEFAULT NULL,
  `stackTrace` longtext,
  `status` varchar(255) DEFAULT NULL,
  `versionId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE `commit` (
  `id` varchar(255) NOT NULL,
  `commit` varchar(255) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `repository_id` varchar(255) DEFAULT NULL,
  `version_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgqmkfk1wovdbkmbanfrrxc9pp` (`repository_id`),
  KEY `FKo8og62f41bsf9c1xxyltofj61` (`version_id`),
  CONSTRAINT `FKgqmkfk1wovdbkmbanfrrxc9pp` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`),
  CONSTRAINT `FKo8og62f41bsf9c1xxyltofj61` FOREIGN KEY (`version_id`) REFERENCES `version` (`id`)
);

CREATE TABLE `query_feature_map` (
  `id` varchar(255) DEFAULT NULL,
  `query_name` varchar(255) DEFAULT NULL,
  `feature` varchar(255) DEFAULT NULL
);

CREATE TABLE `user` (
  `name` varchar(255) NOT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `affiliation` varchar(255) DEFAULT NULL,
  `approved` tinyint(4) DEFAULT NULL,
  `approval_token` varchar(50) DEFAULT NULL,
  `locked` tinyint(4) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`name`)
);

CREATE TABLE `project` (
  `id` varchar(255) NOT NULL,
  `version_id` varchar(255) NOT NULL,
  `project_id` varchar(255) NOT NULL,
  `source` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`version_id`) REFERENCES `version` (`id`)
);

INSERT INTO user (name, firstname, lastname, password, affiliation, approved, approval_token, locked, email) VALUES ('admin', 'admin', 'admin', '6tUPRi/i6ukRjmvQK/sKXNtIlamItxhrfY6+p8jAjFc=$WItU72gsjjpOf5vY63GJrp/YeWy8Lle4527Byk4sp0E=', 'University of Paderborn', 1, 'back/end/update', 0, 'anut347@gmail.com');

INSERT INTO role (role_name, role_type) VALUES ('UserAdmin', 2) ON DUPLICATE KEY UPDATE role_type = 2;

INSERT INTO role (role_name, role_type) VALUES ('admin', 1);

INSERT INTO role_members (member_parent, member_member, member_is_basic) VALUES ('UserAdmin', 'admin',  1);

INSERT INTO role_properties (property_role, property_name, property_value, property_type) VALUES ('admin', 'password', '6tUPRi/i6ukRjmvQK/sKXNtIlamItxhrfY6+p8jAjFc=$WItU72gsjjpOf5vY63GJrp/YeWy8Lle4527Byk4sp0E=', 2);

