package cn.nju.edu.software.SqlHelp;


import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Util.FileUtil;

import java.sql.*;
import java.util.HashMap;


/**
 * Created by zr on 2017/11/14.
 */
public class DaoUtil {

    public static void executeSql(Connection c,String sql){
     Statement s = null;
        if(c!=null){
            try {
                s = c.createStatement();
                s.executeQuery(sql);
                s.close();
                c.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    public static void executeUpdate(Connection c,String sql){
        Statement s = null;
        if(c!=null){
            try {
                s = c.createStatement();
                s.executeUpdate(sql);
                s.close();
                c.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    /*
    * 建最后的存储表，直接执行sql
    * */
    public void createTable(){
        String sql = "";//建库语句
        Statement s = null;
        Connection c = getMySqlConnection(ConstantConfig.CLEANBASE);
        executeSql(c,sql);
    }

    /*
    建临时表，直接执行sql
     */
    public void createTempDatabase(){
        String sql = "create DATABASE if not EXISTS temp_logdb";
        Statement s = null;
        Connection c = getMySqlConnection(ConstantConfig.CLEANBASE);
        executeSql(c,sql);

        c =getMySqlConnection(ConstantConfig.TEMPBASE);
        String tableSql ="";
        executeSql(c,tableSql);
    }


    public static Connection getSqliteConnection(String dbPath){
        Connection c = null;
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
        }catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }

    public  static Connection getMySqlConnection(String database){
        Connection c = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection(ConstantConfig.MYSQLURL+database+"?useSSL=true", ConstantConfig.MYSQLUSERNAME, ConstantConfig.MYSQLPASSWORD);
        }catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }

    public static void closeConnection(Connection connection,PreparedStatement prep,ResultSet set){

            try {
                if(connection!=null&&!connection.isClosed()){
                    connection.close();
                }
                if(prep!=null&&!prep.isClosed()){
                    prep.close();
                }

                if(set!=null&&!set.isClosed()){
                    set.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }
    /*
    方法要求脚本文件满足 所有语句用;分割，并且不存在注释
     */
    public static void ExecuteSQLScript(String inDBName, String inScriptPath, HashMap<String,String> scriptParams){
        String theScript  = FileUtil.GetFileContent(inScriptPath);

        if(scriptParams != null){
            for(String key : scriptParams.keySet()){
                String regexPattern = "\\$\\{"+key+"\\}";
                theScript = theScript.replaceAll(regexPattern,scriptParams.get(key));
            }
        }

        Connection c = DaoUtil.getMySqlConnection("");
        Statement statement = null;
        try {
            statement = c.createStatement();

            String[] sqlScripts = theScript.split(";");
            for(String sql : sqlScripts){
                sql = sql.trim();
                if(sql.isEmpty()){
                   continue;
                }
                //遇到注释掉的语句
                if(sql.startsWith("--")){
                    continue;
                }
                statement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
