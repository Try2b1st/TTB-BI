package com.yupi.springbootinit.controller;

import com.qcloud.cos.model.COSObject;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.ImgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/photo")
@Slf4j
public class PhotoController {

    @Resource
    private UserService userService;

    private final String fileName = "Img/";

    @PostMapping("/upload")
    public void uploadImage(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);

        //校验用户
        ThrowUtils.throwIf(loginUser == null, ErrorCode.PARAMS_ERROR, "请登录");

        //将用户上传的图片保存到腾讯云COS
        ImgUtil.upload(fileName + multipartFile.getOriginalFilename(), multipartFile);

        //得到图片的url
        String imgUrl = String.valueOf(ImgUtil.getImgUrl(fileName + multipartFile.getOriginalFilename()));

        //发送给模型
        System.out.println("哈哈："+imgUrl);

    }
}
