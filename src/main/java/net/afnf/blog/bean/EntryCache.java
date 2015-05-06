package net.afnf.blog.bean;

import java.util.List;

import net.afnf.blog.common.IfModifiedSinceFilter;
import net.afnf.blog.domain.Entry;

public class EntryCache {

    private static EntryCache instance = new EntryCache();

    private long updated;
    private int totalNormalCount;
    private List<NameCountPair> tagList;
    private List<NameCountPair> monthlyList;
    private List<Entry> recents;

    public static EntryCache getInstance() {
        return instance;
    }

    public long getLastModified() {
        return IfModifiedSinceFilter.getLastModified();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public int getTotalNormalCount() {
        return totalNormalCount;
    }

    public void setTotalNormalCount(int totalNormalCount) {
        this.totalNormalCount = totalNormalCount;
    }

    public List<NameCountPair> getTagList() {
        return tagList;
    }

    public void setTagList(List<NameCountPair> tagList) {
        this.tagList = tagList;
    }

    public List<NameCountPair> getMonthlyList() {
        return monthlyList;
    }

    public void setMonthlyList(List<NameCountPair> monthlyList) {
        this.monthlyList = monthlyList;
    }

    public List<Entry> getRecents() {
        return recents;
    }

    public void setRecents(List<Entry> recents) {
        this.recents = recents;
    }

}
