package com.style.approval.main.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/main")
public class MainWebController {

    @GetMapping
    public String mainView() {
        return "main/main";
    }

}
