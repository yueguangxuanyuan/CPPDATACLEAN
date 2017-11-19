package cn.nju.edu.software.Classfication;

/**
 * Created by zuce wei on 2017/11/19.
 */
public class Student {
    private int questionId;
    private int studentId;
    private int score;
    private int codeTime;
    private int debugTime;
    private int sumTime;
    private int longCopy=0;
    private int externalCopy=0;


    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

    public int getSumTime() {
        return sumTime;
    }

    public void setSumTime(int sumTime) {
        this.sumTime = sumTime;
    }

    public int getLongCopy() {
        return longCopy;
    }

    public void setLongCopy(int longCopy) {
        this.longCopy = longCopy;
    }

    public int getExternalCopy() {
        return externalCopy;
    }

    public void setExternalCopy(int externalCopy) {
        this.externalCopy = externalCopy;
    }

    @Override
    public String toString() {
        return "Student{" +
                "questionId=" + questionId +
                ", studentId=" + studentId +
                ", score=" + score +
                ", codeTime=" + codeTime +
                ", debugTime=" + debugTime +
                ", sumTime=" + sumTime +
                ", longCopy=" + longCopy +
                ", externalCopy=" + externalCopy +
                '}';
    }
}
