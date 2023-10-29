package com.yupi.springbootinit.mapper;

import com.yupi.springbootinit.utils.CreateTableUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChartMapperTest {

    @Test
    void createTable() throws IOException {
        String csvData = "日期,新增人数\n" +
                "1,112\n" +
                "2,520\n" +
                "3,100";
        String chartId = "1659210482555121666";
        createTableUtil.createTable(csvData,chartId);
    }

    @Test
    void insertData() {
        String csvData = "日期,新增人数\n" +
                "1,112\n" +
                "2,520\n" +
                "3,100";
        String chartId = "1659210482555121666";
        createTableUtil.insertData(csvData,chartId);
    }

    @Test
    void queryChartData() {
        String chartId = "1659210482555121666";
        String querySql = String.format("select * from chart_%s", chartId);
        List<Map<String, Object>> resultData = chartMapper.queryChartData(querySql);
        System.out.println(resultData);
    }
    @Resource
    private ChartMapper chartMapper;

    @Resource
    private CreateTableUtil createTableUtil;
}