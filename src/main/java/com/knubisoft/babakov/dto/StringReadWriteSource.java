package com.knubisoft.babakov.dto;

import lombok.Data;

@Data
public class StringReadWriteSource implements DataReadWriteSource{
    private String content;
}
