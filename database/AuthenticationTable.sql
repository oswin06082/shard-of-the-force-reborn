CREATE TABLE `authtable` (
  `id` int NOT NULL auto_increment,
  `username` varchar(255),
  `password` varchar(255),
  `active` tinyint(1),
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=ascii PACK_KEYS=0 ROW_FORMAT=DYNAMIC;
