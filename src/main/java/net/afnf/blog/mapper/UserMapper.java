package net.afnf.blog.mapper;

import java.util.List;
import net.afnf.blog.domain.User;
import net.afnf.blog.domain.UserExample;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    int countByExample(UserExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    int deleteByExample(UserExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    int insertSelective(User record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    List<User> selectByExample(UserExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    User selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table users
     * @mbggenerated  Tue Dec 31 19:53:25 JST 2013
     */
    int updateByPrimaryKey(User record);
}