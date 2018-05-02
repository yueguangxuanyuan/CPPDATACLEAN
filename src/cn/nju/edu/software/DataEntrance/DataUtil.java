package cn.nju.edu.software.DataEntrance;

import cn.nju.edu.software.Common.DBCommon;
import cn.nju.edu.software.Common.ExamCommon;
import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.CommitModel;
import cn.nju.edu.software.Model.DebugInfoUniqueStruct;
import cn.nju.edu.software.Model.TriConsumer;
import cn.nju.edu.software.Model.TriFunction;
import cn.nju.edu.software.SqlHelp.DaoUtil;
import cn.nju.edu.software.Util.TimeUtil;
import com.google.gson.*;
import javafx.beans.binding.ObjectExpression;

import javax.xml.transform.Result;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by zr on 2017/11/14.
 */
public class DataUtil {
    Connection tempbaseCon = null;
    Connection mysqlbaseCon = null;
    Connection persistentDBCon = null;
    public void ConnectToDatabase(boolean needMysql , boolean needPersistent){
        tempbaseCon = DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        if(needMysql){
            mysqlbaseCon = DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
        }
        if(needPersistent){
            persistentDBCon = DaoUtil.getMySqlConnection(DBCommon.getInstance().getTargetDBName());
        }

    }
    public void closeCon(){
        try {
            tempbaseCon.close();
            tempbaseCon = null;
            if(mysqlbaseCon != null){
                mysqlbaseCon.close();
                mysqlbaseCon = null;
            }
            if(persistentDBCon != null){
                persistentDBCon.close();
                persistentDBCon = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


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

    public void insertDataFromServerLog(String logFile, int e_id, int s_id){
        try {
            FileReader fr = new FileReader(logFile);
            BufferedReader br = new BufferedReader(fr);
            String line = null;

            String current_Exam_Date = ExamCommon.getInstance().getCurrent_Exam_Date_Standard();

            ArrayList<String> cashedTimes = new ArrayList<>();
            ArrayList<Double> cashedScores = new ArrayList<>();
            ArrayList<LinkedHashMap> cashedLogs = new ArrayList<>();

            while ((line=br.readLine())!=null){
                //解析每一行
                String[] temp = line.split("::");

                String actionType = temp[2];
                if(!actionType.equals("test")){
                    continue;
                }
                String time = temp[0].trim();
                if(!time.startsWith(current_Exam_Date)){
                    continue;
                }
                int exam_id = Integer.valueOf(temp[4]);
                if(exam_id != e_id){
                    continue;
                }

                String test_result = temp[8];//解析json字符串？
                //插入数据库的测试表
                JsonObject j =  new JsonParser().parse(test_result).getAsJsonObject();
                int ac_count = 0;
                int total_count = 0;
                LinkedHashMap<String,String> loggedCasedMap = new LinkedHashMap<>();
                loggedCasedMap.put("AC","");
                loggedCasedMap.put("WA","");
                loggedCasedMap.put("RE","");
                loggedCasedMap.put("TIE","");
                loggedCasedMap.put("ME","");
                loggedCasedMap.put("SEC","");
                for(String result_type : loggedCasedMap.keySet()){
                    if(j.has(result_type)) {
                        JsonArray json_case = j.get(result_type).getAsJsonArray();
                        total_count +=json_case.size();
                        if(result_type == "AC"){
                            ac_count = json_case.size();
                        }
                        loggedCasedMap.put(result_type,json_case.toString());
                    }
                }

                double tscore = 0;
                if(total_count!=0){
                    tscore= (double)ac_count/(double)total_count;
                }
                BigDecimal temps = new BigDecimal(tscore);
                double score =temps.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                score = score*100;

                cashedTimes.add(time);
                cashedScores.add(score);
                cashedLogs.add(loggedCasedMap);
            }

            Connection c = DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
            if(c!=null){
                c.setAutoCommit(false);
                String sql = "insert ignore into test_result(time,score,ac,wa,re,tie,me,sec) values (?,?,?,?,?,?,?,?)";
                PreparedStatement s = c.prepareStatement(sql);
                for(int i = 0 ; i < cashedTimes.size() ; i++){
                    s.setString(1, cashedTimes.get(i));
                    s.setDouble(2, cashedScores.get(i));//score
                    LinkedHashMap<String,String> loggedCasedMap = cashedLogs.get(i);
                    int baseIndex = 3;
                    for(String result_type : loggedCasedMap.keySet()){
                        s.setString(baseIndex++,loggedCasedMap.get(result_type));
                    }
                    s.addBatch();
                }
                s.executeBatch();
                c.setAutoCommit(true);
                s.close();
                c.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //获取服务器数据库的提交记录
    public List<CommitModel> getCommitHistory(int exam_id){
        List<CommitModel> res = new ArrayList<>();
        String sql = "SELECT * FROM exams_examprojects where exam_id = ? and has_monitor = 1 order by user_id,id;";
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

    /*
    初始化TempDatabae：
    一方面保证数据库存在(如果没有则创建新的
    另外一方面保证数据库被清空（如有存在则清空
     */
    public void initTempDatabase(){
        //确保数据库存在
        DaoUtil.ExecuteSQLScript("",ConstantConfig.TEMPDB_INIT_SQLFILE,null);

        cleanTempDatabase();
    }

    public void cleanTempDatabase(){
        DaoUtil.ExecuteSQLScript(ConstantConfig.TEMPBASE,ConstantConfig.TEMPDB_CLEAN_SQLFILE,null);
    }

    public void initTargetDatabase(String mark){
        //确保数据库存在
        HashMap<String,String> scriptParams = new HashMap<>();
        scriptParams.put("mark",mark);
        DaoUtil.ExecuteSQLScript("",ConstantConfig.TARGETDB_INIT_SQLFILE,scriptParams);
        cleanTargetDatabase(mark);
    }

    public void cleanTargetDatabase(String mark){
        HashMap<String,String> scriptParams = new HashMap<>();
        scriptParams.put("mark",mark);
        DaoUtil.ExecuteSQLScript(ConstantConfig.TEMPBASE,ConstantConfig.TARGETDB_CLEAN_SQLFILE,scriptParams);
    }

    public void insertToTempDatabase(String logdbPath,String tableName){
        Connection sqlitec  = DaoUtil.getSqliteConnection(logdbPath);
        List<String> columnList = new ArrayList<>();
        try{
            //获取列的名字
            String columnSql = "show columns from "+tableName+";";
            Statement columnS = tempbaseCon.createStatement();
            ResultSet columnRs = columnS.executeQuery(columnSql);
            int columnCount = 0;
            while (columnRs.next()){
                columnCount++;
                columnList.add(columnRs.getString(1));
            }
            columnRs.close();
            columnS.close();

            String col ="(";
            String value = "(";
            for (int i = 1; i < columnCount; i++) {
                String colName = columnList.get(i);
                col += "`"+colName + "`,";
                value += "?,";
            }
            col = col.substring(0, col.length() - 1);
            col = col + ")";
            value = value.substring(0, value.length() - 1);
            value = value + ");";
            String sql = "insert ignore into " + tableName + col + " values" + value;

            Statement s = sqlitec.createStatement();
            String current_Exam_Date = ExamCommon.getInstance().getCurrent_Exam_Date();
            ResultSet rs = s.executeQuery("SELECT * FROM "+tableName+" where `time` like \""+current_Exam_Date+"%\" ;");
            PreparedStatement ps = tempbaseCon.prepareStatement(sql);
            while (rs.next()) {
                //读取数据
                for (int i = 2; i <= columnCount; i++) {
                    ps.setObject(i - 1, rs.getObject(i));
                }
                ps.addBatch();

            }
            tempbaseCon.setAutoCommit(false);
            ps.executeBatch();
            tempbaseCon.commit();
            tempbaseCon.setAutoCommit(true);

            ps.close();
            s.close();
            rs.close();
            sqlitec.close();

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    public String getExamDate(int inExamId){
        String outExamDate = "";
        String sql = "SELECT end_time FROM cpp_test_server.exams_exam  where id =?;";
        Connection c = null;
        PreparedStatement s = null;
        ResultSet rs = null;
        try {
            c = DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
            s = c.prepareStatement(sql);
            s.setInt(1,inExamId);
            rs = s.executeQuery();
            if(rs.next()){
                String exam_end_time = rs.getString("end_time");
                Matcher matcher = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})").matcher(exam_end_time);
                if(matcher.find()){
                    outExamDate =  matcher.group();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(c,s,rs);
        }
        return outExamDate;
    }

    /*
    将数据从temp数据库导入到持久化数据库
     */
    public void moveDataFromTempToPermanentDB(String tableName,int user_id,boolean needIdCol){
        try{
            List<String> columnList = new ArrayList<>();
            //获取列的名字
            String columnSql = "show columns from "+tableName+";";
            Statement columnS = tempbaseCon.createStatement();
            ResultSet columnRs = columnS.executeQuery(columnSql);
            int columnCount = 0;
            while (columnRs.next()){
                columnCount++;
                columnList.add(columnRs.getString(1));
            }
            columnRs.close();
            columnS.close();

            String col ="(user_id,";
            String value = "(?,";
            for (int i = needIdCol?0:1; i < columnCount; i++) {
                String colName = columnList.get(i);
                col += "`"+colName + "`,";
                value += "?,";
            }
            col = col.substring(0, col.length() - 1);
            col = col + ")";
            value = value.substring(0, value.length() - 1);
            value = value + ");";
            String sql = "insert ignore into " + tableName + col + " values" + value;

            Statement s = tempbaseCon.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM "+tableName+";");

            PreparedStatement ps = persistentDBCon.prepareStatement(sql);
            int baseOffset = needIdCol?2:1;
            while (rs.next()) {
                ps.setInt(1,user_id);
                //读取数据
                for (int i = needIdCol?0:1; i < columnCount; i++) {
                    if(columnList.get(i).contains("time") &&  !columnList.get(i).equals("happentime")){
                        String timeStr = rs.getString(i+1);
                        timeStr = TimeUtil.timeFormat(timeStr);
                        ps.setString(i+baseOffset,timeStr);
                    }else{
                        ps.setObject(i+baseOffset , rs.getObject(i+1));
                    }
                }
                ps.addBatch();

            }
            tempbaseCon.setAutoCommit(false);
            ps.executeBatch();
            tempbaseCon.commit();
            tempbaseCon.setAutoCommit(true);

            rs.close();
            s.close();

            ps.close();
        }catch (Exception e){

            e.printStackTrace();
        }
    }

    public void insertDebugToTempDatabase(String logdbPath){
        Connection sqlitec  = DaoUtil.getSqliteConnection(logdbPath);
        try {
            String exam_date = ExamCommon.getInstance().getCurrent_Exam_Date();
            String sql = "select tmp3.*,debug_exception_thrown.exception_id as exception_thrown from\n" +
                    "    (select tmp2.*,debug_exception_not_handled.exception_id as not_handled from\n" +
                    "        (select tmp1.*,debug_run.run_type,debug_run.breakpoint_last_hit as run_breakpoint from \n" +
                    "           (select tmp0.*,debug_break.break_reason,debug_break.breakpoint_last_hit as break_breakpoint from\n" +
                    "                (select * from\n" +
                    "                    debug_info\n" +
                    "                    where debug_info.timestamp like \""+exam_date+"%\") as tmp0\n" +
                    "                left join\n" +
                    "                debug_break\n" +
                    "                on\n" +
                    "                tmp0.id = debug_break.id) as tmp1 \n" +
                    "            left join\n" +
                    "            debug_run\n" +
                    "            on\n" +
                    "            tmp1.id = debug_run.id) as tmp2\n" +
                    "        left join\n" +
                    "        debug_exception_not_handled\n" +
                    "        on\n" +
                    "        tmp2.id = debug_exception_not_handled.id) as tmp3\n" +
                    "    left join\n" +
                    "    debug_exception_thrown\n" +
                    "    on tmp3.id = debug_exception_thrown.id";

            Statement statement = sqlitec.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            ResultSetMetaData resultSetMetaData  = rs.getMetaData();

            List<String> colsToInsert = new ArrayList<>();
            TriFunction<String,ResultSetMetaData,List<String>,String> createInsertSqlFunction = (tableName, metaData, colNames)->{
                String colParams = "(";
                String valueParams = "(";
                try {
                    for(int i = 1 ; i <= metaData.getColumnCount() ; i++){
                        String colName = metaData.getColumnName(i);
                        if(colName.equals("id")){
                            continue;
                        }
                        colNames.add(colName);
                        if(colParams.length()>1){
                            colParams += ",";
                            valueParams += ",";
                        }
                        colParams += "`"+colName+"`";
                        valueParams += "?";
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                colParams +=")";
                valueParams += ")";

                String insertSql = "insert ignore into "+tableName+" " + colParams + " values " + valueParams;
                return insertSql;
            };

            String insertSql = createInsertSqlFunction.apply("debug_info",resultSetMetaData,colsToInsert);

            TreeSet<Integer> breakPointSet = new TreeSet<>();
            TreeSet<Integer> exceptionSet = new TreeSet<>();

            List<String> breakPointColNames = Arrays.asList("break_breakpoint","run_breakpoint");
            List<String> exceptionColNames = Arrays.asList("not_handled","exception_thrown");

            HashSet<DebugInfoUniqueStruct> uniqueStructsConstraint = new HashSet<>();
            TriConsumer<List<String>,Set<Integer>,ResultSet> cacheValueConsumer = (colNameList, valueSet,resultSet)->{
                try {
                    for(String colName:colNameList){
                        Object value = resultSet.getObject(colName);
                        if(value == null){
                            continue;
                        }
                        valueSet.add((Integer) value);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            };
            while(rs.next()){
                //跳过一些重复的列
                boolean needInsert = uniqueStructsConstraint.add(new DebugInfoUniqueStruct(rs.getString("timestamp"),rs.getString("type")));
                if(!needInsert){
                    return;
                }
                cacheValueConsumer.apply(breakPointColNames,breakPointSet,rs);
                cacheValueConsumer.apply(exceptionColNames,exceptionSet,rs);
            }
            uniqueStructsConstraint.clear();
            rs.close();

            Connection tmpDBCon = DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);

            TriConsumer<String,Set<Integer>,Map<Integer,Integer>>  valueConsumer = (tableName,idSet,idMap)->{
                if(idSet.size()<=0){
                    return;
                }
                StringBuffer selectSql = new StringBuffer("select * from "+tableName +" where id in (");
                idSet.forEach(n->{
                    selectSql.append(n);
                    selectSql.append(",");
                });
                selectSql.replace(selectSql.length()-1,selectSql.length(),") order by id");

                try {
                    Statement selectStatement = sqlitec.createStatement();
                    ResultSet selectResult = selectStatement.executeQuery(selectSql.toString());
                    List<String> colsForInsert = new ArrayList<>();
                    String valueConsumerInsertSql = createInsertSqlFunction.apply(tableName,selectResult.getMetaData(),colsForInsert);

                    PreparedStatement  vc_preparedStatement = tmpDBCon.prepareStatement(valueConsumerInsertSql,Statement.RETURN_GENERATED_KEYS);
                    while(selectResult.next()){
                        IntStream.range(0,colsForInsert.size()).forEach(n->{
                            try {
                                vc_preparedStatement.setObject(n+1,selectResult.getObject(colsForInsert.get(n)));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        vc_preparedStatement.addBatch();
                    }
                    tmpDBCon.setAutoCommit(false);
                    vc_preparedStatement.executeBatch();
                    tmpDBCon.commit();
                    tmpDBCon.setAutoCommit(true);
                    ResultSet keyRS = vc_preparedStatement.getGeneratedKeys();
                    List<Integer> idList = new ArrayList<>();
                    while(keyRS.next()){
                        idList.add(keyRS.getInt(1));
                    }
                    int tmpIndex= 0 ;
                    for(int n : idSet){
                        idMap.put(n,idList.get(tmpIndex));
                        tmpIndex++;
                    }
                    keyRS.close();
                    vc_preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            };

             /*
            将对应的breakPoint插入到Temp数据库中
             */
            HashMap<Integer,Integer> breakPointMap = new HashMap<>();
            valueConsumer.apply("breakpoint",breakPointSet,breakPointMap);
            /*
            将对应的exception插入到Temp数据库中
             */
            HashMap<Integer,Integer> exceptionMap = new HashMap<>();
            valueConsumer.apply("exception",exceptionSet,exceptionMap);

            PreparedStatement insertStatement = tmpDBCon.prepareStatement(insertSql);
            //将breakPoint表插入
            rs =  statement.executeQuery(sql);
            while(rs.next()){
                for (int n = 0 ; n < colsToInsert.size() ; n++){
                    String colName = colsToInsert.get(n);
                    try {
                        Object value = rs.getObject(colName);
                        if(breakPointColNames.contains(colName)){
                            if(value != null){
                                value = breakPointMap.get(value);
                            }
                        }
                        if(exceptionColNames.contains(colName)){
                            if(value != null){
                                value = exceptionMap.get(value);
                            }
                        }
                        insertStatement.setObject(n+1,value);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                };
                insertStatement.addBatch();
            }
            tmpDBCon.setAutoCommit(false);
            insertStatement.executeBatch();
            tmpDBCon.commit();
            tmpDBCon.setAutoCommit(true);

            rs.close();
            statement.close();
            sqlitec.close();
            tmpDBCon.close();
        }catch (Exception e){

            e.printStackTrace();
        }
    }
}
