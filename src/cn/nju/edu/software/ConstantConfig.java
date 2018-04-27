package cn.nju.edu.software;

/**
 * Created by zr on 2017/11/14.
 */
public class ConstantConfig {
    /*
    数据库信息
     */
    public static String MYSQLURL = "jdbc:mysql://localhost:3306/";//本地mysql的路径
    public static String MYSQLBASE = "cpp_test_server";//server的数据库
    public static String MYSQLUSERNAME = "root";
    public static String MYSQLPASSWORD = "xuanyuan";

    public static String MYSQLVISUA = "exam_1";//server的数据库
    public static String CLEANBASE = "cpp_temp";//清洗完的数据存的数据库
    public static String ClEAN_TARGET_DB_PATTERN = "cleanDB_${mark}";
    /*
    服务器日志文件存放位置
     */
    public static String LOGPATH = "E:\\server_data\\log\\07\\";//log文件存放的路径
    public static String MONITORPATH = "E:\\server_data\\project\\07\\";//monitor文件存放的路径

    //数据库输出文件的
    public static String LOG_UNZIPPATH = "E:\\server_data\\tmp\\log\\";//log文件解压路径
    public static String MONITOR_UNZIPPATH = "E:\\server_data\\tmp\\monitor\\";//monitor文件解压路径

    public static String TEMPBASE = "temp_logdb";//临时的本地表
    /*
    数据库脚本文件路径
     */
    public static String TEMPDB_INIT_SQLFILE = "sql/temp.sql";
    public static String TEMPDB_CLEAN_SQLFILE = "sql/cleanTemp.sql";

    public static String TARGETDB_INIT_SQLFILE = "sql/target.sql";
    public static String TARGETDB_CLEAN_SQLFILE = "sql/cleanTarget.sql";

    /*
    一些神奇的常数
     */
    public static String[] TABLELIST = {"build_info","build_project_info","command_text","command_file","solution_open_event","file_event","content_info"};
    public static String[] DEBUG_TABLELIST ={"breakpoint","exception","debug_info"};
 }
