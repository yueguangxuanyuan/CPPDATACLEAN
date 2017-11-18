
create database if not exists temp_logdb;
use temp_logdb;
create table if not exists build_info (id INTEGER  auto_increment UNIQUE , time varchar(255),buildstarttime VARCHAR(255) , buildendtime VARCHAR(255) , solutionname VARCHAR(255) , content TEXT,PRIMARY KEY (buildstarttime,buildendtime,solutionname));
create table if not exists build_project_info (id INTEGER  auto_increment UNIQUE , time varchar(255),buildid TEXT,buildstarttime VARCHAR(255),buildendtime VARCHAR(255), solutionname VARCHAR(255), projectname TEXT,configurationname TEXT,configurationtype TEXT,runcommand TEXT,commandarguments TEXT,buildlogfile TEXT,buildlogcontent TEXT,compilercommand TEXT,linkcommand TEXT,PRIMARY KEY (buildstarttime,buildendtime,solutionname));
create table if not exists command_text (id INTEGER  AUTO_INCREMENT UNIQUE , time varchar(255),action varchar(10),name VARCHAR(255),path TEXT,content TEXT,happentime int8,project TEXT,PRIMARY KEY (time,action,name));
create table if not exists command_file (id INTEGER  AUTO_INCREMENT UNIQUE , time varchar(255),action varchar(5),filepath TEXT,pastefilepath TEXT,pasteto TEXT,project VARCHAR(255),PRIMARY KEY (time,action,project));
create table if not exists content_info (id INTEGER  auto_increment UNIQUE , time varchar(255),operation varchar(7),fullpath TEXT,textfrom blob,textto blob,line int,lineoffset int,happentime int8,project VARCHAR(255),PRIMARY KEY (operation,happentime,project));
CREATE TABLE IF NOT EXISTS debug_info ( id INTEGER UNIQUE , type VARCHAR(255) NOT NULL, timestamp VARCHAR(255) NOT NULL, debug_target TEXT, config_name TEXT,PRIMARY KEY (type,timestamp));
-- CREATE TABLE IF NOT EXISTS debug_break ( id INTEGER PRIMARY KEY, break_reason TEXT NOT NULL, breakpoint_last_hit INTEGER);
CREATE TABLE IF NOT EXISTS breakpoint ( id INTEGER PRIMARY KEY, tag TEXT, `condition` TEXT, condition_type TEXT, current_hits INT DEFAULT 0, file TEXT NOT NULL, file_column INT NOT NULL, file_line INT NOT NULL, function_name TEXT, location_type TEXT NOT NULL , enabled TEXT NOT NULL);
-- CREATE TABLE IF NOT EXISTS debug_run ( id INTEGER PRIMARY KEY, run_type TEXT NOT NULL, breakpoint_last_hit INTEGER);
-- CREATE TABLE IF NOT EXISTS exception ( id INTEGER PRIMARY KEY, type TEXT, name TEXT, description TEXT, code INT, action TEXT NOT NULL);
-- CREATE TABLE IF NOT EXISTS debug_exception_thrown ( id INTEGER PRIMARY KEY, exception_id INTEGER NOT NULL);
-- CREATE TABLE IF NOT EXISTS debug_exception_not_handled ( id INTEGER PRIMARY KEY, exception_id INTEGER NOT NULL);
-- CREATE TABLE IF NOT EXISTS local_variable ( id INTEGER PRIMARY KEY, debug_id INTEGER NOT NULL, name TEXT NOT NULL, value TEXT NOT NULL );
-- CREATE TABLE IF NOT EXISTS breakpoint_event ( id INTEGER PRIMARY KEY AUTO_INCREMENT, modification TEXT NOT NULL, breakpoint_id INTEGER );
create table if not exists solution_open_event (id INTEGER  auto_increment UNIQUE , time varchar(255),solutionname VARCHAR(255),type tinyint,info TEXT,targetfolder TEXT,PRIMARY KEY (time,solutionname,type));
create table if not exists file_event (id INTEGER  auto_increment UNIQUE , time varchar(255),filename TEXT,projectname VARCHAR(255),type tinyint,targetFile TEXT,PRIMARY KEY (time,projectname,type));