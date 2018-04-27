package cn.nju.edu.software.Common;

import cn.nju.edu.software.ConstantConfig;

public class DBCommon {
    private DBCommon(){

    }

    private static DBCommon dbCommon = null;

    public static DBCommon getInstance(){
        if (dbCommon == null) {
            dbCommon = new DBCommon();
        }
        return dbCommon;
    }

    String targetDBName = "";

    public String getTargetDBName() {
        return targetDBName;
    }

    public void setTargetDBName(String mark) {
        targetDBName = ConstantConfig.ClEAN_TARGET_DB_PATTERN;
        targetDBName = targetDBName.replaceAll("\\$\\{mark\\}",mark);
    }
}
