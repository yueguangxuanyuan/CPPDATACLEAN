drop table if EXISTS coding_action;
create table coding_action(
	id int(10) not null AUTO_INCREMENT COMMENT 'id主键',
	eid int (10) not null COMMENT '考试id',
	sid int(10) not null COMMENT '学生的id',
	pid int (10)  COMMENT '题目id',
	pname varchar (255) not null COMMENT '题目名称',
	save_num int(10) default '0',
	cut_num int(10) DEFAULT '0',
	paste_num int(10) DEFAULT '0' COMMENT '粘贴次数',
	copy_num int(10) DEFAULT '0',
	undo_num int(10) DEFAULT '0' COMMENT '回撤次数',
	`time` int(10) default '0' COmment '耗费的时间',
	primary key(id)
);

drop table if EXISTS text_info;
create table text_info(
	id int(10) not null AUTO_INCREMENT COMMENT 'id主键',
	eid int (10) not null COMMENT '考试id',
	sid int(10) not null COMMENT '学生的id',
	pid int (10) COMMENT '题目id',
	pname varchar (255)  COMMENT '题目名称',
	type varchar(255) COMMENT '类型，含COPY，CUT，PASTE',
	content text COMMENT 'COPY，CUT，PASTE 的内容',
	`time` datetime COMMENT '操作时间',
	file_name varchar(255),
	file_path varchar(255),
	primary key(id)
);

drop table if EXISTS build;
create table build(
	id int(10) not null AUTO_INCREMENT COMMENT 'id主键',
	eid int (10) not null COMMENT '考试id',
	sid int(10) not null COMMENT '学生的id',
	pid int (10) not null COMMENT '题目id',
	warning_num int(10) default '0' COMMENT '警告信息出现的次数',
	error_num int(10) default '0' COMMENT '警告错误出现的次数',
	success_num int(10) default '0' COMMENT '编译成功的次数',
	failed_num int(10) default '0' COMMENT '编译失败的次数',
	PRIMARY Key(id)
);

drop table if EXISTS build_info;
create table build_info(
	id int(10) not null AUTO_INCREMENT COMMENT 'id主键',
	result_type varchar(255) COMMENT '类型为ERROR,WARNING两种，成功的不记录',
	build_id int(10) not null,
	content text,
	result_info varchar(255) COMMENT '编译的报错类型，如未定义的标识符',
	`begintime` datetime, 
	`endtime` datetime,
	PRIMARY KEY(id)
);


drop table if EXISTS debug;
create table debug(
	id int(10) not null AUTO_INCREMENT COMMENT 'id主键',
	eid int (10) not null COMMENT '考试id',
	sid int(10) not null COMMENT '学生的id',
	pid int (10) not null COMMENT '题目id',
	break_point__num int(10) default '0' COMMENT '设的断点的总数',
	debug_num int(10) default '0' COMMENT '运行的次数',
	debug_time datetime COMMENT 'debug的时间',
	PRIMARY Key(id)
);

drop table if EXISTS debug_exception;
create table debug_exception(
	id int(10) not null AUTO_INCREMENT COMMENT 'id主键',
	eid int (10) not null COMMENT '考试id',
	sid int(10) not null COMMENT '学生的id',
	pid int (10) not null COMMENT '题目id',
	exception_type varchar(255) default '0' COMMENT 'debug异常类型',
	num int(10) default '0' COMMENT '碰到异常的次数',
	PRIMARY Key(id)
);


drop table if EXISTS test;
create table test(
	id int(10) not null AUTO_INCREMENT COMMENT 'id主键',
	eid int (10) not null COMMENT '考试id',
	sid int(10) not null COMMENT '学生的id',
	pid int (10) not null COMMENT '题目id',
	score int(10) default '0' COMMENT '最高得分数',
	accept_case varchar(255) COMMENT '通过的测试用例，使用字符串连接表示，',
	wrong_answer varchar(255) COMMENT '没通过的测试用例',
	test_time datetime COMMENT '测试时间',
	PRIMARY Key(id)
);

drop table if EXISTS varible;
create table varible(
	id int(10) not null AUTO_INCREMENT COMMENT 'id主键',
	sid int(10) not null COMMENT '学生的id',
	eid int (10) not null COMMENT '考试id',
	pid int (10) not null COMMENT '题目id',
	name varchar(255) default '0' COMMENT '变量名称',
	count int(10) default '0' COMMENT '变量出现的次数',
	PRIMARY Key(id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='使用的变量名称的统计';
