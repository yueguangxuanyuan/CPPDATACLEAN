package cn.nju.edu.software.Common.UserSet;

import cn.nju.edu.software.Common.StringHelper;
import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.*;
import java.util.*;

public class UserSetHelper {
    private static Object lock = new Object();

    private static  Set<Integer> recordedEaxms = new HashSet<>();
    public static Map<Integer,String> userStudentMap = new HashMap<>();

    private static void loadData(int[] eIds){
         boolean isSame = true;
         if(eIds.length == recordedEaxms.size()){
             for(Integer id : eIds){
                 if(!recordedEaxms.contains(id)){
                     isSame = false;
                     break;
                 }
             }
         }else{
             isSame = false;
         }

         if(isSame){
             return;
         }

         recordedEaxms.clear();
         userStudentMap.clear();

        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            String sql = "select distinct sid,student_id from studentinexam where exam_id in ${eids} order by sid asc";
            sql = sql.replaceAll("\\$\\{eids}",StringHelper.arrayToStringWithSmall(eIds));
            prepar=connection.prepareStatement(sql);
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                userStudentMap.put(set.getInt("sid"),set.getString("student_id"));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }

        for(Integer id : eIds){
            recordedEaxms.add(id);
        }
        return;
    }

    public static Map<Integer,String> getUserSutdentMap(int[] eIds){
        synchronized (lock){
            loadData(eIds);
            return new HashMap<>(userStudentMap);
        }
    }

    /*
    返回User_id
     */
    public static List<Integer> getStudentListOfAnExam(int[] eIds){
        synchronized (lock){
            loadData(eIds);
            List<Integer> userIds = new ArrayList<>(userStudentMap.keySet());
            return userIds;
        }
    }

    /*
    返回 Student id
     */
    public static List<String> getStudentIdListOfAnExam(int[] eIds){
        synchronized (lock){
            loadData(eIds);
            List<String> studentIds = new ArrayList<>(userStudentMap.values());
            return studentIds;
        }
    }

    public static void main(String[] args) {
//        System.out.println(getStudentIdListOfAnExam(new int[]{52,53}).size());
        Map<Integer,String> theMap = UserSetHelper.getUserSutdentMap(new int[]{52});
        System.out.println(theMap.size());
        theMap.clear();
        System.out.println(UserSetHelper.userStudentMap.size());
    }
}
