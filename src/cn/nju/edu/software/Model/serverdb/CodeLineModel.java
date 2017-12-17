package cn.nju.edu.software.Model.serverdb;

/**
 * Created by zuce wei on 2017/12/17.
 */
public class CodeLineModel {
    private int id;
    private int sid;
    private int eid;
    private int pid;
    private int num;//代码行数

    private String projectPath;
    private String logPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }


    @Override
    public String toString() {
        return "CodeLineModel{" +
                "id=" + id +
                ", sid=" + sid +
                ", eid=" + eid +
                ", pid=" + pid +
                ", num=" + num +
                ", projectPath='" + projectPath + '\'' +
                ", logPath='" + logPath + '\'' +
                '}';
    }
}
