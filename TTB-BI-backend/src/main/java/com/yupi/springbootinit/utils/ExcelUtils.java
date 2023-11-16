package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ciModel.auditing.TextAuditingRequest;
import com.qcloud.cos.model.ciModel.auditing.TextAuditingResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.config.CosClientConfig;
import com.yupi.springbootinit.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel 相关工具类
 *
 * @author 下水道的小老鼠
 */
@Slf4j
@Component
public class ExcelUtils {

    @Resource
    private CosClientConfig cosClientConfig;

    public String excelToCsv(MultipartFile multipartFile) {
        //读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误" + e);
            throw new RuntimeException(e);
        }

        if (CollUtil.isEmpty(list)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "表格为空");
        }

        //转化为CSV
        StringBuilder stringBuilder = new StringBuilder();
        //读取表头
        LinkedHashMap<Integer, String> linkedHeadMap = (LinkedHashMap) list.get(0);
        List<String> headList = linkedHeadMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headList, ",")).append("\n");

        //读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> linkedDataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = linkedDataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        System.out.println(stringBuilder);
        return stringBuilder.toString();
    }


    public String auditingCSV(String csv) {
        //1.创建任务请求对象
        TextAuditingRequest request = new TextAuditingRequest();
        //2.添加请求参数 参数详情请见 API 接口文档
        request.setBucketName(cosClientConfig.getBucket());
        //2.1.1设置请求内容,文本内容的Base64编码
        request.getInput().setContent(csv);
        TextAuditingResponse response = cosClientConfig.cosClient().createAuditingTextJobs(request);
        return response.getJobsDetail().getResult();
    }
}
