package com.sky.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data
@AllArgsConstructor  // 全参构造
@NoArgsConstructor  // 无参构造
public class PageResult implements Serializable {

    private long total; //总记录数

    private List records; //当前页数据集合

}
