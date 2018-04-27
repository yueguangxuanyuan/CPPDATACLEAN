package cn.nju.edu.software.Util;

import cn.nju.edu.software.ConstantConfig;

import java.io.*;

/*
文件工具类
 */
public class FileUtil {
    /*
    在文件能够正常打开的情况下返回文件内容，
    在文件不能正常打开的情况下返回空串
     */
    public static String GetFileContent(String inFilePath){
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inFilePath));

            String line = null;
            try {
                while((line = reader.readLine())!= null){
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args){
        System.out.println(FileUtil.GetFileContent(ConstantConfig.TEMPDB_INIT_SQLFILE));
    }
}
