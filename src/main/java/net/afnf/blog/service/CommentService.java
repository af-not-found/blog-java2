package net.afnf.blog.service;

import java.util.List;

import net.afnf.blog.bean.CommentList;
import net.afnf.blog.bean.PageParam;
import net.afnf.blog.common.IfModifiedSinceFilter;
import net.afnf.blog.domain.Comment;
import net.afnf.blog.domain.CommentExample;
import net.afnf.blog.mapper.CommentMapperCustomized;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    public enum CommentState {
        WAIT, NORMAL, DELETE
    }

    final private static int LIMIT = 20;

    @Autowired
    private CommentMapperCustomized cm;

    public CommentList getComments(Integer page) {

        int pageInt = 1, offset = 0, limit = LIMIT;
        if (page != null) {
            limit = LIMIT;
            pageInt = page.intValue();
            offset = Math.max(0, pageInt - 1) * limit;
        }

        PageParam cond = new PageParam(limit, offset);
        CommentList ret = new CommentList(limit);
        ret.setComments(cm.selectWithOffset(cond));
        ret.setTotalCount(cm.countByExample(null));
        ret.setThisPage(pageInt);

        return ret;
    }

    public List<Comment> getEntryComments(int entryid) {
        CommentExample cond = new CommentExample();
        cond.setOrderByClause("id");
        cond.createCriteria().andEntryidEqualTo(entryid).andStateNotEqualTo((short) CommentState.DELETE.ordinal());
        List<Comment> comments = cm.selectByExample(cond);
        return comments;
    }

    public void registerComment(Comment newComment) {
        cm.insertSelective(newComment);
        IfModifiedSinceFilter.updateLastModified();
    }

    public void updateCommentState(Integer id, Short state) {
        if (id == null || state == null) {
            throw new NullPointerException("id or state is null, id=" + id + ", state=" + state);
        }
        Comment newComment = new Comment();
        newComment.setId(id);
        newComment.setState(state);
        cm.updateByPrimaryKeySelective(newComment);
        IfModifiedSinceFilter.updateLastModified();
    }
}
