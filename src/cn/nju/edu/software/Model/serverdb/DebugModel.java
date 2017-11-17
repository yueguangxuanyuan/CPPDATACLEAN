package cn.nju.edu.software.Model.serverdb;

/**
 * Created by zuce wei on 2017/11/17.
 */
public class DebugModel extends BaseModel{
    private int breakPointNum;
    private int debugRunNum;
    private String debugTime;

    public int getBreakPointNum() {
        return breakPointNum;
    }

    public void setBreakPointNum(int breakPointNum) {
        this.breakPointNum = breakPointNum;
    }

    public int getDebugRunNum() {
        return debugRunNum;
    }

    public void setDebugRunNum(int debugRunNum) {
        this.debugRunNum = debugRunNum;
    }

    public String getDebugTime() {
        return debugTime;
    }

    public void setDebugTime(String debugTime) {
        this.debugTime = debugTime;
    }
}
