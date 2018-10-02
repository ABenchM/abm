DROP TABLE IF EXISTS `role_properties`;
DROP TABLE IF EXISTS `role_members`;
DROP TABLE IF EXISTS `role`;

CREATE TABLE `role`
(
   `role_name` varchar(255) NOT NULL,
   `role_type` tinyint NOT NULL,
   PRIMARY KEY(`role_name`)
);

CREATE TABLE `role_properties`
(
   `property_role` varchar(255) NOT NULL,
   `property_name` varchar(255) NOT NULL,
   `property_value` varchar(1024),
   `property_type` tinyint NOT NULL,
   PRIMARY KEY (`property_role`,`property_name`)
);
ALTER TABLE `role_properties` ADD FOREIGN KEY (`property_role`) REFERENCES `role`(`role_name`);

CREATE TABLE `role_members`
(
   `member_parent` varchar(255) NOT NULL,
   `member_member` varchar(255) NOT NULL,
   `member_is_basic` bit(1),
   PRIMARY KEY (`member_member`)
);
ALTER TABLE `role_members` ADD FOREIGN KEY (`member_member`) REFERENCES `role`(`role_name`);
