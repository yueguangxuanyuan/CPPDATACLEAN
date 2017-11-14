package cn.nju.edu.software.Model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by zr on 2017/11/14.
 */
public class CommitModel {
    private String log;
    private int user_id;
    private String monitor;
    private Timestamp create_time;
public CommitModel(String l,int u,String m,Timestamp t){
    log = l;
    user_id = u;
    monitor = m;
    create_time = t;
}
    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }
}
