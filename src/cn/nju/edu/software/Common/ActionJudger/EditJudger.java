package cn.nju.edu.software.Common.ActionJudger;

import cn.nju.edu.software.ConstantConfig;
import cn.nju.edu.software.Model.serverdb.TextInfoModel;
import cn.nju.edu.software.SqlHelp.DaoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditJudger {
    public static boolean isExternalCopy(TextInfoModel model){
        boolean res=true;
        Connection connection= DaoUtil.getMySqlConnection(ConstantConfig.CLEANBASE);
        ResultSet set=null;
        PreparedStatement prepar=null;
        try {
            prepar=connection.prepareStatement("select *  from text_info where" +
                    " pname=? and sid=? and type in ('CUT','COPY') and content=?");
            //把sql语句发送到数据库，得到预编译类的对象，这句话是选择该student表里的所有数据
            prepar.setString(1,"Q"+model.getPid());
            prepar.setInt(2,model.getSid());
            prepar.setString(3,model.getContent());
            // prepar.setString(4,model.getTime());
            // prepar.setInt(2,pId);
            set=prepar.executeQuery();
            while(set.next()) {
                res=false;
                break;
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DaoUtil.closeConnection(connection,prepar,set);
        }
        return res;
    }
}
