package com.yupi.springbootinit.mapper;

import com.yupi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
* @author 下水道的小老鼠
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2023-08-23 15:17:57
* @Entity com.yupi.springbootinit.model.entity.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {
    void createTable(@Param("tableName") String tableName, @Param("columns") String columns);

    void insertData(@Param("tableName") String tableName, @Param("columnNames") String[] columnNames, @Param("values") String[] values);

    List<Map<String, Object>> queryChartData(String querySql);
}




