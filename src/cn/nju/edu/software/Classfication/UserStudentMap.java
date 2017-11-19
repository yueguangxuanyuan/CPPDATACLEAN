package cn.nju.edu.software.Classfication;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zuce wei on 2017/11/19.
 */
public class UserStudentMap {
    private static Map<Integer,String>  userStudentMap;
    private static Map<String,Integer> studentUserMap;
    private UserStudentMap(){

    }

    public static Map<Integer,String> userStudentMap(){
        if(userStudentMap!=null){
            return userStudentMap;
        }

        Map<Integer,String> userStudentMap=new HashMap<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select id,username from auth_user");
            set=prepar.executeQuery();
            while(set.next()) {
                userStudentMap.put(set.getInt("id"),set.getString("username"));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }
        return userStudentMap;
    }

    public static Map<String,Integer> studentUserMap(){
        if(studentUserMap!=null){
            return studentUserMap;
        }
        Map<String,Integer> studentUserMap=new HashMap<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select id,username from auth_user");
            set=prepar.executeQuery();
            while(set.next()) {
                studentUserMap.put(set.getString("username"),set.getInt("id"));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }

        return studentUserMap;
    }

}
