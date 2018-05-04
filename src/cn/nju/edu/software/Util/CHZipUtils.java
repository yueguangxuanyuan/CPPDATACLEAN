package cn.nju.edu.software.Util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


public class CHZipUtils {
    /**使用GBK编码可以避免压缩中文文件名乱码*/
    private static final Charset CHINESE_CHARSET = Charset.forName("GBK");
    /**文件读取缓冲区大小*/
    private static final int CACHE_SIZE = 1024;

    /**
     * 压缩文件
     * @param sourceFolder 压缩文件夹
     * @param zipFilePath 压缩文件输出路径
     */
    public static void zip(String sourceFolder, String zipFilePath) {
        OutputStream os = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zos = null;
        try {
            os = new FileOutputStream(zipFilePath);
            bos = new BufferedOutputStream(os);
            // 解决中文文件名乱码
            zos = new ZipOutputStream(bos, Charset.forName("GBK"));
            File file = new File(sourceFolder);
            String basePath = null;
            if (file.isDirectory()) {
                basePath = file.getPath(); //将此抽象路径名转换为路径名字符串
            } else {
                basePath = file.getParent();  //得到文件路径
            }
            zipFile(file, basePath, zos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 递归压缩文件
     * @param parentFile    需压缩的文件
     * @param basePath      压缩文件所在路径
     * @param zos
     * @throws Exception
     */
    private static void zipFile(File parentFile, String basePath, ZipOutputStream zos) throws Exception {
        File[] files = new File[0];
        if (parentFile.isDirectory()) {
            //返回文件夹中的文件列表
            files = parentFile.listFiles();
        } else {  //压缩一个文件的情况下
            files = new File[1];
            files[0] = parentFile;
        }
        String pathName;
        InputStream is;
        BufferedInputStream bis;
        byte[] cache = new byte[CACHE_SIZE];
        for (File file : files) {
            if (file.isDirectory()) {
                //文件夹 再次遍历里面的内容
                pathName = file.getPath().substring(basePath.length() + 1) + File.separator;
                zos.putNextEntry(new ZipEntry(pathName));

                zipFile(file, basePath, zos);
            } else {
                pathName = file.getPath().substring(basePath.length() + 1);  //得到文件名
                is = new FileInputStream(file);
                bis = new BufferedInputStream(is);
                zos.putNextEntry(new ZipEntry(pathName));
                int nRead = 0;
                String content=null;
                //bis.read 返回-1表示已经读到文件尾
                /*bis.read(cache, 0, CACHE_SIZE)和bis.read(cache)
                    都是将缓存数据读到字节数组中，read返回zero则是没读完，返回-1读取完闭。
                    缓存中的数据读完之后 ，就会 释放。*/
                //将字符读入数组   bis.read(目的缓存区，开始存储的字节偏移量，要读取的最大字节数)
                while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                    content+=new String(cache, 0, nRead );
                    zos.write(cache, 0, nRead);
                }
                bis.close();
                is.close();
            }
        }
    }

    /**
     * 解压压缩包
     * @param zipFilePath 压缩文件路径
     * @param destDir 解压目录
     * pathIdentity 限制文件的路径 需要包含的关键字
     */
    public static void unZip(String zipFilePath, String destDir,String pathIdentity) {
        ZipFile zipFile = null;  //需解压的压缩文件
        try {
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            zipFile = new ZipFile(zipFilePath, CHINESE_CHARSET);
            //以压缩文件中显示顺序将所有文件返回
            Enumeration<ZipEntry> zipEntries = (Enumeration<ZipEntry>) zipFile.entries();
            File file, parentFile;
            ZipEntry entry;  //需解压的对象
            byte[] cache = new byte[CACHE_SIZE];
            while (zipEntries.hasMoreElements()) {  //压缩包内是否包含多个元素，至少包含一个对象时返回true，否则返回false
                //zipEntries.nextElement()如果该枚举对象至少有一个元素可提供，则返回该枚举的下一个元素
                entry = (ZipEntry) zipEntries.nextElement();

                if(pathIdentity != null){
                    if(!entry.getName().contains(pathIdentity)){
                        continue;
                    }
                }

                if (entry.getName().endsWith(File.separator)) {
                    new File(destDir + entry.getName()).mkdirs();
                    continue;
                }
                bis = new BufferedInputStream(zipFile.getInputStream(entry));
                file = new File(destDir + entry.getName());  //创建解压文件
                parentFile = file.getParentFile();
                if (parentFile != null && (!parentFile.exists())) {
                    parentFile.mkdirs();
                }
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, CACHE_SIZE);
                int readIndex = 0;

                //写入解压到的文件中
                while ((readIndex = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                    fos.write(cache, 0, readIndex);
                }
                //刷新此缓冲的输出流，保证数据全部都能写出
                bos.flush();
                bos.close();
                fos.close();
                bis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}