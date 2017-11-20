package cn.nju.edu.software.Classfication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zuce wei on 2017/11/19.
 */
public class Fileter {
    public Map<Integer,List<Student>> filter(int eid, int qId, String timeType, int longCopyNum){
        List<Student> list=new ArrayList<>();
        ScoreFilter scoreFilter=new ScoreFilter();
        ExerciseTimeFilter exerciseTimeFilter=new ExerciseTimeFilter();
        TextFilter textFilter=new TextFilter();
        list=scoreFilter.filter(eid,qId);
        list=exerciseTimeFilter.filter(eid,qId,timeType,list);
        return  textFilter.filter(qId,list,longCopyNum);
        //return list;
    }
}
