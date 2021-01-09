package com.xinxin.eshop.excel.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/base")
public class BaseController {

    @PostMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name){

        return name;
    }
}
