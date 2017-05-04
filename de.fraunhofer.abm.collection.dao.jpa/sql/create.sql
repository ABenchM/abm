CREATE TABLE `collection`
(
   `id` varchar(36) NOT NULL,
   `name` varchar(255) NOT NULL,
   `description` text,
   `creation_date` timestamp,
   `user` varchar(255) NOT NULL,
   PRIMARY KEY(`id`)
);
