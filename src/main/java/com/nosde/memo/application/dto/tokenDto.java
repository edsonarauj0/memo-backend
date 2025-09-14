package com.nosde.memo.application.dto;

import lombok.Data;

@Data
public class tokenDto {
    private String token;

    public tokenDto(String token) {
        this.token = token;
    }
}
