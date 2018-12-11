package com.stylefeng.guns.rest.mq;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "apache")
public class OrderProducer {

    private static DefaultMQProducer orderProduce;
    private static String nameserveraddress;
    private static String producergroup;

    public static DefaultMQProducer getOrderProduce(){
        if(orderProduce==null){
            synchronized (OrderProducer.class){
                if(orderProduce==null){
                    orderProduce = new DefaultMQProducer();
                    orderProduce.setNamesrvAddr(nameserveraddress);
                    orderProduce.setProducerGroup(producergroup);
                }
            }
        }
        return orderProduce;
    }

}
