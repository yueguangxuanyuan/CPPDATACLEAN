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
public class TextInfoCleanUtil {
    public static void main(String args[]) {
       // readRawCommandTextData();
        TextInfoCleanUtil textInfoCleanUtil=new TextInfoCleanUtil();
        textInfoCleanUtil.cleanTextInfo(1,1);
    }


    public void cleanTextInfo(int sid,int eid){
        List<CommandTextModel> commandTextModels=readRawCommandTextData();
        List<TextInfoModel> textInfoModels=new ArrayList<>();
        for(CommandTextModel commandTextModel:commandTextModels){
                TextInfoModel textInfoModel=new TextInfoModel();
                textInfoModel.setEid(eid);
                textInfoModel.setSid(sid);
                textInfoModel.setProjectName(commandTextModel.getProjectName());
                textInfoModel.setContent(commandTextModel.getContent());
                textInfoModel.setFileName(commandTextModel.getName());
                textInfoModel.setTime(commandTextModel.getTime());
                textInfoModel.setFilePath(commandTextModel.getFilePath());
                textInfoModel.setType(commandTextModel.getAction());
                textInfoModels.add(textInfoModel);
        }

        insertTextInfo(textInfoModels);
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
               // System.out.println("获得一条数据： "+set.getString("content"));
                CommandTextModel model=new CommandTextModel();
                model.setContent(set.getString("content"));
                model.setAction(set.getString("action"));
                model.setFilePath(set.getString("path"));
                model.setName(set.getString("name"));
                model.setHappenTime(set.getString("happentime"));
                model.setProjectName(set.getString("project"));
                model.setTime(set.getString("time"));
                list.add(model);
            }
            connection.close();
        } catch (SQLException e) {
            DaoUtil.closeConnection(connection);
            e.printStackTrace();
        }

        return list;
    }

    private void insertTextInfo(List<TextInfoModel> list){
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        try {
            PreparedStatement pstm=connection.prepareStatement("insert into " +
                    "text_info(eid,sid,pid,pname,type,content,time,file_name,file_path) values(?,?,?,?,?,?,?,?,?)");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据

            for (int i = 0; i < list.size(); i++) {
                TextInfoModel model=list.get(i);
                pstm.setInt(1,model.getEid());
                pstm.setInt(2,model.getSid());
                pstm.setInt(3,model.getPid());
                pstm.setString(4,model.getProjectName());
                pstm.setString(5,model.getType());
                pstm.setString(6,model.getContent());
                pstm.setString(7,model.getTime());
                pstm.setString(8,model.getFileName());
                pstm.setString(9,model.getFilePath());
                pstm.addBatch();//准备批量插入
            }
            pstm.executeBatch();//批量插入
           // connection.commit();//别忘记提交
            connection.close();
        } catch (SQLException e) {
            DaoUtil.closeConnection(connection);
            e.printStackTrace();
        }

    }
}










