package com.sky.mapper;

import com.sky.dto.UserDateDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * 插入用户
     * @param user
     */
    void insert(User user);

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 根据时间list求用户list
     * @param begin
     * @param end
     * @return
     */
    List<UserDateDTO> getNewUserBydataList(LocalDate begin, LocalDate end);

    /**
     * 获得begin之前的user总人数
     * @param begin
     * @return
     */
    @Select("select count(id) from user where DATE(create_time) < #{begin}")
    Integer getTotalByBeginDate(LocalDate begin);
}
