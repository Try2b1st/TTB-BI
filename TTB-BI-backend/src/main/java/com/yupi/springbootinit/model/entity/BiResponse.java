package com.yupi.springbootinit.model.entity;

import lombok.Data;

/**
 * AI模型返回值
 *
 * @author 下水道的小老鼠
 */

@Data
public class BiResponse {

    private String genChart;

    private String genResult;

    private Long chartId;
}
