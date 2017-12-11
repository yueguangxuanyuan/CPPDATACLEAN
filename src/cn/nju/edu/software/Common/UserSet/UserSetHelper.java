package cn.nju.edu.software.Common.UserSet;

import cn.nju.edu.software.Classfication.UserStudentMap;
import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserSetHelper {
    /*
    返回User_id
     */
    public static List<Integer> getStudentListOfAnExam(int eId){
        List<Integer> userIds = new ArrayList<>();

        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select distinct user_id from exams_score where exam_id = ? order by user_id asc");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            prepar.setInt(1,eId);
            set=prepar.executeQuery();
            while(set.next()) {
                userIds.add(set.getInt("user_id"));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }
        return userIds;
    }

    /*
    返回 Student id
     */
    public static List<String> getStudentIdListOfAnExam(int eid){
        List<Integer> userIds = getStudentListOfAnExam(eid);

        List<String> studentIds = new ArrayList<>(userIds.size());

        if(userIds.size() > 0){
            Map<Integer,String> userToStudentMap = UserStudentMap.userStudentMap();

            for(int userId : userIds){
                studentIds.add(userToStudentMap.get(userId));
            }
        }

        return studentIds;
    }
}
