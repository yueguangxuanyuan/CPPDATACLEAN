package cn.nju.edu.software.DataEntrance;

import cn.nju.edu.software.Common.ConstCommon;
import cn.nju.edu.software.Util.DirUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by zr on 2017/11/14.
 */
public class ZipDao {
    public static void initBuildFileRootFolder(){
        String rootPath = ConstCommon.getInstance().getTargetFolderName();
        File buildRoot = new File(rootPath);
        if(!buildRoot.exists()){
            buildRoot.mkdirs();
        }else if(buildRoot.isFile()){
            DirUtil.EmptyDir(rootPath);
            buildRoot.delete();
            buildRoot.mkdirs();
        }else {
            DirUtil.EmptyDir(rootPath);
        }
    }

    public static boolean unzipFile(File zipFile, String descDir) {
        ZipFile zf = null;
        try {
            zf = new ZipFile(zipFile);
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(descDir + entry.getName());
                if (!outFile.exists()) {
                    outFile.createNewFile();
                }
                BufferedInputStream bis = new BufferedInputStream(zf.getInputStream(entry));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
                byte[] b = new byte[100];
                while (true) {
                    int len = bis.read(b);
                    if (len == -1)
                        break;
                    bos.write(b, 0, len);
                }
                // close stream
                bis.close();
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean unzipLogdbFile(File zipFile, String descDir) {
        ZipFile zf = null;
        try{
            zf = new ZipFile(zipFile);
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry = null;
            while((entry=zis.getNextEntry())!=null){
                if(entry.getName().equals("Dao\\log.db")||entry.getName().equals("Dao\\log.db-shm")||entry.getName().equals("Dao\\log.db-wal")) {
                    File outFile = new File(descDir + entry.getName());
                    if (!outFile.getParentFile().exists()) {
                        outFile.getParentFile().mkdir();
                    }
                    if (!outFile.exists()) {
                        outFile.createNewFile();
                    }
                    BufferedInputStream bis = new BufferedInputStream(zf.getInputStream(entry));
                    BufferedOutputStream bos = null;
                    bos = new BufferedOutputStream(new FileOutputStream(outFile));

                    byte[] b = new byte[100];
                    while (true) {
                        int len = bis.read(b);
                        if (len == -1)
                            break;
                        bos.write(b, 0, len);
                    }
                    // close stream
                    bis.close();
                    bos.close();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            if(zf != null){
                try {
                    zf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
