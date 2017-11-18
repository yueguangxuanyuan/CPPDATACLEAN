package cn.nju.edu.software.CleanLogic;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.DataEntrance.ZipUtil;
import cn.nju.edu.software.Model.CommitModel;
import cn.nju.edu.software.DataEntrance.DataUtil;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zr on 2017/11/14.
 */
public class CleanJob {
    DataUtil dataUtil = new DataUtil();
    ZipUtil zipUtil = new ZipUtil();
    int current_id = -1;
    BuildInfoCleanUtil buildInfoCleanUtil=new BuildInfoCleanUtil();
    DebugInfoCleanUtil debugInfoCleanUtil=new DebugInfoCleanUtil();
    TextInfoCleanUtil textInfoCleanUtil=new TextInfoCleanUtil();
    public  void doClean(int[] examid ){
        for(int i:examid) {
            current_id = -1;
            System.out.println("EXAM "+i+" : start clean data");
            List<CommitModel> commitList = dataUtil.getCommitHistory(i);
            anayzeCommit(commitList,i);

        }
    }
    public void anayzeCommit(List<CommitModel> commitList,int e_id){

        for(CommitModel c:commitList){
            String _logP = c.getLog();
            String _monitorP = c.getMonitor();
            Timestamp createTime = c.getCreate_time();
            int user_id = c.getUser_id();

            System.out.println("start std:"+user_id);
            //解析log的path
            String[] logPList = _logP.split("/");
            String logP = ConstantConfig.LOGPATH+logPList[3]+"\\"+logPList[4]+"\\"+logPList[5];

            //解析monitor的path
            String[] monitorPList = _monitorP.split("/");
            String monitorP = ConstantConfig.MONITORPATH+monitorPList[3]+"\\"+monitorPList[4]+"\\"+monitorPList[5];

            //解压log压缩包和monitor压缩包
            File logFile = new File(logP);
            File monitorFile = new File(monitorP);

            String log_unzip_path = logP.replace(".zip","")+"\\";
            String monitor_unzip_path = monitorP.replace(".zip","")+"\\";
            //System.out.println(log_unzip_path);
            //System.out.println(monitor_unzip_path);
            try {
                File log_dir = new File(log_unzip_path+"\\");
                log_dir.mkdir();
                File monitor_dir = new File(monitor_unzip_path+"\\");
                monitor_dir.mkdir();
            }catch (Exception e){
                e.printStackTrace();
            }
            zipUtil.unzipFile(logFile,log_unzip_path);
            zipUtil.unzipLogdbFile(monitorFile,monitor_unzip_path);

            //monitor数据logdb的去重
            if((c.getUser_id()!=current_id)&&(current_id!=-1)){


                //从本地临时数据库进行数据分析，结果插入最终的数据库，七个模块
                clean(current_id,e_id);
                //清空数据库
                dataUtil.cleanTempDatabase();
                System.out.println("student :"+current_id+" finish clean");
            }
            //插入数据库
            dataUtil.ConnectToDatabase();

            String logdb_path = monitorP.replace(".zip","")+"\\Dao\\log.db";
            for(String t:ConstantConfig.TABLELIST) {
                dataUtil.insertToTempDatabase(logdb_path, t);
            }
            dataUtil.closeCon();
            /*
            * -------------------------------------------------------数据插入完成---------------------------------------------
            * */
            //log的解析..
            String serverlog_dir = logP.replace(".zip","");
            File[] flist  = new File(serverlog_dir).listFiles();
            for(File f:flist) {
                dataUtil.insertDataFromServerLog(f.getAbsolutePath(),e_id,c.getUser_id());
            }
            //
            current_id = c.getUser_id();
        }
        clean(current_id,e_id);
        dataUtil.cleanTempDatabase();
    }

    //从临时数据库进行清洗的入口函数
    private void clean(int sid,int eid){
        buildInfoCleanUtil.cleanBuild(sid,eid);
        textInfoCleanUtil.cleanTextInfo(sid,eid);
        debugInfoCleanUtil.cleanDebugInfo(sid,eid);
    }
}
