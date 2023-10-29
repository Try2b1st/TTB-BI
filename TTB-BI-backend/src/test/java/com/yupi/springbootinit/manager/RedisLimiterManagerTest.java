package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    void doRateLimit() throws InterruptedException {
        for(int i = 0;i<2;i++){
            redisLimiterManager.doRateLimit("user_id");
            System.out.println("成功");
        }
        Thread.sleep(1000);
        for(int i = 0;i<2;i++){
            redisLimiterManager.doRateLimit("user_id");
            System.out.println("成功");
        }
    }
}