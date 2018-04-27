package cn.nju.edu.software.Common;

import cn.nju.edu.software.ConstantConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentCommon {
    /*
    学生:返回true  否则返回false
     */
    private static Pattern studentIdPattern = null;
    public static boolean JudgeStudentID(String inStudentID){
        if(studentIdPattern == null){
            studentIdPattern = Pattern.compile(ConstantConfig.STUDENT_ID_PATTERN);
        }
        Matcher matcher = studentIdPattern.matcher(inStudentID);
        return matcher.find();
    }

    /*
    学生:返回true  否则返回false
     */
    private static Set<Integer> specialUserIdSet = null;
    public static boolean JudgeUserID(int inUserID){
        if(specialUserIdSet == null){
            specialUserIdSet = new HashSet<>();
            for(int user_id : ConstantConfig.SPECIAL_USER_ID_LIST){
                specialUserIdSet.add(user_id);
            }
        }
        return !specialUserIdSet.contains(inUserID);
    }

    public static void main(String[] args){
        System.out.println(StudentCommon.JudgeStudentID("12124234"));
        System.out.println(StudentCommon.JudgeStudentID("1212423s4"));
        System.out.println(StudentCommon.JudgeStudentID("wyh"));
        System.out.println(StudentCommon.JudgeStudentID("SC124234"));
        System.out.println(StudentCommon.JudgeStudentID("124234S"));
    }
}
