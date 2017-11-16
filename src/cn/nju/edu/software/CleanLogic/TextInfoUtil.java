package cn.nju.edu.software.CleanLogic;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.sqlitemodel.CommandTextModel;
import cn.nju.edu.software.Model.serverdb.TextInfoModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuce wei on 2017/11/16.
 */
public class TextInfoUtil {
    public static void main(String args[]) {
        readRawCommandTextData();
    }
    public static void cleanTextInfo(){

    }

    //读取原始的数据库的数据
    public static List<CommandTextModel> readRawCommandTextData(){
        List<CommandTextModel> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        ResultSet set=null;
        try {
            PreparedStatement prepar=connection.prepareStatement("select * from command_text where action!='SAVE'");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                System.out.println("获得一条数据： "+set.getString("content"));
                CommandTextModel model=new CommandTextModel();
                model.setContent(set.getString("content"));
                model.setAction(set.getString("action"));
                model.setFilePath(set.getString("path"));
                model.setName(set.getString("name"));
                model.setHappenTime(set.getString("happentime"));
                model.setProjectName(set.getString("project"));
                list.add(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void insertTextInfo(List<TextInfoModel> list){
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        ResultSet set=null;
        try {
            PreparedStatement pstm=connection.prepareStatement("insert into " +
                    "text_info(eid,sid,pid,panme,type,content,time,file_name,file_path) values(?,?,?,?,?,?,?,?,?,?)");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据

            for (int i = 1; i <= list.size(); i++) {
                TextInfoModel model=list.get(i);
                pstm.setInt(1,model.getEid());
                pstm.setInt(2,model.getSid());
                pstm.setInt(3,model.getPid());
                pstm.setString(5,model.getProjectName());
                pstm.setString(6,model.getType());
                pstm.setString(7,model.getConetnt());
                pstm.setDate(8,model.getTime());
                pstm.setString(9,model.getFileName());
                pstm.setString(10,model.getFilePath());
                pstm.addBatch();//准备批量插入
            }
            pstm.executeBatch();//批量插入
            connection.commit();//别忘记提交
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}










