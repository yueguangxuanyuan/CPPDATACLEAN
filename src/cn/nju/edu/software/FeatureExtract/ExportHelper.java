package cn.nju.edu.software.FeatureExtract;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ExportHelper{

    public static <StudentModel>  boolean exportToFile(Class modelClass,String fileName, List<StudentModel> studentInfos){
        File file = new File(fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            Field[] fields = modelClass.getFields();
            List<String> stringList = new ArrayList<>(fields.length);

            for(Field field : fields){
                stringList.add(field.getName());
            }
            fw.append(String.join(",",stringList) + "\r\n");

            stringList.clear();
            for(Field field : fields){
                stringList.add(field.getType().getSimpleName());
            }
            fw.append(String.join(",",stringList) + "\r\n");

            for(StudentModel studentModel : studentInfos){
                stringList.clear();
                for(Field field : fields){
                    try {
                        stringList.add(String.valueOf(field.get(studentModel)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                fw.append(String.join(FeatureConst.FEATURE_DELIMITER,stringList) + "\r\n");
            }

            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if(fw != null){
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
