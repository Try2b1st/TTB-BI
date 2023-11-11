package com.yupi.springbootinit.mq.single;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class SingleProducer {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        //建立连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //创建操作RabbitMQ的客户端
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //建立队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "hhhh!";
            //发送消息
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}