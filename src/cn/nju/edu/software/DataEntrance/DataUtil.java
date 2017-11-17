package cn.nju.edu.software.DataEntrance;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.CommitModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 2017/11/14.
 */
public class DataUtil {
    public ResultSet getDataFromLogDB(String dbPath,String tableName){
        Connection c = DaoUtil.getSqliteConnection(dbPath);

        if(c!=null){
            //得到logdb库中的数据
            try {
                c.setAutoCommit(false);
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM "+tableName+";");
                s.close();
                c.close();
                return rs;
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            System.out.println("cannot connect!");
        }
        return null;
    }
    private boolean isExistsTemp(String tableName,int id){
        Connection c = DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        if(c!=null){
            try {
                Statement s = c.createStatement();
                String sql = "select isnull((select top(1) 1 from"+ tableName +"where id = "+id+"), 0)";
                int isEx = s.executeQuery(sql).getInt(1);
                if(isEx==1){
                    return true;
                }else{
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean isExistsTemp(String tableName,ResultSet rs){
        try {
            ResultSetMetaData rsm = rs.getMetaData();
            int count = rsm.getColumnCount();
            Connection c = DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
            if(c!=null){
                Statement s = c.createStatement();
                String sql = "";
                for(int i=1;i<count;i++){
                    sql += rsm.getColumnName(i)+"='"+rs.getObject(i).toString()+"'and";
                }
                sql+= rsm.getColumnName(count)+"='"+rs.getObject(count).toString()+"';";
                String exesql = "select count(*) from "+tableName+" where "+sql;
                int isEx = s.executeQuery(exesql).getInt(1);
                if(isEx==1){
                    return true;
                }else{
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
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
        String sql = "SELECT * FROM cpp_test_server.exams_examprojects where exam_id = ? and has_monitor = 1 order by user_id;";
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
                rs.close();
                s.close();
                c.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            System.out.println("connect database error.");
        }
        return res;
    }

    public void cleanTempDatabase(){
        String sql = "TRUNCATE TABLE build_info;\n" +
                "TRUNCATE TABLE build_project_info;\n" +
                "TRUNCATE TABLE command_text;\n" +
                "TRUNCATE TABLE command_file;\n" +
                "TRUNCATE TABLE content_info;\n" +
                "TRUNCATE TABLE debug_info;\n" +
                "TRUNCATE TABLE debug_break;\n" +
                "TRUNCATE TABLE breakpoint;\n" +
                "TRUNCATE TABLE debug_run;\n" +
                "TRUNCATE TABLE exception;\n" +
                "TRUNCATE TABLE debug_exception_thrown;\n" +
                "TRUNCATE TABLE debug_exception_not_handled;\n" +
                "TRUNCATE TABLE local_variable;\n" +
                "TRUNCATE TABLE breakpoint_event;\n" +
                "TRUNCATE TABLE solution_open_event;\n" +
                "TRUNCATE TABLE file_event;";
        Connection c = DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        DaoUtil.executeSql(c,sql);
    }

    public void insertToTempDatabase(String logdbPath,String tableName){
        Connection c = DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        try{
            ResultSet rs = getDataFromLogDB(logdbPath,tableName);
            ResultSetMetaData rsm  = rs.getMetaData();
            int columnCount = rsm.getColumnCount();
                while (rs.next()) {
                    //读取数据
                    //Map<String,Object> tmpMap = new HashMap<>();
                    int d_id = rs.getInt("id");
                    boolean isExists = isExistsTemp(tableName,d_id);
                    //放进mysql
                    if(!isExists){
                        //插入数据
                        boolean isEx = isExistsTemp(tableName,rs);
                        if(!isEx) {
                            String col = "(";
                            String value = "(";
                            for (int i = 1; i <= columnCount; i++) {
                                String colName = rsm.getColumnLabel(i);
                                col += colName + ",";
                                value += "?,";
                            }
                            col = col.substring(0, col.length() - 1);
                            col = col + ")";
                            value = value.substring(0, value.length() - 1);
                            value = value + ");";
                            String sql = "insert into " + tableName + col + " values" + value;
                            PreparedStatement ps = c.prepareStatement(sql);
                            for (int i = 1; i <= columnCount; i++) {
                                ps.setString(i, rs.getObject(i).toString());
                            }
                            ps.executeUpdate();
                            ps.close();
                        }
                    }
                }
            rs.close();
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
