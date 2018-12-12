package com.stylefeng.guns.rest.mq;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class OrderProducer {


    private String nameserveraddress;
    private String producergroup;

    @Bean
    public DefaultMQProducer getOrderProduce(){
        DefaultMQProducer orderProduce = new DefaultMQProducer();
        orderProduce.setNamesrvAddr(nameserveraddress);
        orderProduce.setProducerGroup(producergroup);
        try {
            orderProduce.start();
            log.info("初始化消息队列OrderProducer成功");
        } catch (MQClientException e) {
            log.error("初始化消息队列OrderProducer失败",e);
        }
        return orderProduce;
    }

    /**
     * 发送消息
     * @return
     */
    public static boolean sendMessage(){

        return true;
    }

}
