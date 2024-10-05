package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedList;
import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     *
     * @param employee
     */
    @Insert("insert into employee (name, username, password, phone, sex, id_number,status, create_time, update_time, create_user, update_user) " +
            "values " +
            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Employee employee);

//    @Select("select count(*) from employee;")
//    public Long count();

//    @Select("select * from employee limit #{page},#{pagesize}")
//    public List<Employee> page(int page, int pagesize);

    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
}
