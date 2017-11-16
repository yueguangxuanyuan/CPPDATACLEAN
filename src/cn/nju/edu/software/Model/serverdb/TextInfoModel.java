package cn.nju.edu.software.Model.serverdb;

import java.sql.Date;

/**
 * Created by zuce wei on 2017/11/16.
 */
public class TextInfoModel extends BaseModel{
    private String type;//文本的操作类型，只能为COPY、CUT、PASTE
    private String conetnt;//文本操作的内容
    private Date time;              //操作的时间
    private String fileName;
    private String filePath;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConetnt() {
        return conetnt;
    }

    public void setConetnt(String conetnt) {
        this.conetnt = conetnt;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
