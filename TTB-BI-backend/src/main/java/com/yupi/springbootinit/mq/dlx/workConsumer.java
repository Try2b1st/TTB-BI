package com.yupi.springbootinit.mq.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

public class workConsumer {
    private static final String DLX_EXCHANGE_NAME = "dlx-direct-exchange";

    private static final String EXCHANGE_NAME = "work-direct-exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        // 创建队列，分配一个队列名称
        String queueName = "xiaodog_queue";

        // 指定死信队列参数
        Map<String, Object> args = new HashMap<>();

        // 要绑定到哪个交换机
        args.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);

        // 指定死信要转发到哪个死信队列
        args.put("x-dead-letter-routing-key", "waibao");

        //设置死信交换机，死信队列
        channel.queueDeclare(queueName, true, false, false, args);
        channel.queueBind(queueName, EXCHANGE_NAME, "xiaodog");

        // 创建队列，分配一个队列名称
        String queueName2 = "xiaocat_queue";

        // 指定死信队列参数
        Map<String, Object> args2 = new HashMap<>();

        // 要绑定到哪个交换机
        args2.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);

        // 指定死信要转发到哪个死信队列
        args2.put("x-dead-letter-routing-key", "boss");

        channel.queueDeclare(queueName2, true, false, false, args2);
        channel.queueBind(queueName2, EXCHANGE_NAME, "xiaocat");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback xiaodogDeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaoyu] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
        };

        DeliverCallback xiaocatDeliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaopi] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
        };

        channel.basicConsume(queueName, false, xiaodogDeliverCallback, consumerTag -> {
        });
        channel.basicConsume(queueName2, false, xiaocatDeliverCallback, consumerTag -> {
        });
    }
}
