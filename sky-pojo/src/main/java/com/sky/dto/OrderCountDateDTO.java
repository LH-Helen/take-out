package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderCountDateDTO {
    private LocalDate orderDate;
    private Integer orderCount;
}
