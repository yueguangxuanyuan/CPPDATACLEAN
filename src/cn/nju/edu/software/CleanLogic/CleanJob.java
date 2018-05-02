package cn.nju.edu.software.CleanLogic;

import cn.nju.edu.software.Common.ExamCommon;
import cn.nju.edu.software.Common.LogCommon;
import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.DataEntrance.ZipUtil;
import cn.nju.edu.software.Model.CommitModel;
import cn.nju.edu.software.DataEntrance.DataUtil;
import cn.nju.edu.software.SqlHelp.DaoUtil;
import cn.nju.edu.software.Util.DirUtil;

import java.io.File;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by zr on 2017/11/14.
 */
public class CleanJob {
    DataUtil dataUtil = new DataUtil();
    ZipUtil zipUtil = new ZipUtil();
    int current_id = -1;

    public  void doClean(int[] examid ){
        for(int i:examid) {
            current_id = -1;
            System.out.println("====EXAM "+i+" : start clean data====");

            String current_Exam_Date = dataUtil.getExamDate(i);
            ExamCommon.getInstance().setCurrent_Exam_Date_Standard(current_Exam_Date);
            List<CommitModel> commitList = dataUtil.getCommitHistory(i);

            anayzeCommit(commitList,i);

            System.out.println("====EXAM "+i+" : end clean data====");
        }
    }
    public void anayzeCommit(List<CommitModel> commitList,int e_id){
        final String log_unzip_path = ConstantConfig.LOG_UNZIPPATH;
        final String monitor_unzip_path = ConstantConfig.MONITOR_UNZIPPATH;
        try {
            File log_dir = new File(log_unzip_path);
            log_dir.mkdirs();
            File monitor_dir = new File(monitor_unzip_path);
            monitor_dir.mkdirs();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("创建解压文件夹根路径失败");
            return;
        }

        LocalTime startTime = null;
        LocalTime endTime = null;

        BiFunction<LocalTime,LocalTime,Long> getCleanTimeOfOneStudent = (start,end)->{
            if(start == null || end == null){
                return -1L;
            }
            return Duration.between(start,end).getSeconds();
        };

        for(CommitModel c:commitList){
            String _logP = c.getLog();
            String _monitorP = c.getMonitor();
            int user_id = c.getUser_id();
            //一位同学的数据被清洗完毕
            if((c.getUser_id()!=current_id)&&(current_id!=-1)){
                //从本地临时数据库进行数据分析，结果插入最终的数据库，七个模块
                endClean(current_id);
                //清空数据库
                dataUtil.cleanTempDatabase();
                endTime = LocalTime.now();
                System.out.println("====student :"+current_id+" finish clean :"+getCleanTimeOfOneStudent.apply(startTime,endTime)+"====");
//                return;
            }
            if(current_id != user_id){
                current_id = user_id;
                startTime = LocalTime.now();
                System.out.println("====student :"+current_id+" start clean====");
            }

            //解析log的path
            String[] logPList = _logP.split("/");
            String logP = ConstantConfig.LOGPATH+logPList[5];

            //解析monitor的path
            String[] monitorPList = _monitorP.split("/");
            String monitorP = ConstantConfig.MONITORPATH+monitorPList[5];

            /*
            初始化解压路径
             */
            DirUtil.EmptyDir(ConstantConfig.LOG_UNZIPPATH);
            DirUtil.EmptyDir(ConstantConfig.MONITOR_UNZIPPATH);

            //解压log压缩包和monitor压缩包
            File logFile = new File(logP);
            File monitorFile = new File(monitorP);


            zipUtil.unzipFile(logFile,log_unzip_path);
            zipUtil.unzipLogdbFile(monitorFile,monitor_unzip_path);

            //插入数据库
            String logdb_path = ConstantConfig.MONITOR_UNZIPPATH+ "Dao\\log.db";
            if(new File(logdb_path).exists()){
                dataUtil.ConnectToDatabase(true,false);
                for(String t:ConstantConfig.TABLELIST) {
                    dataUtil.insertToTempDatabase(logdb_path, t);
                }
                dataUtil.closeCon();
                //debug数据插入
                dataUtil.insertDebugToTempDatabase(logdb_path);
            }else{
                System.out.println("日志文件不存在");
            }

            //log的解析..
            String serverlog_dir = ConstantConfig.LOG_UNZIPPATH;
            File[] flist  = new File(serverlog_dir).listFiles();
            if(flist.length == 0){
                System.out.println("日志文件缺失");
            }
            for(File f:flist) {
                dataUtil.insertDataFromServerLog(f.getAbsolutePath(),e_id,user_id);
            }
        }
        //结束最后一个学生的清理
        endClean(current_id);
        dataUtil.cleanTempDatabase();
        endTime = LocalTime.now();
        System.out.println("====student :"+current_id+" finish clean :"+getCleanTimeOfOneStudent.apply(startTime,endTime)+"====");
    }

    //从临时数据库进行清洗的入口函数
    private void endClean(int sid){
        dataUtil.ConnectToDatabase(false,true);
        for(String tableName:ConstantConfig.TABLELIST) {
            dataUtil.moveDataFromTempToPermanentDB(tableName,sid,false);
        }

        for(String tableName:ConstantConfig.DEBUG_TABLELIST) {
            dataUtil.moveDataFromTempToPermanentDB(tableName,sid,true);
        }

        dataUtil.moveDataFromTempToPermanentDB("test_result",sid,true);
        dataUtil.closeCon();
    }
}
