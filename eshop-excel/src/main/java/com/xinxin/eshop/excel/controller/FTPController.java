package com.xinxin.eshop.excel.controller;

import com.xinxin.eshop.excel.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *
 */
@RestController
@RequestMapping("/pic")
public class FTPController {

    @Autowired
    private PictureService pictureService;

    @RequestMapping("/upload")
    public String pictureUpload(@RequestParam(value = "fileUpload") MultipartFile uploadFile){
        String json = "";
        try {
            pictureService.uploadPicture(uploadFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }
}
