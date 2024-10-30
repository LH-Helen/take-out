package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDateDTO {
    private LocalDate createDate;
    private Integer newUser;
}
