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

public class ExtractBuild extends ACExtract{
    public class StudentBuildModel{
        public StudentBuildModel(String studentId){
            this.studentId = studentId;
        }
        public String studentId;
        public int compileSuccessCount;
        public int compileFailCount;
    }

    /*
    目前由于一场考试只有一条题目 暂时不考虑 多项目的问题
     */
    public void getCompileCountInfo(Map<String,StudentBuildModel> studentBuildModelMap,int qId,int[] eIds){
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            String sql = "SELECT sid,sum(case result when 'SUCCESS' then 1 else 0 end) as success,sum(case result when 'SUCCESS' then 0 else 1 end) as fail FROM build where eid in ${eids} group by sid;";
            sql = sql.replaceAll("\\$\\{eids}", StringHelper.arrayToStringWithSmall(eIds));
            prepar=connection.prepareStatement(sql);
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();

            Map<Integer,String> userStudentMap = UserSetHelper.getUserSutdentMap(eIds);
            while(set.next()) {
                int sid = set.getInt("sid");
                String studentId = userStudentMap.get(sid);
                StudentBuildModel studentBuildModel = studentBuildModelMap.get(studentId);
                if(studentBuildModel != null){
                    studentBuildModel.compileFailCount = set.getInt("fail");
                    studentBuildModel.compileSuccessCount = set.getInt("success");
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
    public boolean extractToFile(String rootFolderPath, int[] eids, int qid) {
        List<String> studentIds = UserSetHelper.getStudentIdListOfAnExam(eids);
        TreeMap<String,StudentBuildModel> studentBuildModelTreeMap = new TreeMap<>();
        for(String studentid : studentIds){
            studentBuildModelTreeMap.put(studentid,new StudentBuildModel(studentid));
        }
        getCompileCountInfo(studentBuildModelTreeMap,qid,eids);

        List<StudentBuildModel> studentBuildModels = new ArrayList<>(studentBuildModelTreeMap.values());
        return ExportHelper.exportToFile(StudentBuildModel.class,studentBuildModels,rootFolderPath,qid,getTagName());
    }

    public static void main(String[] args){
        System.out.println(new ExtractBuild().extractToFile("E:\\CPP日志\\extract",new int[]{52,53},103));
    }
}
