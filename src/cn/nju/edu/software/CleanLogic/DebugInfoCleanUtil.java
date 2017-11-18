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

    public static void main(String args[]){
        DebugInfoCleanUtil debug=new DebugInfoCleanUtil();
        debug.cleanDebugInfo(1,1);
    }

    public void cleanDebugInfo(int sid, int eid){
        List<String> projectNames= projectName();
        List<DebugModel> list= new ArrayList<>();
        for(String projectName:projectNames){
            DebugModel model=new DebugModel();
            model.setProjectName(projectName);
            model.setSid(sid);
            model.setEid(eid);
            model.setBreakPointNum(breakPointNum(projectName));
            model.setDebugRunNum(debugRunNum(projectName));
            list.add(model);
        }

        inserDebugInfo(list);

    }

    private  int breakPointNum(String projectName){
        int num=0;
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        ResultSet set=null;
        try {
            PreparedStatement prepar=connection.prepareStatement(
                    "select count(*) as num,file from breakpoint " +
                    " where tag is not null group by tag,file,file_line ");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                String file=set.getString("file");
                if(file.contains(projectName)){
                    num=num+set.getInt("num")/2;
                }
            }
            connection.close();
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
                    "select count(*) as num,debug_target from debug_info  where type='run' " +
                            " group by debug_target");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                String file=set.getString("debug_target");
                if(file.contains(projectName)){
                    num=num+set.getInt("num");
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }

    private void inserDebugInfo(List<DebugModel> models){
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        try {
            PreparedStatement pstm=connection.prepareStatement("insert into " +
                    "debug(eid,sid,pid,pname,break_point_num,debug_run_num) values(?,?,?,?,?,?)");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            for(DebugModel model:models){
                pstm.setInt(1,model.getEid());
                pstm.setInt(2, model.getSid());
                pstm.setInt(3,model.getPid());
                pstm.setString(4,model.getProjectName());
                pstm.setInt(5,model.getBreakPointNum());
                pstm.setInt(6,model.getDebugRunNum());
                pstm.addBatch();//准备批量插入
            }
            pstm.executeBatch();//批量插入
            //connection.commit();//别忘记提交
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> projectName(){
        List<String> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        ResultSet set=null;
        try {
            PreparedStatement prepar=connection.prepareStatement("select DISTINCT debug_target from debug_info");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                String target=set.getString("debug_target");
                String str[]=target.split("\\\\");
                String name=str[str.length-1];
              //  System.out.println("获取到的名字："+name+"   n: "+name.split("\\.")[0]);
                list.add(name.split("\\.")[0]);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
