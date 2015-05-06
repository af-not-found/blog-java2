package net.afnf.blog.bean;

public class NameCountPair {

    private String key;
    private int count;
    private String disp;

    public NameCountPair() {

    }

    public NameCountPair(String key, int count, String disp) {
        this.key = key;
        this.count = count;
        this.disp = disp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDisp() {
        return disp;
    }

    public void setDisp(String disp) {
        this.disp = disp;
    }

}
