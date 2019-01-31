package cn.zbuter.btdownloadassistant;

import java.util.List;

public class MagnetUri {
    private String Magnet;
    private String name;
    private long length;
    private String create_time;
    private String tips;
    private String id;

    @Override
    public String toString() {
        return "MagnetUri{" +
                "Magnet='" + Magnet + '\'' +
                ", name='" + name + '\'' +
                ", length=" + length +
                ", create_time='" + create_time + '\'' +
                ", tips='" + tips + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getMagnet() {
        return Magnet;
    }

    public void setMagnet(String magnet) {
        Magnet = magnet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }


    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
