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
    public  void doClean(List<Integer> examid ){
        for(int i:examid) {
            System.out.println("EXAM "+i+" : start clean data");
            List<CommitModel> commitList = dataUtil.getCommitHistory(i);
            anayzeCommit(commitList);
        }
    }
    public void anayzeCommit(List<CommitModel> commitList){
        for(CommitModel c:commitList){
            String _logP = c.getLog();
            String _monitorP = c.getMonitor();
            Timestamp createTime = c.getCreate_time();
            int user_id = c.getUser_id();
            //解析log的path
            String[] logPList = _logP.split("/");
            String logP = ConstantConfig.LOGPATH+logPList[3]+"/"+logPList[4]+"/"+logPList[5];
            //解析monitor的path
            String[] monitorPList = _monitorP.split("/");
            String monitorP = ConstantConfig.MONITORPATH+monitorPList[3]+"/"+monitorPList[4]+"/"+monitorPList[5];
            //解压log压缩包和monitor压缩包
            File logFile = new File(logP);
            File monitorFile = new File(monitorP);
            zipUtil.unzipFile(logFile,ConstantConfig.LOGUNZIPPATH);
            zipUtil.unzipFile(monitorFile,ConstantConfig.MONITORUNZIPPATH);
        }
    }
}
