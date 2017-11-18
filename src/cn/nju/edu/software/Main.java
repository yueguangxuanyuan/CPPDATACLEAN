package cn.nju.edu.software;

import cn.nju.edu.software.DataEntrance.ZipUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File f = new File("D:\\CPP日志\\monitor-all\\11\\14\\44_monitor.zip");
        ZipUtil z = new ZipUtil();
        z.unzipLogdbFile(f,"D:\\CPP日志\\unzip\\");

//        String json = "{'1':'1'}";
//        JsonObject t = new JsonParser().parse(json).getAsJsonObject();
//        if(!t.has("2")){
//            System.out.print("ss");
//        }
    }
}
