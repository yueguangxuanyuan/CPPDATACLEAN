package cn.nju.edu.software.DataEntrance;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.CommitModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 2017/11/14.
 */
public class DataUtil {
    public void getDataFromLogDB(String dbPath){
        Connection c = DaoUtil.getSqliteConnection(dbPath);
        if(c!=null){
            //得到logdb库中的数据
        }else{
            System.out.println("cannot connect!");
        }
    }
    public void getDataFromServerLog(String logFile){
        try {
            FileReader fr = new FileReader(logFile);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line=br.readLine())!=null){
                //解析每一行
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //获取服务器数据库的提交记录
    public List<CommitModel> getCommitHistory(int exam_id){
        List<CommitModel> res = new ArrayList<>();
        String sql = "SELECT * FROM cpp_test_server.exams_examprojects where exam_id = ? and has_monitor = 1;";
        PreparedStatement s = null;
        Connection c = DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
        if(c!=null){
            try {
                s = c.prepareStatement(sql);
                s.setString(1,String.valueOf(exam_id));
                ResultSet rs = s.executeQuery(sql);
                while(rs.next()){
                    CommitModel temp = new CommitModel(rs.getString("log"),rs.getInt("user_id"),rs.getString("monitor"),rs.getTimestamp("create_time"));
                    res.add(temp);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            System.out.println("connect database error.");
        }
        return res;
    }
}
