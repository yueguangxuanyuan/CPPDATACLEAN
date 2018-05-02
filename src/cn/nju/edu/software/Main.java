package cn.nju.edu.software;

import cn.nju.edu.software.CleanLogic.CleanJob;
import cn.nju.edu.software.Common.DBCommon;
import cn.nju.edu.software.Common.LogCommon;
import cn.nju.edu.software.DataEntrance.DataUtil;

import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        LogCommon.getInstance().log(LocalTime.now().toString());
        int[] eid = {52,53};
        String mark = "t3";

        //初始化数据
        DBCommon.getInstance().setTargetDBName(mark);
        DataUtil d = new DataUtil();
        d.initTempDatabase();
        d.initTargetDatabase(mark);

        System.out.println("====程序初始化成功====");
        LogCommon.getInstance().log(LocalTime.now().toString());

        CleanJob c = new CleanJob();
        c.doClean(eid);
        LogCommon.getInstance().log(LocalTime.now().toString());
    }
}
