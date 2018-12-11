package com.stylefeng.guns.rest.mq;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "apache")
public class OrderProducer {

    private DefaultMQProducer orderProduce;
    private String nameserveraddress;
    private String producergroup;

    @PostConstruct
    public void getOrderProduce(){
        orderProduce = new DefaultMQProducer();
        orderProduce.setNamesrvAddr(nameserveraddress);
        orderProduce.setProducerGroup(producergroup);
        try {
            orderProduce.start();
        } catch (MQClientException e) {
            log.error("初始化消息队列OrderProducer失败",e);
        }
    }

}
