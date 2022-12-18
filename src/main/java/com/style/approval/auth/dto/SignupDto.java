package com.style.approval.auth.dto;

import lombok.*;

@ToString
public class SignupDto {

    private SignupDto(){}

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private String email;
        private String password;
        private String username;
        private String role;
    }
}
