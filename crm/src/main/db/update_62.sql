ALTER TABLE `crmdb`.`alert` 
CHANGE COLUMN `expired` `expired` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `publishDate` `publishDate` DATETIME NULL DEFAULT NULL ;