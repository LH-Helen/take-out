package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;

public interface CategoryService {
    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);
}
