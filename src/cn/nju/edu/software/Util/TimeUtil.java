package cn.nju.edu.software.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    private static Pattern timePattern = null;
    public static String timeFormat(String inTimeStr){
        String outTimeStr = "Format_Error";
        if(timePattern == null){
            timePattern = Pattern.compile("(\\d+)\\W(\\d+)\\W(\\d+)[^\\d]+(\\d+):(\\d+):(\\d+)");
        }
        Matcher matcher = timePattern.matcher(inTimeStr);

        if(matcher.find()){
            String year = matcher.group(1);
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            int hour = Integer.parseInt(matcher.group(4));
            int minute = Integer.parseInt(matcher.group(5));
            int second = Integer.parseInt(matcher.group(6));
            outTimeStr = String.format("%s-%02d-%02d %02d:%02d:%02d",year,month,day,hour,minute,second);
        }
        return outTimeStr;
    }

    public static void main(String[] args){
        System.out.println(TimeUtil.timeFormat("2017/12/7 8:33:33"));
        System.out.println(TimeUtil.timeFormat("2017/12/7/周四 8:18:11"));
        System.out.println(TimeUtil.timeFormat("2017/12/7 星期四 上午 9:00:33"));
    }
}
