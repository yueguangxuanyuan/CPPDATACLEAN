package cn.nju.edu.software.Model.sqlitemodel;

/**
 * Created by zuce wei on 2017/11/16.
 */
public class BuildRawInfoModel {
    private int id;
    private String time;
    private String startTime;
    private String endTime;
    private String solutionName;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public java.lang.String getStartTime() {
        return startTime;
    }

    public void setStartTime(java.lang.String startTime) {
        this.startTime = startTime;
    }

    public java.lang.String getEndTime() {
        return endTime;
    }

    public void setEndTime(java.lang.String endTime) {
        this.endTime = endTime;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
