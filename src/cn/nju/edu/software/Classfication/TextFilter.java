package cn.nju.edu.software.Classfication;

import cn.nju.edu.software.Common.ActionJudger.EditJudger;
import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.serverdb.TextInfoModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zuce wei on 2017/11/19.
 */
public class TextFilter {

    public static void main(String args[]){
        ScoreFilter scoreFilter=new ScoreFilter();
        TextFilter textFilter=new TextFilter();
        List<Student> list=scoreFilter.filter(2,2);
        list=textFilter.filter(2,list,20).get(1);
        for(Student student:list){
            System.out.println(student.toString());
        }
    }

    public List<Integer> filter(int sid,int qid,int longCopyLength){
        List<Integer> list=new ArrayList<>();
        List<TextInfoModel> textInfoModels=getTextInfoModelFromDB(sid,qid,longCopyLength);
        return list;
    }

    public Map<Integer,List<Student>> filter(int qid,List<Student> list,int longCopyLength){
        Map<Integer,List<Student>> map=new HashMap<>();
        List<Student> res=new ArrayList<>();
        List<Student> noCopyStudent=new ArrayList<>();//，没有使用copy的学生
        List<TextInfoModel> textInfoModels;
        for(Student student:list){
            textInfoModels=getTextInfoModelFromDB(student.getStudentId(),qid,longCopyLength);
            boolean isAdd=false;
            if(textInfoModels!=null&&textInfoModels.size()!=0){//存在有长copy的学生
                int externalNum=0;
                for(TextInfoModel textInfoModel:textInfoModels){
                    if(EditJudger.isExternalCopy(textInfoModel)){
                        externalNum++;
                    }
                }
                student.setExternalCopy(externalNum);
                student.setLongCopy(textInfoModels.size());
                res.add(student);
                isAdd=true;
            }
            if(!isAdd){
                student.setExternalCopy(0);
                student.setLongCopy(0);
                noCopyStudent.add(student);
            }
        }
        map.put(1,res);
        map.put(2,noCopyStudent);
        return map;
    }


    //找到PASTE的语句
    public List<TextInfoModel> getTextInfoModelFromDB(int sId,int qId,int longCopyLength){
        List<TextInfoModel> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select *  from text_info where" +
                    " pname=? and sid=? and type='PASTE'");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            prepar.setString(1,"Q"+qId);
            prepar.setInt(2,sId);
            // prepar.setInt(2,pId);
            set=prepar.executeQuery();
            while(set.next()) {
              TextInfoModel model=new TextInfoModel();
              model.setType(set.getString("type"));
              model.setTime(set.getString("time"));
              model.setSid(sId);
              model.setPid(qId);
              model.setContent(set.getString("content"));
              model.setFilePath(set.getString("file_path"));
              model.setFileName(set.getString("file_name"));

              if(model.getContent().length()>longCopyLength){
                  list.add(model);
              }

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
