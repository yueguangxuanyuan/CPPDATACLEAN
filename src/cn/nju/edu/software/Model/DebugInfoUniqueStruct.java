package cn.nju.edu.software.Model;

public class DebugInfoUniqueStruct {
    private String type;
    private String timestamp;

    public DebugInfoUniqueStruct(String type, String timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + timestamp.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebugInfoUniqueStruct that = (DebugInfoUniqueStruct) o;
        return type.equals(that.type)&&timestamp.equals(that.timestamp);
    }
}
