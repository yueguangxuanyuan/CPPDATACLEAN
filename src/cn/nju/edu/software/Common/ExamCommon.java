package cn.nju.edu.software.Common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExamCommon {
    private ExamCommon(){

    }

    private static ExamCommon examCommon = null;

    public static ExamCommon getInstance(){
        if(examCommon == null){
            examCommon = new ExamCommon();
        }
        return examCommon;
    }

    private String current_Exam_Date = "";  //sql.db 采用的格式  2016/7/7
    private String current_Exam_Date_Standard = "";//标准格式  2016/06/06

    public String getCurrent_Exam_Date_Standard() {
        return current_Exam_Date_Standard;
    }

    public String getCurrent_Exam_Date() {
        return current_Exam_Date;
    }

    public void setCurrent_Exam_Date_Standard(String current_Exam_Date_Standard) {
        this.current_Exam_Date_Standard = current_Exam_Date_Standard;

        if(current_Exam_Date_Standard.length() > 2){
            Matcher matcher = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})").matcher(current_Exam_Date_Standard);
            if(matcher.find()){
                this.current_Exam_Date =  matcher.group(1)+"/"+Integer.parseInt(matcher.group(2))+"/"+Integer.parseInt(matcher.group(3));
            }
        }else{
            this.current_Exam_Date = "";
        }
    }
}
