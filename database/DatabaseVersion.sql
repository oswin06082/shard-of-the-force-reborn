-- This file is OPTIONAL. It is only to be executed when using the creature tool.
-- If this table doesn't exist, the version information will be read from the /.svn/ folders.
-- Notice that databaseVersion is the revision number, NOT the version string from Constants.
DROP TABLE IF EXISTS `database_information`;
CREATE TABLE `database_information` (
  `databaseVersion` smallint(4) unsigned,
  PRIMARY KEY (`databaseVersion`)
) ENGINE=MyISAM DEFAULT CHARSET=ascii;

-- Actual version entry.
INSERT INTO `database_information` VALUES (946);
