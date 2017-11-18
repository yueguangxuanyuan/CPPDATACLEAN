package cn.nju.edu.software.CleanLogic;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.serverdb.Build;
import cn.nju.edu.software.Model.serverdb.BuildInfoDetail;
import cn.nju.edu.software.Model.sqlitemodel.BuildRawInfoModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zuce wei on 2017/11/16.
 */
public class BuildInfoCleanUtil {

    public static void main(String args[]) {
        BuildInfoCleanUtil buildInfoCleanUtil=new BuildInfoCleanUtil();
        buildInfoCleanUtil.cleanBuild(1,1);
    }


    public  void cleanBuild(int sId,int eId){
        List<BuildRawInfoModel> buildRawInfoModels=readRawBuildInfo();
        List<Build> buildList=new ArrayList<>();
        List<BuildInfoDetail> detailList=new ArrayList<>();
        int buildId=maxBuildId();

        for(BuildRawInfoModel buildRawInfoModel:buildRawInfoModels){
            buildId++;
            Build build=new Build();
            String content=buildRawInfoModel.getContent();

            build.setId(buildId);
            build.setSid(sId);
            build.setEid(eId);
            build.setContent(content);
            build.setBegingTime(buildRawInfoModel.getStartTime());
            build.setEndTime(buildRawInfoModel.getEndTime());
            String projectName=projectNameFromContent(content);
            if(projectName==null){
                continue;
            }
            build.setProjectName(projectName);//设置该项目的名称
            build.setResult(buildResultFromContent(content));
            List<BuildInfoDetail> buildInfoDetailsE=buildInfoDetailList(buildId,content,"ERROR");
            if(buildInfoDetailsE!=null&&buildInfoDetailsE.size()!=0){
                detailList.addAll(buildInfoDetailsE);
            }

            List<BuildInfoDetail> buildInfoDetailsW=buildInfoDetailList(buildId,content,"WARNING");
            if(buildInfoDetailsE!=null&&buildInfoDetailsW.size()!=0){
                detailList.addAll(buildInfoDetailsW);
            }
            buildList.add(build);
        }

        insertBuild(buildList);
        insertBuildInfoDetail(detailList);
    }

    private  String projectNameFromContent(String content){
        String project=null;
        Pattern projectNamePattern=Pattern.compile("Q(\\d+).c");
        Matcher m = projectNamePattern.matcher(content);
        while (m.find()) {
            project=m.group();
           // System.out.println("正则表达式获取到的项目名称： "+project);
           break;
        }
        return project;
    }

    private static  List<BuildInfoDetail> buildInfoDetailList(int buildId,String content,String type){
        List<BuildInfoDetail> list=new ArrayList<>();

        Pattern pattern=Pattern.compile(": warning C(\\d+):");
        if("ERROR".equals(type)){
            pattern=Pattern.compile(": error C(\\d+):");
        }
        Matcher m = pattern.matcher(content);
        String line;
        while (m.find()) {
            line=m.group();
            //System.out.println("正则表达式获取到的项目名称： "+line);
            line=line.substring(1,line.length()-1).trim();
            String s[]=line.split(" ");
            BuildInfoDetail buildInfoDetail=new BuildInfoDetail();
            buildInfoDetail.setBuildId(buildId);
            buildInfoDetail.setResultType(type);
            buildInfoDetail.setResultInfo(s[1]);
            list.add(buildInfoDetail);
        }
        return list;
    }

    private static  String buildResultFromContent(String content){
        String result="Failed";
        String line=null;
        Pattern projectNamePattern=Pattern.compile("==========([\\s\\S]*)==========");
        Matcher m = projectNamePattern.matcher(content);
        while (m.find()) {
            line=m.group();
            if(!line.contains("成功0个")||!line.contains("0 succeeded")){
                result="SUCCESS";
            }
            //System.out.println("正则表达式获取到的项目名称： "+line);
            break;
        }
        return result;
    }

   static List<BuildRawInfoModel> readRawBuildInfo(){
        List<BuildRawInfoModel> list=new ArrayList<>();
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.TEMPBASE);
        ResultSet set=null;
        try {
            PreparedStatement prepar=connection.prepareStatement("select * from build_info");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
                //System.out.println("读取到一条BuildInfo的信息");
                BuildRawInfoModel model=new BuildRawInfoModel();
                model.setId(set.getInt("id"));
                model.setTime(set.getString("time"));
                model.setStartTime(set.getString("buildstarttime"));
                model.setEndTime(set.getString("buildendtime"));
                model.setSolutionName(set.getString("solutionname"));
                model.setContent(set.getString("content"));
                list.add(model);
            }
            connection.close();
        } catch (SQLException e) {
            DaoUtil.closeConnection(connection);
            e.printStackTrace();
        }
        return list;
    }

    //插入每一条编译结果的详情。
    static void insertBuildInfoDetail(List<BuildInfoDetail> list){
      // System.out.println("插入一条信息："+list.size());
            Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
           // ResultSet set=null;
            try {
                PreparedStatement pstm=connection.prepareStatement("insert into " +
                        "build_info(result_type,build_id,detail,result_info) values(?,?,?,?)");
                //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据

                for (int i = 0; i < list.size(); i++) {
                    BuildInfoDetail model=list.get(i);
                    pstm.setString(1,model.getResultType());
                    pstm.setInt(2,model.getBuildId());
                    pstm.setString(3,model.getDetail());
                    pstm.setString(4,model.getResultInfo());
                    pstm.addBatch();//准备批量插入
                }
                pstm.executeBatch();//批量插入
               // connection.commit();//别忘记提交
                connection.close();
            } catch (SQLException e) {
                DaoUtil.closeConnection(connection);
                e.printStackTrace();
            }
    }

    //插入每一次build的结果
    public static void insertBuild(List<Build> list){
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        try {
            PreparedStatement pstm=connection.prepareStatement("insert into " +
                    "build(eid,sid,pid,pname,result,content,begintime,endtime) values(?,?,?,?,?,?,?,?)");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据

            for (int i = 0; i < list.size(); i++) {
                Build model=list.get(i);
                pstm.setInt(1,model.getEid());
                pstm.setInt(2,model.getSid());
                pstm.setInt(3,model.getPid());
                pstm.setString(4,model.getProjectName());
                pstm.setString(5,model.getResult());
                pstm.setString(6,model.getContent());
                pstm.setString(7,model.getBegingTime());
                pstm.setString(8,model.getEndTime());
                pstm.addBatch();//准备批量插入
            }
            pstm.executeBatch();//批量插入
            //connection.commit();//别忘记提交
            connection.close();
        } catch (SQLException e) {
            DaoUtil.closeConnection(connection);
            e.printStackTrace();
        }
    }

    //获取build的最大id值
    private int maxBuildId(){
        int res=0;
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        try {
            PreparedStatement prepar=connection.prepareStatement("select max(id) as maxId from build");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            set=prepar.executeQuery();
            while(set.next()) {
               res=set.getInt("maxId");
               break;
            }
            connection.close();
        } catch (SQLException e) {
            DaoUtil.closeConnection(connection);
            e.printStackTrace();
        }
        return res;
    }

}
