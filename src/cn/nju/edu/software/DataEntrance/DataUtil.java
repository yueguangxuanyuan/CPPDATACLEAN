package cn.nju.edu.software.DataEntrance;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.CommitModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;
import com.google.gson.*;

import java.io.*;
import java.math.BigDecimal;
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
                //c.setAutoCommit(false);
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM "+tableName+";");
                if(rs==null){
                    System.out.println("rs 为null");
                }
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
                String sql = "select count(*) from "+tableName+" where id= "+id+";";
                ResultSet rs = s.executeQuery(sql);
                rs.next();
                int isEx = rs.getInt(1);

                if(isEx>=1){
                    rs.close();
                    s.close();
                    c.close();
                    return true;
                }else{
                    rs.close();
                    s.close();
                    c.close();
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            System.out.println("temp base connect error");
        }
        return true;
    }

    private boolean isExistsTemp(String tableName,ResultSet rs){
        Connection c = DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        try {
            //System.out.println(rs.getObject(6).toString());
            ResultSetMetaData rsm = rs.getMetaData();
            int count = rsm.getColumnCount();
            //System.out.println(count);

            if(c!=null){
                //Statement s = c.createStatement();
                String sql = "";
                for(int i=2;i<count;i++){
                    String n = rsm.getColumnName(i);
                    if(n.equals("condition")){
                        n = "`"+n+"`";
                    }
                    sql += n+"= ? and ";
                }
                sql+= rsm.getColumnName(count)+"= ?;";
                String exesql = "select count(*) from "+tableName+" where "+sql;
                //System.out.println(exesql);
                PreparedStatement s = c.prepareStatement(exesql);
                for(int i=2;i<=count;i++){

                    s.setObject(i-1,rs.getObject(i));
                    //System.out.println(i+":"+rs.getObject(i).toString());
                }
                ResultSet trs = s.executeQuery();
                trs.next();
                int isEx = trs.getInt(1);
                if(isEx>0){
                    trs.close();
                    s.close();
                    c.close();
                    return true;
                }else{
                    trs.close();
                    s.close();
                    c.close();
                    return false;
                }
            }

            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public void insertDataFromServerLog(String logFile, int e_id, int s_id){
        try {
            FileReader fr = new FileReader(logFile);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line=br.readLine())!=null){
                //解析每一行
                String[] temp = line.split("::");
                String actionType = temp[2];
                if(actionType.equals("test")){
                    String time = temp[0];
                    int exam_id = Integer.valueOf(temp[4]);
                    int question_id = Integer.valueOf(temp[6]);
                    String test_result = temp[8];//解析json字符串？
                    //插入数据库的测试表
                    JsonObject j =  new JsonParser().parse(test_result).getAsJsonObject();
                    int ac_count = 0;
                    int total_count = 0;
                    String accept_case = "";
                    String wrong_answer = "";
                    if(j.has("WA")) {
                        JsonArray wa_case = j.get("WA").getAsJsonArray();
                        total_count +=wa_case.size();
                        for(int i=0;i<wa_case.size();i++){
                            wrong_answer+=wa_case.get(i).toString();
                        }
                    }
                    if(j.has("AC")) {
                        JsonArray ac_case = j.get("AC").getAsJsonArray();
                        ac_count = ac_case.size();
                        total_count +=ac_case.size();
                        for(int i=0;i<ac_count;i++){
                            accept_case+=ac_case.get(i).toString()+";";
                        }
                    }
                    if(j.has("TIE")) {
                        JsonArray tie_case = j.get("TIE").getAsJsonArray();
                        total_count+=tie_case.size();
                        for(int i=0;i<tie_case.size();i++){
                            wrong_answer+=tie_case.get(i).toString();
                        }
                    }
                    if(j.has("RE")) {
                        JsonArray re_case = j.get("RE").getAsJsonArray();
                        total_count+=re_case.size();
                        for(int i=0;i<re_case.size();i++){
                            wrong_answer+=re_case.get(i).toString();
                        }
                    }
                    double tscore = (double)ac_count/(double)total_count;
                    BigDecimal temps = new BigDecimal(tscore);
                    double score =temps.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    score = score*100;

                    //System.out.println("std:"+s_id+" ac:"+accept_case+" wa:"+wrong_answer+" score:"+score);

                    boolean isExist = false;
                    Connection c = DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
                    if(c!=null){
                        //确保日志不重复
                        try{
                            String sql = "select count(*) from test where test_time = '"+time+"' and eid = '"+exam_id+"' and pid = '"+question_id+"';";
                            Statement s = c.createStatement();
                            ResultSet rs = s.executeQuery(sql);
                            rs.next();
                            if(rs.getInt(1)==1){
                                isExist = true;
                            }
                            rs.close();
                            s.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (!isExist) {
                            try {
                                String sql = "insert into test(eid,sid,pid,score,accept_case,wrong_answer,test_time) values (?,?,?,?,?,?,?)";
                                PreparedStatement s = c.prepareStatement(sql);
                                s.setInt(1, e_id);
                                s.setInt(2, s_id);
                                s.setInt(3, question_id);
                                s.setDouble(4, score);//score
                                s.setString(5, accept_case);//accept_case
                                s.setString(6, wrong_answer);//wrong_case
                                s.setString(7, time);//time
                                s.executeUpdate();
                                s.close();
                                c.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

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
                s.setInt(1,exam_id);
                ResultSet rs = s.executeQuery();
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
        System.out.println("insert into "+tableName +" from "+logdbPath);
        Connection c = DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        Connection sqlitec  = DaoUtil.getSqliteConnection(logdbPath);
        List<String> columnList = new ArrayList<>();
        try{
//            ResultSetMetaData rsm  = rs.getMetaData();
//            int columnCount = rsm.getColumnCount();
            //获取列的名字
            String columnSql = "show columns from "+tableName+";";
            Statement columnS = c.createStatement();
            ResultSet columnRs = columnS.executeQuery(columnSql);
            int columnCount = 0;
            while (columnRs.next()){
                columnCount++;
                columnList.add(columnRs.getString(1));
                //System.out.println(columnRs.getString(1));
            }
            //ResultSet rs = getDataFromLogDB(logdbPath,tableName);
            //Connection sqlitec  = DaoUtil.getSqliteConnection(logdbPath);
            //sqlitec.setAutoCommit(false);
            Statement s = sqlitec.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM "+tableName+";");

            String col = "(";
            String value = "(";
            for (int i = 2; i <= columnCount; i++) {
                String colName = columnList.get(i-1);
                if(colName.equals("condition")){
                    colName = "`"+colName+"`";
                }
                col += colName + ",";
                value += "?,";
            }
            col = col.substring(0, col.length() - 1);
            col = col + ")";
            value = value.substring(0, value.length() - 1);
            value = value + ");";
            String sql = "insert into " + tableName + col + " values" + value;
            PreparedStatement ps = c.prepareStatement(sql);

                while (rs.next()) {
                    //读取数据

                    //Map<String,Object> tmpMap = new HashMap<>();
                    int d_id = rs.getInt("id");
                    boolean isExists = isExistsTemp(tableName,rs);
                    //放进mysql
                    if(!isExists){
                        //插入数据
                            for (int i = 2; i <= columnCount; i++) {
                                ps.setObject(i-1, rs.getObject(i));
                            }
                            ps.addBatch();

                    }else{
                        System.out.println("已经存在");
                    }
                }
            ps.executeBatch();
            ps.close();
            s.close();
            sqlitec.close();
            columnRs.close();
            columnS.close();
            rs.close();
            c.close();
            //System.out.println(tableName + "close");
        }catch (Exception e){

            e.printStackTrace();
        }
        try{
            c.close();
            sqlitec.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
