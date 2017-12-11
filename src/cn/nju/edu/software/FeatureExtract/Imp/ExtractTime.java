package cn.nju.edu.software.FeatureExtract.Imp;


import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.FeatureExtract.ACExtract;
import cn.nju.edu.software.FeatureExtract.ExportHelper;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExtractTime extends ACExtract {
    public class StudentModel{
        public String studentId;
        public int codeTime;
        public int debugTime;
        public int sumTime;
    }

    public List<StudentModel> statisticCodeTime(int qId){
        List<StudentModel> students = new ArrayList<>();

        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLVISUA);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select student_id_id as studentId,question_id_id as questionId," +
                    "sum(code_time) as code_time," +
                    "sum(debug_time) as debug_time  from codeanddebugtime where" +
                    " question_id_id=? group by student_id_id,question_id_id");

            prepar.setInt(1,qId);
            set=prepar.executeQuery();
            HashMap<String,StudentModel> studentDataMap = new HashMap<>();

            while(set.next()) {
                String studentId=set.getString("studentId");
                if( !studentDataMap.containsKey(studentId)){
                    StudentModel student = new StudentModel();
                    student.studentId = studentId;
                    student.codeTime = set.getInt("code_time");
                    student.debugTime = set.getInt("debug_time");
                    student.sumTime = student.codeTime + student.debugTime;
                    studentDataMap.put(studentId,student);
                }else{
                    //把当前状况当做异常情况处理 2017-12-10
                    System.out.println(getFileName() + " 异常数据 - 数据条重复 - " + studentId);
                }
            }

            for(StudentModel student : studentDataMap.values()){
                students.add(student);
            }

            studentDataMap.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }

        return students;
    }

    public boolean extractToFile(String rootFolderPath,int eid,int qid) {
        List<StudentModel> studentInfos = statisticCodeTime(qid);

        String fileName = rootFolderPath + File.separator + qid+"-"+getFileName();

        return ExportHelper.exportToFile(StudentModel.class,fileName,studentInfos);
    }

    public static void main(String[] args){

        ACExtract acExtract = new ExtractTime();

        System.out.println(acExtract.extractToFile("E:\\CPP日志\\extract",0,102));
    }
}
