package cn.nju.edu.software;

import cn.nju.edu.software.CleanLogic.CleanJob;
import cn.nju.edu.software.DataEntrance.ZipUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        CleanJob c = new CleanJob();
        int[] eid = {36};
        c.doClean(eid);
    }
}
