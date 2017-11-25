package cn.nju.edu.software.Classfication;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zuce wei on 2017/11/19.
 */
public class ScoreFilter {

    public static void main(String args[]){
        ScoreFilter filter=new ScoreFilter();
      // System.out.println("均分 :"+ filter.meanScore(2,2,15));
       List<Student> list=filter.upMeanUserIds(2,2,100);
       for(Student student:list){
           System.out.println(student.toString());
       }
    }

    public List<Student> filter(int eId,int pId){
        double mean =meanScore(eId,pId,50);
        return upMeanUserIds(eId,pId,mean);
    }

    public double meanScore(int eId,int pId,int lastNum){
        double mean=0;
        List<Double> list=new ArrayList<>();

        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select max(score) as score from exams_score where" +
                    " exam_id=? and question_id=? group by exam_id,user_id");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            prepar.setInt(1,eId);
            prepar.setInt(2,pId);
            set=prepar.executeQuery();
            while(set.next()) {
                list.add(set.getDouble("score")*100);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }

        //排序
        Collections.sort(list);
        int count=0;
        double sum=0;
        for(int i=list.size()-1;i>=0;i--){
            System.out.println(" 分数："+list.get(i));
            sum=sum+list.get(i);
            count++;
            if(lastNum==count){
                break;
            }
        }
        if(list.size()==0){
            return 0;
        }else {
            mean=sum/lastNum;
        }
        System.out.println(mean);
        return mean;
    }

    public List<Student> upMeanUserIds(int eId,int pId,double score){
        List<Student> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        score=score/100;//数据库中的每题的满分都是100
        try {
            prepar=connection.prepareStatement("select DISTINCT user_id,max(score) as score from exams_score where" +
                    " exam_id=? and question_id=? group by exam_id,user_id having max(score)>=?");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            prepar.setInt(1,eId);
            prepar.setInt(2,pId);
            prepar.setDouble(3,score);
            set=prepar.executeQuery();
            while(set.next()) {
                Student student=new Student();
                student.setStudentId(set.getInt("user_id"));
                student.setQuestionId(pId);
                student.setScore(set.getDouble("score")*100);
                //System.out.println(" userId: "+set.getInt("user_id"));
                //list.add(set.getInt("user_id"));
                list.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }
        return list;
    }
}
