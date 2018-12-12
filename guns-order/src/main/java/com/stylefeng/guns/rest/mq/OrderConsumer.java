package com.stylefeng.guns.rest.mq;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class OrderConsumer {

    private String nameserveraddress;
    private String consumergroup;

    @Autowired
    private MQConsumerMsgListenerProcessor mqMessageListenerProcessor;

    @Bean
    public DefaultMQPushConsumer getOrderConsumer(){
        DefaultMQPushConsumer orderConsumer = new DefaultMQPushConsumer();
        try {
            orderConsumer.setNamesrvAddr(nameserveraddress);
            orderConsumer.setConsumerGroup(consumergroup);
            orderConsumer.registerMessageListener(mqMessageListenerProcessor);
            orderConsumer.setConsumeMessageBatchMaxSize(1);
            orderConsumer.subscribe("orderMessage","*");
            orderConsumer.start();
            log.info("初始化消息队列 consumer is start !!! groupName:{},topics:{},namesrvAddr:{}",
                        "orderMessage","*",nameserveraddress);
        } catch (MQClientException e) {
            log.error("初始化消息队列消费者OrderConsumer失败",e);
        }
        return orderConsumer;
    }
}
