package com.example.mimimimetr.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateCatDto {
    private String name;
    private String photo;
    private MultipartFile file;
}
