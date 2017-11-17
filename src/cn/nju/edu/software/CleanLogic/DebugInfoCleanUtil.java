package cn.nju.edu.software.CleanLogic;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.serverdb.DebugModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuce wei on 2017/11/17.
 */
public class DebugInfoCleanUtil {

    public void cleanDebufInfo(int sid,int eid){
        List<String> projectNames=projetcName();
        List<DebugModel> list= new ArrayList<>();
        for(String projectName:projectNames){
            DebugModel model=new DebugModel();
            model.setProjectName(projectName);
            model.setSid(sid);
            model.setBreakPointNum(breakPointNum(projectName));
            model.setDebugRunNum(debugRunNum(projectName));
            list.add(model);
        }
    }

    private  int breakPointNum(String projectName){
        int num=0;
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        ResultSet set=null;
        try {
            PreparedStatement prepar=connection.prepareStatement(
                    "select count(*) as num,file from breakpoint group by tag,file,file_line " +
                    "where tag is not null");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                String file=set.getString("file");
                if(file.contains(projectName)){
                    num=num+set.getInt("num")/2;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }

    private int debugRunNum(String projectName){
        int num=0;
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        ResultSet set=null;
        try {
            PreparedStatement prepar=connection.prepareStatement(
                    "select count(*) as num,tdebug_target from debug_info group by tdebug_target where type=run" +
                            "where tag is not null");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                String file=set.getString("debug_target");
                if(file.contains(projectName)){
                    num=num+set.getInt("num");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }

    private void inserDebugInfo(DebugModel model){
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        ResultSet set=null;
        try {
            PreparedStatement pstm=connection.prepareStatement("insert into " +
                    "(eid,sid,pid,panme,break_point_num,debug_run_num) values(?,?,?,?,?,?)");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据

            pstm.setInt(1,model.getEid());
            pstm.setInt(2, model.getSid());
            pstm.setInt(3,model.getPid());
            pstm.setString(4,model.getProjectName());
            pstm.setInt(5,model.getBreakPointNum());
            pstm.setInt(6,model.getDebugRunNum());

            pstm.execute();
            connection.commit();//别忘记提交
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> projetcName(){
        List<String> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        ResultSet set=null;
        try {
            PreparedStatement prepar=connection.prepareStatement("select DISTINCT debug_target from debug_info");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                list.add(set.getString("debug_target"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
