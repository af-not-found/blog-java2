package net.afnf.blog.bean;

public class EntryCondition extends PageParam {

    protected String tag;
    protected String month;
    protected boolean filter;

    public EntryCondition() {
    }

    public EntryCondition(String tag, String month, int limit, int offset, boolean filter) {
        this.tag = tag;
        this.month = month;
        this.limit = limit;
        this.offset = offset;
        this.filter = filter;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }
}
