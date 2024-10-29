package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TurnoutDateDTO {
    private LocalDate orderDate;
    private Double totalAmount;
}
