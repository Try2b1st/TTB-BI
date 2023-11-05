package com.yupi.springbootinit.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池测试
 */

@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev", "local"})
public class ThreadController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String taskName) {
        CompletableFuture.runAsync(() -> {
            System.out.println("执行任务中：" + taskName + ",线程：" + Thread.currentThread().getName());
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    @GetMapping("/get")
    public Map<String, Object> get() {
        Map<String, Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        long taskCount = threadPoolExecutor.getTaskCount();
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        int activeCount = threadPoolExecutor.getActiveCount();

        map.put("任务队列长度：", size);
        map.put("任务总数：", taskCount);
        map.put("已经完成的任务数：", completedTaskCount);
        map.put("正在执行的任务：", activeCount);
        return map;
    }
}
