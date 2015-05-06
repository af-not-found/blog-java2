package net.afnf.blog.bean;

import java.util.List;

import net.afnf.blog.domain.Comment;

public class CommentList extends PagingList {

    public CommentList(int limit) {
        this.setLimit(limit);
    }

    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
