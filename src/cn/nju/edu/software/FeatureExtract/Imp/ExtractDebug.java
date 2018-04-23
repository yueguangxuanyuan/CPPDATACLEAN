package cn.nju.edu.software.FeatureExtract.Imp;

import cn.nju.edu.software.Common.StringHelper;
import cn.nju.edu.software.Common.UserSet.UserSetHelper;
import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.FeatureExtract.ACExtract;
import cn.nju.edu.software.FeatureExtract.ExportHelper;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExtractDebug extends ACExtract{
    public class StudentDebugModel{
        public StudentDebugModel(String studentId){
            this.studentId = studentId;
        }
        public String studentId;
        public int debugCount;
    }

    public void getDebugCount(Map<String,StudentDebugModel> studentDebugModelMap,int qId,int[] eIds){
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            String sql = "SELECT sid,count(*) as debug_count FROM debug where eid in ${eids} group by sid;";
            sql = sql.replaceAll("\\$\\{eids}", StringHelper.arrayToStringWithSmall(eIds));
            prepar=connection.prepareStatement(sql);
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            Map<Integer,String> userStudentMap = UserSetHelper.getUserSutdentMap(eIds);
            while(set.next()) {
                int sid = set.getInt("sid");
                String studentId = userStudentMap.get(sid);
                StudentDebugModel studentDebugModel = studentDebugModelMap.get(studentId);
                if(studentDebugModel != null){
                    studentDebugModel.debugCount = set.getInt("debug_count");
                }else{
                    //出现异常
                    System.out.println(getTagName()+"-学生id缺失-"+studentId);
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }
    }
    @Override
    public boolean extractToFile(String rootFolderPath, int[] eids,int qid) {
        List<String> studentIds = UserSetHelper.getStudentIdListOfAnExam(eids);
        TreeMap<String,StudentDebugModel> studentDebugModelTreeMap = new TreeMap<>();
        for(String studentid : studentIds){
            studentDebugModelTreeMap.put(studentid,new StudentDebugModel(studentid));
        }
        getDebugCount(studentDebugModelTreeMap,qid,eids);

        List<StudentDebugModel> studentDebugModels = new ArrayList<>(studentDebugModelTreeMap.values());
        return ExportHelper.exportToFile(StudentDebugModel.class,studentDebugModels,rootFolderPath,qid,getTagName());
    }

    public static void main(String[] args){
        System.out.println(new ExtractDebug().extractToFile("E:\\CPP日志\\extract",new int[]{52,53},103));
    }
}
