package com.t2m.g2nee.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class testController {

    @GetMapping("/test")
    public String hello(){

        return "Hello Test!";
    }
}
