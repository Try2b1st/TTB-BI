package com.yupi.springbootinit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/photo")
@Slf4j
public class PhotoController {
    @PostMapping("/upload")
    public void handleFileUpload(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            // 在这里，您可以处理文件，例如保存到服务器的某个目录
            // 或者保存到数据库，或者进行其他处理
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            System.out.println(message);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            System.out.println(message);
        }
    }
}
