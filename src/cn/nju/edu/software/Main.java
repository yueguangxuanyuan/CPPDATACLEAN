package cn.nju.edu.software;

import cn.nju.edu.software.Classfication.Fileter;
import cn.nju.edu.software.Classfication.Student;
import cn.nju.edu.software.CleanLogic.CleanJob;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
//        CleanJob c = new CleanJob();
//        int[] eid = {46};
//        c.doClean(eid);
//        DataUtil d = new DataUtil();
//        d.cleanTempDatabase();
        Fileter f = new Fileter();
//        int[] qid1 = {2,10,63,9,64,65,66,67,68,69,70,13,71,72,73};
//        int[] qid2 = {75,76,77,85,86,87,88,89,90,91};
//        for(int i:qid2) {
//            try {
//                File file = new File("D:\\CPP日志\\result2\\Q" + i+".txt");
//                FileWriter fw = new FileWriter(file);
//                if (!file.exists()) {
//                    try {
//                        file.createNewFile();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                List<Student> l = f.filter(41, i, "SUM", 40);
//                for (Student s : l) {
//                    fw.append(s.toString() + "\r\n");
//                }
//                fw.close();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        Map<Integer,List<Student>> m =f.filter(46,102,"SUM",40);
        List<Student> l1 = m.get(1);
        List<Student> l2 = m.get(2);
        try {
//            FileWriter fw = new FileWriter("D:\\CPP日志\\Q102.txt");
//            for (Student s : l1) {
//                fw.append(s.toString()+"\r\n");
//            }
//            fw.close();
//            FileWriter fw2 = new FileWriter("D:\\CPP日志\\Q102-GOOD.txt");
//            for (Student s : l2) {
//                fw2.append(s.toString()+"\r\n");
//            }
//            fw2.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
