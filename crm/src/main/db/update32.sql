ALTER TABLE `crmdb`.`userinfo` 
DROP COLUMN `ts`,
DROP COLUMN `isActivited`,
ADD UNIQUE INDEX `loginName_UNIQUE` (`loginName` ASC);