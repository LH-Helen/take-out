package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {

    /**
     * 新增菜品和相对应的口味
     *
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);
}
