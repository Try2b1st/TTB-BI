package com.yupi.springbootinit.mq.dlx;

import com.rabbitmq.client.*;

import java.util.Scanner;

public class DlxDirectConsumer {
    private static final String DLX_EXCHANGE_NAME = "dlx-direct-exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明死信交换机
        channel.exchangeDeclare(DLX_EXCHANGE_NAME, "direct");

        //声明死信队列并绑定交换机和路由规则
        // 创建队列，分配一个队列名称
        String queueName = "boss_queue";
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, DLX_EXCHANGE_NAME, "boss");

        // 创建队列，分配一个队列名称
        String queueName2 = "waibao_queue";
        channel.queueDeclare(queueName2, true, false, false, null);
        channel.queueBind(queueName2, DLX_EXCHANGE_NAME, "waibao");

        DeliverCallback xiaoyuDeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [boss] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        DeliverCallback xiaopiDeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [waibao] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(queueName, true, xiaoyuDeliverCallback, consumerTag -> {
        });
        channel.basicConsume(queueName2, true, xiaopiDeliverCallback, consumerTag -> {
        });

    }
}
