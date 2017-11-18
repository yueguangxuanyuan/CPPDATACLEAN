package cn.nju.edu.software.SqlHelp;


import cn.nju.edu.software.ConstantConfig;
import java.sql.*;



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
}
