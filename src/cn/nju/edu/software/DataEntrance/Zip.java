package cn.nju.edu.software.DataEntrance;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by zuce wei on 2017/12/17.
 */
public class Zip {
    public static String unZip(String filePath,String desc){
        String fileSourcePath=null;
        if(!filePath.contains(".zip")){
            return null;
        }
        String path[]=filePath.split("\\.");
       // createDir(path[0]);
        //desc=path[0];
        long startTime=System.currentTimeMillis();
        try {
            ZipInputStream zIn=new ZipInputStream(new FileInputStream(filePath));//输入源zip路径
            BufferedInputStream bIn=new BufferedInputStream(zIn);
            String Parent=desc; //输出路径（文件夹目录）
            File fOut=null;
            ZipEntry entry;
            try {
                while((entry = zIn.getNextEntry())!=null && !entry.isDirectory()){
                    fOut=new File(Parent,entry.getName());
                    if(!fOut.exists()){
                        (new File(fOut.getParent())).mkdirs();
                    }
                    fileSourcePath=entry.getName();
                   // System.out.println("   entry.getName() "+entry.getName());
                    FileOutputStream out=new FileOutputStream(fOut);
                    BufferedOutputStream Bout=new BufferedOutputStream(out);
                    int b;
                    while((b=bIn.read())!=-1){
                        Bout.write(b);
                    }
                    Bout.close();
                    out.close();
                    //System.out.println(fOut+"解压成功");
                }
                bIn.close();
                zIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long endTime=System.currentTimeMillis();
        System.out.println("耗费时间： "+(endTime-startTime)+" ms");
        return fileSourcePath;
    }

    public static  boolean createDir(String destDirName){
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }
}
