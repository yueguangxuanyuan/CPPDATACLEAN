package cn.nju.edu.software.SqlHelp;


import cn.nju.edu.software.ConstantConfig;
import java.sql.*;



/**
 * Created by zr on 2017/11/14.
 */
public class DaoUtil {
    public void createDatabase(){
        String sql = "";//建库语句
        Statement s = null;
        Connection c = getMySqlConnection(ConstantConfig.CLEANBASE);
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
            c = DriverManager.getConnection(ConstantConfig.MYSQLURL+database, ConstantConfig.MYSQLUSERNAME, ConstantConfig.MYSQLPASSWORD);
        }catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }
}
