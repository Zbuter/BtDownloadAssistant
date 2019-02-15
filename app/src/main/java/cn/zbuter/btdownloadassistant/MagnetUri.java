package cn.zbuter.btdownloadassistant;

public class MagnetUri{
    private String id;
    private String name;
    private String tips;
    private String magnet;
    private long length;
    private String CreateTime;

    public MagnetUri(String id, String name, String tips, String magnet, long length, String createTime) {
        this.id = id;
        this.name = name;
        this.tips = tips;
        this.magnet = magnet;
        this.length = length;
        CreateTime = createTime;
    }
    public MagnetUri(){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }
}