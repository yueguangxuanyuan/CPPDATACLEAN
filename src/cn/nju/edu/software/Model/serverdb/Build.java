package cn.nju.edu.software.Model.serverdb;

/**
 * Created by zuce wei on 2017/11/17.
 */
public class Build extends BaseModel{
    private String result;
    private String content;
    private String begingTime;
    private String endTime;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBegingTime() {
        return begingTime;
    }

    public void setBegingTime(String begingTime) {
        this.begingTime = begingTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
