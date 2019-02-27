package cn.nju.edu.software.CleanLogic;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.function.BiFunction;

public class PurifyDB {

    private String startTime;
    private String endTime;

    public PurifyDB() {
        startTime = null;
        endTime = null;
    }

    private void getExamDuration(int[] examIds){
        Connection serverConnection = DaoUtil.getMySqlConnection(ConstantConfig.MYSQLBASE);
        PreparedStatement preparedStatement = null;
        ResultSet serverResultset = null;

        try {
            preparedStatement= serverConnection.prepareStatement("SELECT begin_time,end_time FROM exams_exam\n" +
                    "\twhere id =?;");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for(int id : examIds){
                preparedStatement.setInt(1,id);
                serverResultset = preparedStatement.executeQuery();

                serverResultset.next();
                Timestamp beginDBTimestamp = serverResultset.getTimestamp("begin_time");
                Timestamp endDBTimestamp = serverResultset.getTimestamp("end_time");
                serverResultset.close();

                String begin = simpleDateFormat.format(beginDBTimestamp);
                String end = simpleDateFormat.format(endDBTimestamp);
                if(startTime == null || startTime.compareTo(begin) > 0){
                    startTime = begin;
                }
                if(endTime == null || endTime.compareTo(end) < 0){
                    endTime = end;
                }
            }
            //因为服务器上存在时间错位，所以需要增加是时区偏差-8小时
            DateTimeFormatter dateTimeFormatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            BiFunction<String,DateTimeFormatter,String> add8HourFunction= (timeString,theformatter)->{
                LocalDateTime localDateTime = LocalDateTime.parse(timeString,theformatter);
                localDateTime = localDateTime.plusHours(8);
                return localDateTime.format(theformatter);
            };

            startTime = add8HourFunction.apply(startTime,dateTimeFormatter);
            endTime = add8HourFunction.apply(endTime,dateTimeFormatter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DaoUtil.closeConnection(serverConnection,preparedStatement,serverResultset);
    }

    private List<Integer> getStudentIdListWithActionOutOfEaxmTime(){
        Set<Integer> idSet = new TreeSet<>();


        return null;
    }

    private void rmUnReliableRecord(String dbName){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DaoUtil.getMySqlConnection(dbName);
            statement = connection.createStatement();
            String sqlTemplate = "DELETE from ${tableName} where time < \""+startTime+"\" or time > \""+endTime+"\"";

            //常用记录清理
            String[] normalTableNameList = ConstantConfig.TABLELIST;

            for(String tableName : normalTableNameList){
                String sql = sqlTemplate.replaceAll("\\$\\{tableName\\}",tableName);
                statement.executeUpdate(sql);
            }

            String sql = "DELETE from debug_info where timestamp < \""+startTime+"\" or timestamp > \""+endTime+"\"";
            statement.executeUpdate(sql);

            sql = sqlTemplate.replaceAll("\\$\\{tableName\\}","test_result");
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void purifyDB(String dbName,int[] examIds){
        //执行过程
        getExamDuration(examIds);
        rmUnReliableRecord(dbName);
    }
}
