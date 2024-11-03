package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ValidOrderCountDateDTO {
    private LocalDate orderDate;
    private Integer validCount;
}
