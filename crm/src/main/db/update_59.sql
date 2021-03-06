use crmdb;
drop table if Exists product;
drop table if Exists productline;
drop table if Exists productcategory;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  `productlineId` int(11) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `whenadded` datetime DEFAULT NULL,
  `modify_datetime` datetime DEFAULT NULL,
  `owner` varchar(50) DEFAULT NULL,
  `modifier` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

CREATE TABLE `productcategory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  `productId` int(11) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `whenadded` datetime DEFAULT NULL,
  `modify_datetime` datetime DEFAULT NULL,
  `owner` varchar(50) DEFAULT NULL,
  `modifier` varchar(50) DEFAULT NULL,
  `productlineId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8;

CREATE TABLE `productline` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  `description` varchar(512) DEFAULT NULL,
  `whenadded` datetime DEFAULT NULL,
  `modify_datetime` datetime DEFAULT NULL,
  `owner` varchar(50) DEFAULT NULL,
  `modifier` varchar(50) DEFAULT NULL,
  `val` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

