package org.example.controller;

import groovy.util.logging.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }
}
