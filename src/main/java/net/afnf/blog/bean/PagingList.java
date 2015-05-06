package net.afnf.blog.bean;

public abstract class PagingList {

    private int limit;
    protected int totalCount;
    protected int thisPage;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPageCount() {
        return limit == -1 ? 1 : 1 + ((getTotalCount() - 1) / limit);
    }

    public int getThisPage() {
        return thisPage;
    }

    public void setThisPage(int thisPage) {
        this.thisPage = thisPage;
    }

}
