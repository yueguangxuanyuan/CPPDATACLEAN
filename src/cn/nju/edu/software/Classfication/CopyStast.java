package cn.nju.edu.software.Classfication;


import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CopyStast {
    public static void main(String args[]){
        CopyStast copyStast=new CopyStast();
        List<Student> list= copyStast.readStudentFromDb();

        List<Student> list1=new ArrayList<>(3);
        list1.add(list.get(0));
        list1.add(list.get(1));

        TextFilter textFilter=new TextFilter();

        Map<Integer,List<Student>> map=textFilter.filter(102,list1,40);

        List<Student> list2=map.get(2);
        list2.addAll(map.get(1));
        for(Student student:list2){
            System.out.println("<<<| "+student.toString());
        }


    }

    public List<Student> readStudentFromDb(){
        List<Student> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select *  from studentinexam ");
            // prepar.setInt(2,pId);
            set=prepar.executeQuery();
            while(set.next()) {
                Student model=new Student();
                model.setStudentId(set.getInt("sid"));
                model.setExamId(set.getInt("exam_id"));
                list.add(model);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }
        return list;
    }

}
