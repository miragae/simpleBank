CREATE DATABASE IF NOT EXISTS `bankdb`;
USE `bankdb`;

CREATE TABLE IF NOT EXISTS `user` (
	`id` BIGINT(20) NOT NULL,
	`firstName` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_polish_ci',
	`idNumber` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_polish_ci',
	`lastName` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_polish_ci',
	`login` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_polish_ci',
	`password` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_polish_ci',
	`type` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_polish_ci',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UK_587tdsv8u5cvheyo9i261xhry` (`login`)
)
COLLATE='utf8_polish_ci'
ENGINE=InnoDB
;

INSERT INTO `user` (`id`, `firstName`, `idNumber`, `lastName`, `login`, `password`, `type`) VALUES 
	(1, 'Michal', 'ABC4324', 'Lal', 'admin', '$2a$12$74i.vbaV./bh.9kWVBk/Oed7kOBpX7.5ugw7619Koar4l/tIZxkRK', 'ADMIN');


CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) 
COLLATE='utf8_polish_ci'
ENGINE=InnoDB
;

INSERT INTO `hibernate_sequence` (`next_val`) VALUES
	(2);
