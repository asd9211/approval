package com.style.approval.auth.web;

import com.style.approval.auth.dto.SignupDto;
import com.style.approval.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/signup")
    public boolean signUp(@RequestBody SignupDto.Request request) {
        return authService.signup(request);
    }

}
