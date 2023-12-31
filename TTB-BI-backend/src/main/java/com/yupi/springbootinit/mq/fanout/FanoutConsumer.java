package com.yupi.springbootinit.mq.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class FanoutConsumer {
  private static final String EXCHANGE_NAME = "fanout-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();

    //创建频道
    Channel channel1 = connection.createChannel();
    Channel channel2 = connection.createChannel();

    //声明交换机
    channel1.exchangeDeclare(EXCHANGE_NAME, "fanout");
    channel2.exchangeDeclare(EXCHANGE_NAME, "fanout");

    String queueName1 = "小王的任务队列";
    String queueName2 = "小高的任务队列";

    //声明队列
    channel1.queueDeclare(queueName1, true, false, false, null);
    channel2.queueDeclare(queueName2, true, false, false, null);


    //交换机绑定队列
    channel1.queueBind(queueName1, EXCHANGE_NAME, "");
    channel2.queueBind(queueName2,EXCHANGE_NAME,"");
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [小王] Received '" + message + "'");
    };
    DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
      System.out.println(" [小高] Received '" + message + "'");
    };

    //监听
    channel1.basicConsume(queueName1, true, deliverCallback1, consumerTag -> { });
    channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });

  }
}