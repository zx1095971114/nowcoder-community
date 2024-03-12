package com.nowcoder.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("test")
public class TestController {
    @RequestMapping(path = "/abc")
    public String test1(){
        return "test2";
    }

    @RequestMapping("/test1")
    @ResponseBody
    public String test(){
        return "This is the test1.";
    }
}
