package cn.nju.edu.software.FeatureExtract;

import cn.nju.edu.software.Common.StringHelper;
import cn.nju.edu.software.Common.UserSet.UserSetHelper;
import cn.nju.edu.software.ConstantConfig;

import java.io.*;
import java.util.*;

public class ExtractUtil {

    public static boolean concatFeature(List<String> studentIdList, String rootPath, String prefix){
        File root = new File(rootPath);

        if(!root.exists()  || root.isFile()){
          return false;
        }

        File[] featureFiles=root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix);
            }
        });

        String outPutFileName = prefix + "-Sum";

        //创建文件
        TreeMap<String,String> idToFeatureMap = new TreeMap<>();
        for(String studentId : studentIdList){
            idToFeatureMap.put(studentId,"");
        }

        //创建表头缓存
        ArrayList<String> tableHeaderList = new ArrayList<>();
        tableHeaderList.add("studentId");
        ArrayList<String> tableHeaderTypeList = new ArrayList<>();
        tableHeaderTypeList.add("String");

        List<String> preNullOccupyList = new ArrayList<>();
        for(File targetFeatureFile : featureFiles){
            if(targetFeatureFile.getName().equals(outPutFileName)){
                continue;
            }
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(targetFeatureFile));
                String lineContent = null;


                //读取表头
                lineContent = br.readLine();
                String[] lineContentArray = lineContent.split(FeatureConst.FEATURE_DELIMITER,2);
                tableHeaderList.add(lineContentArray[1]);

                //读取类型行
                lineContent = br.readLine();
                lineContentArray = lineContent.split(FeatureConst.FEATURE_DELIMITER,2);
                tableHeaderTypeList.add(lineContentArray[1]);

                //读取文件内容
                Set<String> studentIds = new HashSet<>();
                studentIds.addAll(studentIdList);

                while ( (lineContent = br.readLine())!=null){
                    lineContentArray = lineContent.split(FeatureConst.FEATURE_DELIMITER,2);
                    String studentId = lineContentArray[0];
                    String featureContent = idToFeatureMap.get(studentId);
                    if(featureContent != null){
                        featureContent += FeatureConst.FEATURE_DELIMITER + lineContentArray[1];
                        idToFeatureMap.put(studentId,featureContent);
                        studentIds.remove(studentId);
                    }else{
                        //出现异常
//                        System.out.println("concatFeaure-异常StudentId-" +targetFeatureFile.getName() + "-"+studentId);
                        //当做正常情况处理，用空值填充
                        featureContent = FeatureConst.FEATURE_DELIMITER+String.join(FeatureConst.FEATURE_DELIMITER,preNullOccupyList);
                        featureContent += FeatureConst.FEATURE_DELIMITER + lineContentArray[1];
                        idToFeatureMap.put(studentId,featureContent);
                        studentIds.remove(studentId);
                    }
                }

                //生成空值站位符
                int featureCount = StringHelper.countSubString(lineContentArray[1],FeatureConst.FEATURE_DELIMITER);
                ArrayList<String> nullOccupyList = new ArrayList<>(featureCount);
                for(int i = 0 ; i <= featureCount ; i++){
                    nullOccupyList.add(FeatureConst.NULL_OCCUPY);
                }
                preNullOccupyList.addAll(nullOccupyList);
                String nullFeatures = String.join(FeatureConst.FEATURE_DELIMITER,nullOccupyList);

                for(String inexistStudentId : studentIds){
                    String featureContent = idToFeatureMap.get(inexistStudentId);
                    featureContent += FeatureConst.FEATURE_DELIMITER + nullFeatures;
                    idToFeatureMap.put(inexistStudentId,featureContent);
                }
                studentIds.clear();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }finally {
                if(br != null){
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //输出到文件
        BufferedWriter fw = null;
        try {
            String outPutFilePath = rootPath + File.separator + outPutFileName;
            File outputFile = new File(outPutFilePath);
            if(!outputFile.exists()){
                outputFile.createNewFile();
            }
            fw = new BufferedWriter(new FileWriter(outputFile));

            fw.write(String.join(FeatureConst.FEATURE_DELIMITER, tableHeaderList) +"\r\n");
            fw.write(String.join(FeatureConst.FEATURE_DELIMITER, tableHeaderTypeList) +"\r\n");
            fw.flush();
            for(String studentId : idToFeatureMap.keySet()){
                fw.write(studentId + idToFeatureMap.get(studentId)+"\r\n");
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

    public static void main(String[] args){
        List<String> studentIds = UserSetHelper.getStudentIdListOfAnExam(46);
        System.out.println(ExtractUtil.concatFeature(studentIds,"E:\\CPP日志\\extract","102"));
    }
}
