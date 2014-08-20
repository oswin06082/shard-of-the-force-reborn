--
-- Definition of table `inst_players`
--

DROP TABLE IF EXISTS `inst_players`;
CREATE TABLE  `inst_players` (
  `characterid` bigint(20) unsigned NOT NULL,
  `charactername` varchar(100) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `z` float NOT NULL,
  `planetid` int(10) unsigned NOT NULL,
  `serverid` int(10) unsigned NOT NULL,
  `online` tinyint(1) NOT NULL,
  PRIMARY KEY (`characterid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


--
-- Definition of table `inst_server_status`
--

DROP TABLE IF EXISTS `inst_server_status`;
CREATE TABLE  `inst_server_status` (
  `process` varchar(100) NOT NULL,
  `uptime` bigint(20) unsigned NOT NULL,
  `memused` bigint(20) unsigned NOT NULL,
  `memfree` bigint(20) unsigned NOT NULL,
  `memtotal` bigint(20) unsigned NOT NULL,
  `processorcount` int(10) unsigned NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
