package cn.nju.edu.software.Model.serverdb;

/**
 * Created by zuce wei on 2017/11/19.
 */
public class CodingModel {
    private String studentId;
    private int questionId;
    private int codeTime;
    private int debugTime;
    private int sumTime;

    public int getSumTime() {
        return sumTime;
    }

    public void setSumTime(int sumTime) {
        this.sumTime = sumTime;
    }

    public String getStudentId() {
        return studentId;
    }


    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getCodeTime() {
        return codeTime;
    }

    public void setCodeTime(int codeTime) {
        this.codeTime = codeTime;
    }

    public int getDebugTime() {
        return debugTime;
    }

    public void setDebugTime(int debugTime) {
        this.debugTime = debugTime;
    }
}
