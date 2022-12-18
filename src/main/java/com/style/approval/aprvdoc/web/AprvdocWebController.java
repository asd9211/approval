package com.style.approval.aprvdoc.web;

import com.style.approval.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aprvdoc")
public class AprvdocWebController {

    private final AuthService authService;

    @GetMapping("/list/page")
    public String aprvdocView(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("username",userDetails.getUsername());
        return "aprvdoc/aprvdocList";
    }

    @GetMapping("/{docNo}/page")
    public String aprvdocView(@PathVariable("docNo") String docNo, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("username",userDetails.getUsername());
        model.addAttribute("docNo", docNo);
        return "aprvdoc/aprvdocDetail";
    }

    @GetMapping("/regist/page")
    public String aprvdocRegistView(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("username",userDetails.getUsername());
        return "aprvdoc/aprvdocRegist";
    }

}
