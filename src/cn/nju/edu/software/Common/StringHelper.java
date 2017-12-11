package cn.nju.edu.software.Common;

public class StringHelper {
    public static int countSubString(String str,String substr)
    {
        if(substr.length() < 0){
            return 0;
        }
        int index=0;
        int count=0;
        int fromindex=0;
        while((index=str.indexOf(substr,fromindex))!=-1)
        {
            fromindex=index+substr.length();
            count++;
        }
        return count;
    }
}
