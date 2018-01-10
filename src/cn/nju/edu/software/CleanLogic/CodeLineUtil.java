package cn.nju.edu.software.CleanLogic;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.DataEntrance.Zip;
import cn.nju.edu.software.Model.serverdb.CodeLineModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuce wei on 2017/12/17.
 */
public class CodeLineUtil {
    public static final String codeBasePath="D:\\cpp-log2\\07\\";
    public static final String outPath="D:\\cpp-log2\\unZipFile\\";

    public static void main(String args[]) throws IOException {
        CodeLineUtil codeLineUtil=new CodeLineUtil();
        //codeLineUtil.calCodeLine();
        //codeLineUtil.statCodeLines("D:\\cpp-log2\\53_project_ZxBj3Zf\\Q103\\Source Files\\Source.cpp");

        List<CodeLineModel> lineModels=codeLineUtil.readLineFromDB();
//        int i=1;
//        for(CodeLineModel code:lineModels){
//            System.out.println(i+"  --> "+" user_id: "+code.getSid() +code.toString());
//            i++;
//        }
        codeLineUtil.cal(lineModels);
        //String codeSourcePath=Zip.unZip("D:\\cpp-log2\\53_project_ZxBj3Zf.zip","D:\\cpp-log2\\unZipFile\\");
      //  Zip.unZipFiles(new File("D:\\cpp-log2\\53_project_ZxBj3Zf.zip"),"D:\\cpp-log2\\unZipFile\\");
       // System.out.println("21: "+codeSourcePath);
    }

    public  void calCodeLine(){

       // Zip.unZip("D:\\cpp-log2\\53_project_ZxBj3Zf.zip","D:\\cpp-log2\\unZipFile");
       // String descPath=  zipPath.replace(".zip","")+"//";
      //  unZip(zipFile,descPath);
        String codeSourcePath=Zip.unZip("D:\\cpp-log2\\53_project_ZxBj3Zf.zip","D:\\cpp-log2\\unZipFile");
    }

   public void cal(List<CodeLineModel> lineModels){
       // List<CodeLineModel> list=new ArrayList<>();
        for(CodeLineModel model:lineModels){
            String path=model.getProjectPath();
            String str[]=path.split("/");
            String zipName=str[str.length-1];

            String codeSourcePath=Zip.unZip(codeBasePath+zipName,outPath+zipName.substring(0,zipName.length()-4));
            String finalPath=outPath+zipName.substring(0,zipName.length()-4)+"\\"+codeSourcePath;

            List<String> filePaths=new ArrayList<>();

            try {
              filePaths= Zip.unZipFiles(new File(codeBasePath+zipName),"D:\\cpp-log2\\unZipFile\\");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int line=0;
            for(String p:filePaths){
                line=line+statCodeLines(p);
            }
            model.setNum(line);
          model.setPid(102);//第二次考试的试题ID为102
            //insertCodeLine(list);
            //System.out.println(" zipFileName :"+codeBasePath+zipName+"  finalPath :"+finalPath +"  codeLine:"+line);
            //break;
        }

       insertCodeLine(lineModels);
   }


    int statCodeLines(String filePath){
        int num=0;
        //BufferedReader是可以按行读取文件
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        try {
            while((str = bufferedReader.readLine()) != null) {
              //  System.out.println(num+"  <--|-->  "+str);
                num++;
            }
            //close
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return num;
    }

    public List<CodeLineModel> readLineFromDB(){
        List<CodeLineModel> list=new ArrayList<>();

        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.MYSQLPROCESS);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select e1.* from exams_examprojects e1 where \n" +
                    "e1.id in( select max(e2.id) from exams_examprojects  e2\n" +
                    "where e2.exam_id in ('52','53')  \n" +
                    "group by e2.user_id,e2.exam_id) order by user_id;");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                // System.out.println("获得一条数据： "+set.getString("content"));
                CodeLineModel model=new CodeLineModel();
                model.setSid(set.getInt("user_id"));
                //model.setPid(102);
                model.setEid(set.getInt("exam_id"));
                model.setLogPath(set.getString("log"));
                model.setProjectPath(set.getString("project"));
                list.add(model);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }
        return list;
    }


    public void insertCodeLine(List<CodeLineModel> list){
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        PreparedStatement pstm=null;
        try {
            pstm=connection.prepareStatement("insert into " +
                    "code_line(eid,sid,pid,num) values(?,?,?,?)");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据

            for (int i = 0; i < list.size(); i++) {
                CodeLineModel model=list.get(i);
                pstm.setInt(1,model.getEid());
                pstm.setInt(2,model.getSid());
                pstm.setInt(3,model.getPid());
                pstm.setInt(4,model.getNum());
                pstm.addBatch();//准备批量插入
            }
            pstm.executeBatch();//批量插入
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,pstm,set);
        }
    }

}
