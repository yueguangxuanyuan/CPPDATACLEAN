package cn.nju.edu.software;

/**
 * Created by zr on 2017/11/14.
 */
public class ConstantConfig {
    public static String MYSQLURL = "jdbc:mysql://localhost:3306/";//本地mysql的路径
    public static String MYSQLBASE = "cpp_test_server";//server的数据库
    public static String MYSQLVISUA = "exam_1";//server的数据库
    public static String MYSQLUSERNAME = "root";
    public static String MYSQLPASSWORD = "";
    public static String CLEANBASE = "cpp";//清洗完的数据存的数据库
    public static String LOGPATH = "D:\\CPP日志\\log-exam1\\";//log文件存放的路径
    public static String MONITORPATH = "D:\\CPP日志\\monitor-exam1\\";//monitor文件存放的路径
    public static String LOGUNZIPPATH = "";//log文件解压路径
    public static String MONITORUNZIPPATH = "";//monitor文件解压路径
    public static String TEMPBASE = "temp_logdb";//临时的本地表，和logdb文件里的表结构完全一样
    public static String[] TABLELIST = {"build_info","build_project_info","command_text","command_file","debug_info",
            "breakpoint","solution_open_event","file_event"};
    //public static String[] TABLELIST= {"breakpoint"};
}
