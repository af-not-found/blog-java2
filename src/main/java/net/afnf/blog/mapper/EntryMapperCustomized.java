package net.afnf.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import net.afnf.blog.bean.EntryCondition;
import net.afnf.blog.bean.NameCountPair;
import net.afnf.blog.domain.Entry;

@Mapper
public interface EntryMapperCustomized extends EntryMapper {

    List<Entry> selectWithCond(EntryCondition param);

    Integer countWithCond(EntryCondition param);

    List<NameCountPair> selectMonthlyCount();

    List<NameCountPair> selectTagCount();
}