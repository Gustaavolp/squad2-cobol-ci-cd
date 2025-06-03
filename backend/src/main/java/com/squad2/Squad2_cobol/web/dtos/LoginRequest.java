package com.squad2.Squad2_cobol.web.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}