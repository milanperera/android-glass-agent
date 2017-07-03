
-- -----------------------------------------------------
--  Agent Database
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `picavi_DEVICE` (
  `picavi_DEVICE_ID` VARCHAR(45) NOT NULL ,
  `DEVICE_NAME` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`picavi_DEVICE_ID`) );

CREATE TABLE IF NOT EXISTS `edge_DEVICE` (
  `picavi_DEVICE_ID` VARCHAR(45) NOT NULL ,
  `SERIAL` VARCHAR(45) NOT NULL ,
  `DEVICE_NAME` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`SERIAL`) );



