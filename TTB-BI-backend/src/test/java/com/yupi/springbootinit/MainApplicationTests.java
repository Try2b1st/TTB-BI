package com.yupi.springbootinit;

import com.yupi.springbootinit.config.WxOpenConfig;
import javax.annotation.Resource;

import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.utils.CreateTableUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private WxOpenConfig wxOpenConfig;


    @Test
    void contextLoads() {
        System.out.println(wxOpenConfig);
    }


}
