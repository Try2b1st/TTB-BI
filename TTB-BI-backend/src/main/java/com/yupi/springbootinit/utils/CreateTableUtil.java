package com.yupi.springbootinit.utils;

import com.yupi.springbootinit.mapper.ChartMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class CreateTableUtil {

    @Resource
    private ChartMapper chartMapper;

    public Boolean createTable(String csvData, String chartId) throws IOException {

        String[] lines = csvData.split("\n");
        String[] columnNames = lines[0].split(",");

        String tableName = "chart_" + chartId;

        StringBuilder columns = new StringBuilder();
        for (int i = 0; i < columnNames.length; i++) {
            columns.append(columnNames[i]).append(" VARCHAR(255) character set utf8");
            if (i < columnNames.length - 1) {
                columns.append(", ");
            }
        }
        chartMapper.createTable(tableName, columns.toString());
        return true;
    }

    public Boolean insertData(String csvData, String chartId) {
        String[] lines = csvData.split("\n");
        String[] columnNames = lines[0].split(",");
        String tableName = "chart_" + chartId;
        for (int i = 1; i < lines.length; i++) {
            String[] columnData = lines[i].split(",");
            chartMapper.insertData(tableName, columnNames, columnData);
        }
        return true;
    }
}
