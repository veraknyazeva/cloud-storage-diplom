package ru.netology.cloudstoragediplom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonErrorResponse {
    private String message;
    private Integer id;
}
