package com.xinxin.eshop.excel.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PictureService {

    public void uploadPicture(MultipartFile uploadFile) throws IOException;
}
