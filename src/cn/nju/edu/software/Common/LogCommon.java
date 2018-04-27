package cn.nju.edu.software.Common;

public class LogCommon {
    private static LogCommon logCommon = null;

    private LogCommon(){

    }

    public static LogCommon getInstance(){
        if(logCommon == null){
            logCommon = new LogCommon();
        }
        return logCommon;
    }

    public void log(String msg){
        StackTraceElement[] temp=Thread.currentThread().getStackTrace();
        StackTraceElement parentLayer = null;
        if(temp.length >= 3){
            parentLayer=(StackTraceElement)temp[2];
        }

        String className = "unknown";
        String methodName = "unknown";
        int line = -1;

        if(parentLayer != null){
            className = parentLayer.getClassName();
            methodName = parentLayer.getMethodName();
            line = parentLayer.getLineNumber();
        }
        System.out.println("class:"+className+";\nmethod:"+methodName+";line:"+line+";\nmsg:"+msg);
    }
}
