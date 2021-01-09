package com.xinxin.eshop.excel.service.impl;

import com.xinxin.eshop.excel.properties.FTPProperties;
import com.xinxin.eshop.excel.service.PictureService;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PictureServiceImpl implements PictureService {

    @Autowired
    private FTPProperties properties;

    @Override
    public void uploadPicture(MultipartFile uploadFile) throws IOException {
        //本地字符编码
        String local_charset = "GBK";
        //FTP默认文件名编码
        String server_charset = FTPClient.DEFAULT_CONTROL_ENCODING;
        //服务端UTF8的支持选项
        String command_open_utf8 = "OPTS UTF8";
        //开启状态
        String open_status = "ON";

        // 通过ftp上传图片到服务器
        // 1.创建ftp客户端对象
        FTPClient ftpClient = new FTPClient();
        // 2.创建ftp连接
        ftpClient.connect(properties.getAddress(), properties.getPort());
        // 3.登录ftp服务器
        ftpClient.login(properties.getUsername(), properties.getPassword());
        // 将客户端设置为被动模式
        ftpClient.enterLocalPassiveMode();
        // 4.获取文件流
        InputStream inputStream = uploadFile.getInputStream();
        // 5.设置上传路径
        ftpClient.changeWorkingDirectory(properties.getBasePath());
        // 6.图片上传  修改上传文件的格式为二进制
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        // 7.服务器存储文件，第一个参数是存储在服务器的文件名，第二个参数是文件流
        if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(command_open_utf8, open_status))) {
            local_charset = "UTF-8";
        }
        ftpClient.storeFile(new String(uploadFile.getOriginalFilename().getBytes(local_charset),server_charset), inputStream);
        // 8.关闭连接
        inputStream.close();
        ftpClient.logout();
    }
}
