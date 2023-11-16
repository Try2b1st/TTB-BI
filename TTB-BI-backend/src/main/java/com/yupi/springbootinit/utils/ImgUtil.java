package com.yupi.springbootinit.utils;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingRequest;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingResponse;
import com.qcloud.cos.region.Region;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImgUtil {

    // 1 初始化用户身份信息(secretId, secretKey)
    static COSCredentials cred = new BasicCOSCredentials("xxx", "xx");
    // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
    // clientConfig中包含了设置region, https(默认http), 超时, 代理等set方法, 使用可参见源码或者接口文档FAQ中说明
    // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
    static ClientConfig clientConfig = new ClientConfig(new Region("ap-guangzhou"));

    static String bucketName = "try2b1st-photo-1311984591";
    // 指定要上传到 COS 上对象键
    // 对象键（Key）是对象在存储桶中的唯一标识。
    // \例如，在对象的访问域名 `bucket1-1250000000.cos.ap-chengdu.myqcloud.com/mydemo.jpg` 中，对象键为 mydemo.jpg,
    // 详情参考 [对象键](https://cloud.tencent.com/document/product/436/13324)

    public static void upload(String keyName, MultipartFile multipartFile) {
        //生成cos客户端
        COSClient cosClient = new COSClient(cred, clientConfig);

        // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
        // 大文件上传请参照 API 文档高级 API 上传
        //file里面填写本地图片的位置 我这里是相对项目的位置，在项目下有src/test/demo.jpg这张图片
        clientConfig.setHttpProtocol(HttpProtocol.https);

        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata);

        try {
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            System.out.println(putObjectResult.getRequestId());
        } catch (CosClientException e) {
            e.printStackTrace();
        }
        cosClient.shutdown();
    }

    public static URL getImgUrl(String keyName) {
        // 生成cos客户端
        COSClient cosClient = new COSClient(cred, clientConfig);

        // 设置生成的 url 的请求协议, http 或者 https
        // 5.6.54 及更高版本，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);


        URL url = cosClient.getObjectUrl(bucketName, keyName);

        cosClient.shutdown();

        return url;
    }

    public static String imageAuditing(String keyName) {
        // 生成cos客户端
        COSClient cosClient = new COSClient(cred, clientConfig);

        // 设置生成的 url 的请求协议, http 或者 https
        // 5.6.54 及更高版本，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);

        //1.创建任务请求对象
        ImageAuditingRequest request = new ImageAuditingRequest();
        //2.添加请求参数 参数详情请见api接口文档
        //2.1设置请求bucket
        request.setBucketName(bucketName);
        //2.2设置审核类型

        //2.3设置bucket中的图片位置
        request.setObjectKey(keyName);
        //3.调用接口,获取任务响应对象
        ImageAuditingResponse response = cosClient.imageAuditing(request);
        return response.getResult();
    }

}
