create database if not exists cleanDB_${mark};
use cleanDB_${mark};
create table if not exists build_info (user_id int, time varchar(255),buildstarttime VARCHAR(255) , buildendtime VARCHAR(255) , solutionname VARCHAR(255) , content TEXT,PRIMARY KEY (buildstarttime,buildendtime,solutionname,user_id));
create table if not exists build_project_info (user_id int, time varchar(255),buildid TEXT,buildstarttime VARCHAR(255),buildendtime VARCHAR(255), solutionname VARCHAR(255), projectname TEXT,configurationname TEXT,configurationtype TEXT,runcommand TEXT,commandarguments TEXT,buildlogfile TEXT,buildlogcontent TEXT,compilercommand TEXT,linkcommand TEXT,PRIMARY KEY (buildstarttime,buildendtime,solutionname,user_id));
create table if not exists command_text (user_id int, time varchar(255),action varchar(10),name VARCHAR(255),path TEXT,content TEXT,happentime int8,project TEXT,PRIMARY KEY (time,action,name,user_id));
create table if not exists command_file (user_id int, time varchar(255),action varchar(5),filepath TEXT,pastefilepath TEXT,pasteto TEXT,project VARCHAR(255),PRIMARY KEY (time,action,project,user_id));
create table if not exists content_info (user_id int, time varchar(255),operation varchar(7),fullpath TEXT,textfrom blob,textto blob,line int,lineoffset int,happentime int8,project VARCHAR(255),PRIMARY KEY (operation,happentime,project,user_id));
create table if not exists solution_open_event (user_id int, time varchar(255),solutionname VARCHAR(255),type tinyint,info TEXT,targetfolder TEXT,PRIMARY KEY (time,solutionname,type,user_id));
create table if not exists file_event (user_id int, time varchar(255),filename TEXT,projectname VARCHAR(255),type tinyint,targetFile TEXT,PRIMARY KEY (time,projectname,type,user_id));
create table if not exists test_result (user_id int,time varchar(255) ,score DOUBLE ,ac varchar(300),wa varchar(300),re varchar(300),tie varchar(300),me varchar(300),sec varchar(300), PRIMARY KEY(time,user_id));
CREATE TABLE IF NOT EXISTS breakpoint ( user_id int,id INTEGER, tag TEXT, `condition` TEXT, condition_type TEXT, current_hits INT DEFAULT 0, file TEXT NOT NULL, file_column INT NOT NULL, file_line INT NOT NULL, function_name TEXT, location_type TEXT NOT NULL , enabled TEXT NOT NULL,UNIQUE(user_id,id));
CREATE TABLE IF NOT EXISTS exception ( user_id int,id INTEGER, type TEXT, name TEXT, description TEXT, code INT, action TEXT NOT NULL,UNIQUE(user_id,id));
create table if not exists debug_info ( user_id int,id INTEGER,type VARCHAR(255) NOT NULL, timestamp VARCHAR(255) NOT NULL, debug_target TEXT, config_name TEXT,break_reason TEXT, break_breakpoint INTEGER, run_type TEXT, run_breakpoint INTEGER,not_handled INTEGER,exception_thrown INTEGER,UNIQUE(user_id,type,timestamp));