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


DROP TABLE  IF EXISTS `food`;
CREATE TABLE `food` (
 `food_id` BIGINT NOT NULL  COMMENT 'id',
  `food_name` varchar(200) default NULL COMMENT '食物名字',
  `food_remark` varchar(50) default NULL COMMENT '备注',
  PRIMARY KEY  (`food_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE  IF EXISTS `orders`;
CREATE TABLE `orders` (
  `order_id` VARCHAR(50) NOT NULL  COMMENT '订单id',
  `detail_id` VARCHAR(50) NOT NULL  COMMENT '订单详情id',
  `order_type` TINYINT(2) default 0 COMMENT '订单类型',
  `remark` varchar(50) default NULL COMMENT '备注',
  PRIMARY KEY  (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE  IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `detail_id` VARCHAR(50) NOT NULL  COMMENT '订单详情id',
  `detail_content` VARCHAR(200)  default NULL COMMENT '订单详情',
  `remark` varchar(50) default NULL COMMENT '备注',
  PRIMARY KEY  (`detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;