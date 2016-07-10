package net.afnf.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import net.afnf.blog.bean.PageParam;
import net.afnf.blog.domain.Comment;

@Mapper
public interface CommentMapperCustomized extends CommentMapper {

    List<Comment> selectWithOffset(PageParam param);
}