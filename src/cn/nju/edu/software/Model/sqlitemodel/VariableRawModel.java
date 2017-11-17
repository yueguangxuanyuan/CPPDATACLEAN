package cn.nju.edu.software.Model.sqlitemodel;

/**
 * Created by zuce wei on 2017/11/17.
 */
public class VariableRawModel {
    private int id;
    private int debugId;
    private String variableName;
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDebugId() {
        return debugId;
    }

    public void setDebugId(int debugId) {
        this.debugId = debugId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
