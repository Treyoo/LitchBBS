package com.litchi.bbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AlphaController {

    @RequestMapping(path = "/alpha")
    @ResponseBody
    public String alpha(){
        return "oops!";
    }
}
