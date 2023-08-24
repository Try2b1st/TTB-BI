package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Excel 相关工具类
 *
 * @author 下水道的小老鼠
 */
@Slf4j
public class ExcelUtils {
    public static String excelToCsv(MultipartFile multipartFile) {
//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
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

        //转化位CSV
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

//    public static void main(String[] args) {
//        ExcelUtils.excelToCsv(null);
//    }
}
