package cn.nju.edu.software.Model.serverdb;

/**
 * Created by zuce wei on 2017/11/16.
 */
public class TextInfoModel extends BaseModel{
    private String type;//文本的操作类型，只能为COPY、CUT、PASTE
    private String content;//文本操作的内容
    private String time;              //操作的时间
    private String fileName;
    private String filePath;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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
