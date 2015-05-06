package net.afnf.blog.bean;

import java.util.List;

import net.afnf.blog.domain.Entry;

public class EntryList extends PagingList {

    public EntryList(int limit) {
        this.setLimit(limit);
    }

    private List<Entry> entries;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

}
