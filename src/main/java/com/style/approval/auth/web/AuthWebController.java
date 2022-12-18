package com.style.approval.auth.web;

import com.style.approval.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthWebController {

    private final AuthService authService;


    @GetMapping("/signup/page")
    public String signupView() {
        return "auth/signup";
    }

    @GetMapping("/login/page")
    public String loginView(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "exception", required = false) String exception,
                            Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "auth/login";
    }

}
