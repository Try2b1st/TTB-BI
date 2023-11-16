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
import java.util.Objects;

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

        String keyName = fileName + multipartFile.getOriginalFilename();

        //将用户上传的图片保存到腾讯云COS
        ImgUtil.upload(keyName, multipartFile);
        log.info("图片上传成功");

        //审查图片
        String result = ImgUtil.imageAuditing(keyName);
        log.info("图片审查成功");
        if (Objects.equals(result, "0")) {
            System.out.println("图片正常");
        } else if (Objects.equals(result, "1")) {
            System.out.println("图片-违规敏感");
        } else {
            System.out.println("图片-疑似敏感，我们将进行人工复查");
        }

        //得到图片的url
        String imgUrl = String.valueOf(ImgUtil.getImgUrl(keyName));
        log.info("获取图片URL成功");

        //发送给模型
        System.out.println("哈哈：" + imgUrl);
    }
}
