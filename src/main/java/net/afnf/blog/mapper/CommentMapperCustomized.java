package net.afnf.blog.mapper;

import java.util.List;

import net.afnf.blog.bean.PageParam;
import net.afnf.blog.domain.Comment;

public interface CommentMapperCustomized extends CommentMapper {

    List<Comment> selectWithOffset(PageParam param);
}