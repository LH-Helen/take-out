package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CategoryMapper {

    /**
     * 按照分类名查询
     *
     * @param name
     * @return
     */
    @Select("select * from category where name = #{name}")
    Category getByName(String name);

    /**
     * 插入分类数据
     *
     * @param category
     */
    @Update("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user)" +
            "values " +
            "(#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser} )")
    void insert(Category category);

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
}
