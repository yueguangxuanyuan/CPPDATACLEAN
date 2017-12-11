package cn.nju.edu.software;

import cn.nju.edu.software.Classfication.Fileter;
import cn.nju.edu.software.Classfication.Student;
import cn.nju.edu.software.CleanLogic.CleanJob;
import cn.nju.edu.software.DataEntrance.DataUtil;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class Main {
    //数据清理
    public static void cleanData(int[] eid){
        CleanJob c = new CleanJob();
        c.doClean(eid);
        DataUtil d = new DataUtil();
        d.cleanTempDatabase();
    }

    //过滤特征数据集，并写入文件
    public static void filerDataAndWriteToFile(int eid,int[] qidList,String timeType,int longCopyNum){
        Fileter f = new Fileter();


        for(int i:qidList) {
            try {
                File file = new File(ConstantConfig.OUTPUT_LOG_ROOT_PATH + "\\result2\\Q" + i+".txt");
                FileWriter fw = new FileWriter(file);
                if (!file.exists()) {
                    try {
                        file.createNewFile();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Map<Integer,List<Student>> m =f.filter(eid,i,timeType,longCopyNum);
                List<Student> l1 = m.get(1);
                List<Student> l2 = m.get(2);

                for (Student s : l1) {
                    fw.append(s.toString() + "\r\n");
                }
                fw.append("====================Gap Line===============" + "\r\n");
                for (Student s : l2) {
                    fw.append(s.toString() + "\r\n");
                }
                fw.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Fileter f = new Fileter();
        Map<Integer,List<Student>> m =f.filter(46,102,"SUM",40);
        List<Student> l1 = m.get(1);
        List<Student> l2 = m.get(2);
        try {
            FileWriter fw = new FileWriter(ConstantConfig.OUTPUT_LOG_ROOT_PATH+"\\Q102.txt");
            for (Student s : l1) {
                fw.append(s.toString()+"\r\n");
            }
            fw.close();
            FileWriter fw2 = new FileWriter(ConstantConfig.OUTPUT_LOG_ROOT_PATH + "\\Q102-GOOD.txt");
            for (Student s : l2) {
                fw2.append(s.toString()+"\r\n");
            }
            fw2.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
