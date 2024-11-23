package ru.netology.cloudstoragediplom.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private String hash;
    private byte[] file;

}
