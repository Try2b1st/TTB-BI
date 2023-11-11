package com.yupi.springbootinit.mq.multi;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {
        //建立连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();

        for (int i = 0; i < 2; i++) {
            final Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                //取消息
                String message = new String(delivery.getBody(), "UTF-8");

                try {
                    //接收消息后，处理工作
                    System.out.println(" [x] Received '" + message + "'");

                    //模拟处理工作
                    Thread.sleep(5000);

                    //第二个参数 multiple 批量确认：是指是否要一次性确认所有的历史消息直到当前这条
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                } catch (InterruptedException e) {
                    //第 3 个参数表示是否重新入队，可用于重试
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                    throw new RuntimeException(e);
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            //开启消费监听
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}