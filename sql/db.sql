DROP TABLE  IF EXISTS `user`;
CREATE TABLE `user` (
  `id` VARCHAR (50) NOT NULL  COMMENT 'id',
  `user_name` varchar(200) default NULL COMMENT '名字',
  `age` int(2) default 18 COMMENT '年龄',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE  IF EXISTS `shop`;
CREATE TABLE `shop` (
  `id` VARCHAR (50) NOT NULL  COMMENT 'id',
  `user_name` varchar(200) default NULL COMMENT '名字',
  `address` varchar(50) default NULL COMMENT '地址',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE  IF EXISTS `member`;
CREATE TABLE `member` (
 `id` VARCHAR (50) NOT NULL  COMMENT 'id',
  `member_name` varchar(200) default NULL COMMENT '名字',
  `member_remark` varchar(50) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;