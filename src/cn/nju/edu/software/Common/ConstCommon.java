package cn.nju.edu.software.Common;

import cn.nju.edu.software.ConstantConfig;

public class ConstCommon {
    private ConstCommon(){

    }

    private static ConstCommon constCommon = null;

    public static ConstCommon getInstance(){
        if (constCommon == null) {
            constCommon = new ConstCommon();
        }
        return constCommon;
    }

    String targetFolderName = "";

    public String getTargetFolderName() {
        return targetFolderName;
    }

    public void setTargetFolderName(String mark) {
        targetFolderName = ConstantConfig.ClEAN_TARGET_Folder_PATTERN;
        targetFolderName = targetFolderName.replaceAll("\\$\\{mark\\}",mark);
    }

}
