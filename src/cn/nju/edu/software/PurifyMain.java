package cn.nju.edu.software;

import cn.nju.edu.software.CleanLogic.PurifyDB;

public class PurifyMain {
    public static void main(String[] args){
        String dbName = "cleandb_exam1";
        int[] exam_ids = {46,47};
        //{46,47}{52,53}{67,68}{72}
        new PurifyDB().purifyDB(dbName,exam_ids);
    }
}
