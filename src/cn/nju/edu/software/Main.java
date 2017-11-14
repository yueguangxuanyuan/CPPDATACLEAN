package cn.nju.edu.software;

import cn.nju.edu.software.DataEntrance.ZipUtil;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File f = new File("D:\\CPP日志\\log\\10\\23\\38_log.zip");
       ZipUtil.unzipFile(f,"D:\\CPP日志\\unzip\\");
    }
}
