package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.enums.ChartStatusEnum;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.mapper.ChartMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 下水道的小老鼠
 * @description 针对表【chart(图表信息表)】的数据库操作Service实现
 * @createDate 2023-08-23 15:17:57
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService {

    @Resource
    private ChartMapper chartMapper;

    @Override
    public void handleChartUpdateError(Long chartId, String execMessage) {
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setStatus(ChartStatusEnum.STATUS_FAILED.getMessage());
        updateChart.setExecMessage(execMessage);
        boolean update = this.updateById(updateChart);
        if (!update) {
            log.error("更新图表 失败状态 失败" + chartId + "," + execMessage);
        }
    }

    @Override
    public String getCsvData(String sqlString) {
        List<Map<String, Object>> data = chartMapper.queryChartData(sqlString);
        StringBuilder csvOutput = new StringBuilder();

        // Get column names (keys) from the first map in the list
        Set<String> columnNames = data.get(0).keySet();

        // Append column names to the CSV output
        for (String columnName : columnNames) {
            csvOutput.append(columnName).append(",");
        }

        // Remove last comma and add a newline
        csvOutput.setLength(csvOutput.length() - 1);
        csvOutput.append("\n");

        // Append each map's values to the CSV output
        for (Map<String, Object> studentData : data) {
            for (String columnName : columnNames) {
                csvOutput.append(studentData.get(columnName)).append(",");
            }

            // Remove last comma and add a newline
            csvOutput.setLength(csvOutput.length() - 1);
            csvOutput.append("\n");
        }

        return csvOutput.toString();
    }
}




