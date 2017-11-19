package cn.nju.edu.software.Classfication;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.serverdb.CodingModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by zuce wei on 2017/11/19.
 */
public class ExerciseTimeFilter {

    public static void main(String arg[]){
        ExerciseTimeFilter filter=new ExerciseTimeFilter();
        ScoreFilter scoreFilter=new ScoreFilter();
        List<Student> list=scoreFilter.filter(2,2);
        list=filter.filter(2,2,"CODING",list);

        for(Student student:list){
            System.out.println(student.toString());
        }
        //filter.codeMeanTime(1,2,15,"DEBUG");
    }

    public List<Student> filter(int eId,int qId,String type,List<Student> list){
        List<Student> students=new ArrayList<>();
       // List<CodingModel> codingModels=statisticCodeTime(qId);
        list= statisticCodeTime(qId,list);
        double mean =codeMeanTime(eId,qId,15,type);
        if("CODING".equals(type)){
            for(Student student:list){
                if(student.getCodeTime()<mean*0.2){
                    students.add(student);
                }
            }
        }

        if("DEBUG".equals(type)){
            for(Student student:list){
                if(student.getDebugTime()<mean*0.2){
                    students.add(student);
                }
            }
        }

        if("SUM".equals(type)){
            for(Student student:list){
                if(student.getSumTime()<mean*0.2){
                    students.add(student);
                }
            }
        }
        return students;
    }

    public double codeMeanTime(int eId,int pId,int lastNum,String type){
        double codeMean=0;
        double debugMean=0;
        List<Double> codeList=new ArrayList<>();
        List<Double> debugList=new ArrayList<>();
        List<Double> sumList=new ArrayList<>();

        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLVISUA);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select sum(code_time) as code_time,sum(debug_time) " +
                    "as debug_time  from codeanddebugtime where" +
                    " question_id_id=? group by student_id_id,question_id_id");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            prepar.setInt(1,pId);
           // prepar.setInt(2,pId);
            set=prepar.executeQuery();
            while(set.next()) {
                codeList.add(set.getDouble("code_time"));
                debugList.add(set.getDouble("debug_time"));
                sumList.add(set.getDouble("code_time")+set.getDouble("debug_time"));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }

        //排序
        Collections.sort(codeList);
        Collections.sort(debugList);
        int count=0;
        double codeNum=0;
        double debugNum=0;
        double towNum=0;
        for(int i=codeList.size()-1;i>=0;i--){
            //System.out.println(" 编码时间 ："+codeList.get(i));
            codeNum=codeNum+codeList.get(i);
            debugNum=debugNum+debugList.get(i);
            towNum=towNum+codeList.get(i)+debugList.get(i);
            count++;
            if(lastNum==count){
                break;
            }
        }
        if(codeList.size()==0){
            return 0;
        }else {
            codeMean=codeNum/lastNum;
            debugMean=debugNum/lastNum;
        }
        if("DEBUG".equals(type)){
            return debugMean;
        }
        if("CODING".equals(type)){
            return codeMean;
        }
         return towNum/lastNum;
    }


    public List<Student> statisticCodeTime(int qId, List<Student> students){
        List<CodingModel> list=new ArrayList<>();

        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLVISUA);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select student_id_id as studentId,question_id_id as questionId," +
                    "sum(code_time) as code_time," +
                    "sum(debug_time) as debug_time  from codeanddebugtime where" +
                    " question_id_id=? group by student_id_id,question_id_id");

            prepar.setInt(1,qId);
            // prepar.setInt(2,pId);
            set=prepar.executeQuery();
            Map<String ,Integer> studentUserMap=UserStudentMap.studentUserMap();
            while(set.next()) {
                System.out.println("--*-*-*-*--*数据库读取一条记录--");
//                CodingModel model=new CodingModel();
//                model.setStudentId(set.getString("studentId"));
//                model.setCodeTime(set.getInt("code_time"));
//                model.setDebugTime(set.getInt("debug_time"));
//                model.setQuestionId(set.getInt("questionId"));
//                model.setSumTime(set.getInt("code_time")+set.getInt("debug_time"));
//                list.add(model);
                String studentId=set.getString("studentId");
                System.out.println(" 查询到的id: "+set.getString("studentId")+"  转换后的ID："+studentUserMap.get(studentId));
                Student student=findStudent(students,studentUserMap.get(studentId));
                if(student!=null){
                    System.out.println("查到 一条记录 ");
                    student.setSumTime(set.getInt("code_time")+set.getInt("debug_time"));
                    student.setCodeTime(set.getInt("code_time"));
                    student.setDebugTime(set.getInt("debug_time"));
                    student.setQuestionId(qId);
                }else {
                    System.out.println("没有查询到记录---");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }

        return students;
    }

    private Student findStudent(List<Student> students,int sId){
        Student student=null;
        for(Student s:students){
            if(sId==s.getStudentId()){
                return s;
            }
        }
        return student;
    }

}
