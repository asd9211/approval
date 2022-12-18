package com.style.approval.auth.service;

import com.style.approval.auth.dto.SignupDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Test
    @DisplayName("회원가입")
    void 회원가입() {
        //given
        SignupDto.Request signupDto = new SignupDto.Request().builder()
                .email("test@naver.com")
                .password("1234")
                .username("test")
                .role("USER")
                .build();

        //when
        boolean result = authService.signup(signupDto);

        //then
        Assertions.assertEquals(true, result);
    }

}