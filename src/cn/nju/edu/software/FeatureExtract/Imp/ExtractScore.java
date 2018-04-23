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

public class ExtractScore extends ACExtract {
    public class StudentModel{
        public String studentId;
        public double score;
        public int scoreCount;
    }

    private List<StudentModel> statisticScore(int[] eIds,int pId){
        List<StudentModel> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;

        try {
            String sql = "select student_id,max(score) as score,count(*) as scoreCount from exams_score where question_id = ? and exam_id in ${eids} group by student_id";
            sql = sql.replaceAll("\\$\\{eids}", StringHelper.arrayToStringWithSmall(eIds));
            prepar=connection.prepareStatement(sql);
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            prepar.setInt(1,pId);
            set=prepar.executeQuery();

            while(set.next()) {
                StudentModel student=new StudentModel();
                student.studentId = set.getString("student_id");
                student.score = set.getDouble("score")*100;
                student.scoreCount = set.getInt("scoreCount");
                list.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }
        return list;
    }

    @Override
    public boolean extractToFile(String rootFolderPath, int[] eids,int qid) {
        List<StudentModel> studentInfos = statisticScore(eids,qid);
        return ExportHelper.exportToFile(StudentModel.class,studentInfos,rootFolderPath,qid,getTagName());
    }

    public static void main(String[] args){
        System.out.println(new ExtractScore().extractToFile("E:\\CPP日志\\extract",new int[]{52,53},103));
    }
}
