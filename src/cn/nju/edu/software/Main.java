package cn.nju.edu.software;

import cn.nju.edu.software.CleanLogic.CleanJob;
import cn.nju.edu.software.Common.ConstCommon;
import cn.nju.edu.software.Common.DBCommon;
import cn.nju.edu.software.Common.LogCommon;
import cn.nju.edu.software.DataEntrance.DataDao;
import cn.nju.edu.software.DataEntrance.ZipDao;


import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        LogCommon.getInstance().log(LocalTime.now().toString());
        int[] eid = {72};
        String mark = "final_1";

        //初始化数据
        DBCommon.getInstance().setTargetDBName(mark);
        ConstCommon.getInstance().setTargetFolderName(mark);

        DataDao d = new DataDao();
        d.initTempDatabase();
        d.initTargetDatabase(mark);
        ZipDao.initBuildFileRootFolder();

        System.out.println("====程序初始化成功====");
        LogCommon.getInstance().log(LocalTime.now().toString());

        CleanJob c = new CleanJob();
        c.doClean(eid);
        LogCommon.getInstance().log(LocalTime.now().toString());
    }
}
