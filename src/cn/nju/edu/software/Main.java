package cn.nju.edu.software;

import cn.nju.edu.software.CleanLogic.CleanJob;

public class Main {

    public static void main(String[] args) {
        CleanJob c = new CleanJob();
        int[] eid = {41};
        c.doClean(eid);
//        DataUtil d = new DataUtil();
//        d.cleanTempDatabase();
    }
}
