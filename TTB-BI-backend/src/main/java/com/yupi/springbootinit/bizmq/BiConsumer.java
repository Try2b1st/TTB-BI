package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.enums.ChartStatusEnum;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.utils.ExcelUtils;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class BiConsumer {

    @Resource
    private YuCongMingClient client;
    @Resource
    private ChartService chartService;

    @Resource
    private ExcelUtils excelUtils;


    @RabbitListener(queues = {BiConstant.BI_QUEUE}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("receiveMessage message = {}", message);

        if (StringUtils.isBlank(message)) {
            // 如果失败，消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }

        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        log.info(String.valueOf(chart));
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }

        //从存储原始数据的表中获取原始数据
        String querySql = String.format("select * from chart_%s", chartId);
        String csvData = chartService.getCsvData(querySql);

        String result = excelUtils.auditingCSV(csvData);
        log.info("图片审查成功");
        if (Objects.equals(result, "0")) {
            log.info("文件内容-正常");
        } else if (Objects.equals(result, "1")) {
            log.info("文件内容-违规敏感");
            channel.basicNack(deliveryTag, false, false);
            chartService.handleChartUpdateError(chartId, "文件内容-违规敏感");
        } else {
            log.info("文件内容-疑似敏感，我们将进行人工复查");
            channel.basicNack(deliveryTag, false, false);
            chartService.handleChartUpdateError(chartId, "文件内容-疑似敏感，我们将进行人工复查");
        }

        //开始调用AI回复，更改数据状态
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setStatus(ChartStatusEnum.STATUS_RUNNING.getMessage());
        boolean updateStatus = chartService.updateById(updateChart);
        if (!updateStatus) {
            channel.basicNack(deliveryTag, false, false);
            chartService.handleChartUpdateError(chartId, "更新图表 运行/生成 状态 失败");
        }

        //发起请求获取响应
        DevChatRequest devChatRequest = buildUserInput(chart, csvData);
        com.yupi.yucongming.dev.common.BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);

        if (response.getCode() != 0) {
            channel.basicNack(deliveryTag, false, false);
            chartService.handleChartUpdateError(chartId, "更新图表 AI结果 失败");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }

        //生成成功
        //分割两部分数据
        String[] splits = response.getData().getContent().split("【【【【【");
        if (splits.length < 3) {
            channel.basicNack(deliveryTag, false, false);
            chartService.handleChartUpdateError(chart.getId(), "AI 生成错误");
            return;
        }

        //将数据保存到数据库中，用户在个人图表功能查看
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();

        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setStatus(ChartStatusEnum.STATUS_SUCCEED.getMessage());
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            channel.basicNack(deliveryTag, false, false);
            chartService.handleChartUpdateError(chartId, "更新图表 成功状态和AI结果 失败");
        }

        //确认消息
        channel.basicAck(deliveryTag, false);
    }

    private DevChatRequest buildUserInput(Chart chart, String csvData) {
        //设置BI模型ID
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(CommonConstant.BI_MODEL_ID);

        String goal = chart.getGoal();
        String chartType = chart.getChartType();

        //拼接诉求和数据
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("分析目标:").append(goal).append("\n");

        if (StringUtils.isNotBlank(chartType)) {
            stringBuilder.append("图表类型:").append(chartType).append("\n");
        }

        stringBuilder.append("原始数据:\n").append(csvData);
        devChatRequest.setMessage(stringBuilder.toString());

        return devChatRequest;
    }
}
