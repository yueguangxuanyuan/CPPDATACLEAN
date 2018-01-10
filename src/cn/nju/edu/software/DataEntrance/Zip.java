package cn.nju.edu.software.DataEntrance;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
                    System.out.println("-------");
                    fOut=new File(Parent,entry.getName());
                    if(!fOut.exists()){
                        (new File(fOut.getParent())).mkdirs();
                    }
                    fileSourcePath=entry.getName();

                    System.out.println(" fileSourcePath: "+fileSourcePath);
                   // System.out.println("   entry.getName() "+entry.getName());
                    FileOutputStream out=new FileOutputStream(fOut);
                    BufferedOutputStream Bout=new BufferedOutputStream(out);
                    int b;
                    while((b=bIn.read())!=-1){
                        Bout.write(b);
                    }
                    Bout.close();
                    out.close();
                    System.out.println(fOut+"解压成功");
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

    @SuppressWarnings("rawtypes")
    public static  List<String> unZipFiles(File zipFile, String descDir) throws IOException {
        List<String> outPathList=new ArrayList<>();

        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));//解决中文文件夹乱码
        String name = zip.getName().substring(zip.getName().lastIndexOf('\\')+1, zip.getName().lastIndexOf('.'));

        File pathFile = new File(descDir+name);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + name +"/"+ zipEntryName).replaceAll("\\*", "/");

            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            // 输出文件路径信息
//          System.out.println(outPath);

            FileOutputStream out = new FileOutputStream(outPath);
           // System.out.println(" out path:"+outPath);
            outPathList.add(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        System.out.println("******************解压完毕********************");
        return outPathList;
    }

}
