package com.myproject.systemdemo.rabbitmq.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class Receiver {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)//RabbitMQConfig.QUEUE
    public void receiverDirectQueue(String msg) throws IOException {
        System.out.println(msg);
    }

}
