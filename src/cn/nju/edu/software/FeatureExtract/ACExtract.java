package cn.nju.edu.software.FeatureExtract;

/*
抽取函数的基类
 */
public abstract class ACExtract {

    /*
    按照类命名规则来自定义文件名字
     */
    public String getFileName(){
        return this.getClass().getSimpleName().replace("Extract","");
    }

    /*
    抽取的特征按照CSV的格式的组织
    第一行为数据的表头
    第二行为表头对应的数据类型
    第三行开始为表的数据内容
     */
    public abstract boolean extractToFile(String rootFolderPath,int eid,int qid);
}
