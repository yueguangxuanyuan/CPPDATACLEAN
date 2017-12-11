package cn.nju.edu.software.FeatureExtract.Imp;

import cn.nju.edu.software.Classfication.UserStudentMap;
import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.FeatureExtract.ACExtract;
import cn.nju.edu.software.FeatureExtract.ExportHelper;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtractScore extends ACExtract {
    public class StudentModel{
        public String studentId;
        public double score;
        public int scoreCount;
    }

    private List<StudentModel> statisticScore(int eId,int pId){
        List<StudentModel> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;

        Map<Integer,String> userStudentMap = UserStudentMap.userStudentMap();
        try {
            prepar=connection.prepareStatement("select DISTINCT user_id,max(score) as score,count(*) as scoreCount from exams_score where" +
                    " exam_id=? and question_id=? group by exam_id,user_id");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            prepar.setInt(1,eId);
            prepar.setInt(2,pId);
            set=prepar.executeQuery();
            while(set.next()) {
                StudentModel student=new StudentModel();
                int userId = set.getInt("user_id");
                student.studentId = userStudentMap.get(userId);
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
    public boolean extractToFile(String rootFolderPath, int eid,int qid) {
        List<StudentModel> studentInfos = statisticScore(eid,qid);
        String fileName = rootFolderPath + File.separator + qid+"-"+getFileName();
        return ExportHelper.exportToFile(StudentModel.class,fileName,studentInfos);
    }

    public static void main(String[] args){
        System.out.println(new ExtractScore().extractToFile("E:\\CPP日志\\extract",46,102));
    }
}
