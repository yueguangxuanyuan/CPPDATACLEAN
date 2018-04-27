package cn.nju.edu.software.Util;

import java.io.File;

public class DirUtil {
    /*
    清空指定文件夹
     */
    public static void EmptyDir(String inDirPath){
        File dir = new File(inDirPath);
        if(!dir.exists()){
            return;
        }

        if(dir.isFile()){
            return;
        }

        EmptyDir(dir);
    }

    private static void EmptyDir(File inDir){
        File[] subFiles = inDir.listFiles();

        for(File file : subFiles){
            if(file.isFile()){
                file.delete();
            }else{
                EmptyDir(file);
                file.delete();
            }
        }
    }

    public static void main(String[] args){
        DirUtil.EmptyDir("E:\\server_data\\tmp\\log\\");
    }
}
