package cn.nju.edu.software;

import cn.nju.edu.software.CleanLogic.PurifyDB;

public class PurifyMain {
    public static void main(String[] args){
        String dbName = "exam4_purify";
        int[] exam_ids = {72};

        new PurifyDB().purifyDB(dbName,exam_ids);
    }
}
