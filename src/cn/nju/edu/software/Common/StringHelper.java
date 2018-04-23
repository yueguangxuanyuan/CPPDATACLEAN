package cn.nju.edu.software.Common;

import java.util.Arrays;

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

    public static String arrayToStringWithSmall(int[] data){
        String cacheString = Arrays.toString(data);
        cacheString = cacheString.substring(1,cacheString.length()-1);
        return "("+cacheString+")";
    }

    public static <T> String arrayToStringWithSmall(T[] data){
        String cacheString = Arrays.toString(data);
        cacheString = cacheString.substring(1,cacheString.length()-1);
        return "("+cacheString+")";
    }

    public static void main(String[] args){
        System.out.println(arrayToStringWithSmall(new Integer[]{1,2,3}));
    }
}
