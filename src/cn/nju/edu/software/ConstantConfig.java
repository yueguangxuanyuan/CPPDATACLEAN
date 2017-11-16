package cn.nju.edu.software;

/**
 * Created by zr on 2017/11/14.
 */
public class ConstantConfig {
    public static String MYSQLURL = "jdbc:mysql://localhost:3306/";//本地mysql的路径
    public static String MYSQLBASE = "cpp_test_server";//server的数据库
    public static String MYSQLUSERNAME = "root";
    public static String MYSQLPASSWORD = "root";
    public static String CLEANBASE = "clean_base";//清洗完的数据存的数据库
    public static String LOGPATH = "";//log文件存放的路径
    public static String MONITORPATH = "";//monitor文件存放的路径
    public static String LOGUNZIPPATH = "";//log文件解压路径
    public static String MONITORUNZIPPATH = "";//monitor文件解压路径
    public static String TEMPBASE = "temp_logdb";//临时的本地表，和logdb文件里的表结构完全一样
    public static String[] TABLELIST = {"build_info","build_project_info","command_text","command_file","content_info","debug_info","debug_break",
            "breakpoint","debug_run","exception","debug_exception_thrown","debug_exception_not_handled","local_variable","breakpoint_event","solution_open_event","file_event"};

}
