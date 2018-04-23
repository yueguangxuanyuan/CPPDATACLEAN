package cn.nju.edu.software.FeatureExtract.Imp;

import cn.nju.edu.software.Common.ActionJudger.EditJudger;
import cn.nju.edu.software.Common.UserSet.UserSetHelper;
import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.FeatureExtract.ACExtract;
import cn.nju.edu.software.FeatureExtract.ExportHelper;
import cn.nju.edu.software.Model.serverdb.TextInfoModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExtractEdit extends ACExtract{

    public static final int LONG_PASTE_THRESHOLD = 50;

    public class StudentPasteModel{
        public String studentId;
        public int pasteCount;
        public int longPasteCount;
        public int maxPasteLength;
        public int totalPasteLength;
        public int externPasteCount;
    }
    /*
    初次在 102上运行  耗时  2小时45min
     */
    public List<StudentPasteModel> statisticPaste(int qid,List<Integer> userids,int[] eids){
        List<StudentPasteModel> pasteRecords = new ArrayList<>();
        List<TextInfoModel> textInfoModels;
        Map<Integer,String> userStudentMap = UserSetHelper.getUserSutdentMap(eids);

         /*
        获取行号计算进度
         */
        int rowCount = userids.size();
        //显示完成进度，设立target
        int goal = 10;
        float count = 0;

        for(int s_userid:userids){
            StudentPasteModel student = new StudentPasteModel();
            textInfoModels=getTextInfoModelFromDB(s_userid,qid);

            if(textInfoModels!=null){
                int externalNum=0;
                int pasteLength = 0;
                int maxPasteLength = -1;
                int longPasteCount = 0;
                for(TextInfoModel textInfoModel:textInfoModels){
                    if(EditJudger.isExternalCopy(textInfoModel)){
                        externalNum++;
                    }
                    int local_pasteLength = textInfoModel.getContent().length();
                    pasteLength += local_pasteLength;
                    if(local_pasteLength >= LONG_PASTE_THRESHOLD){
                        longPasteCount ++;
                    }
                    maxPasteLength = maxPasteLength>local_pasteLength?maxPasteLength :local_pasteLength;
                }

                student.studentId = userStudentMap.get(s_userid);
                student.externPasteCount = externalNum;
                student.pasteCount = textInfoModels.size();
                student.longPasteCount = longPasteCount;
                student.maxPasteLength = maxPasteLength;
                student.totalPasteLength =pasteLength;

                pasteRecords.add(student);
            }else{
                //认为当前流程是异常流程
                System.out.println(getTagName() + "- paste统计 - 异常userid - "+s_userid);
            }

            count++;
            if(count/rowCount*100 >= goal){
                goal = new Float(count/rowCount*100).intValue();
                System.out.println("已完成："+goal+"%");
                goal+=10;
            }
        }

        return pasteRecords;
    }


    //找到PASTE的语句
    public List<TextInfoModel> getTextInfoModelFromDB(int sId,int qId){
        List<TextInfoModel> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select *  from text_info where" +
                    " pname=? and sid=? and type='PASTE'");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            prepar.setString(1,"Q"+qId);
            prepar.setInt(2,sId);
            // prepar.setInt(2,pId);
            set=prepar.executeQuery();

            while(set.next()) {
                TextInfoModel model=new TextInfoModel();
                model.setType(set.getString("type"));
                model.setTime(set.getString("time"));
                model.setSid(sId);
                model.setPid(qId);
                model.setContent(set.getString("content"));
                model.setFilePath(set.getString("file_path"));
                model.setFileName(set.getString("file_name"));
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

    @Override
    public boolean extractToFile(String rootFolderPath, int[] eids, int qid) {
        List<Integer> attendedUserIds = UserSetHelper.getStudentListOfAnExam(eids);
        List<StudentPasteModel> studentPasteInfos = statisticPaste(qid,attendedUserIds,eids);

        return ExportHelper.exportToFile(StudentPasteModel.class,studentPasteInfos,rootFolderPath,qid,getTagName());
    }


    public static void main(String[] args){
        System.out.println(new ExtractEdit().extractToFile("E:\\CPP日志\\extract",new int[]{52,53},103));
    }
}
