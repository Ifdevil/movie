package com.stylefeng.guns.rest.mq;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class GatewayProducer {


    private String nameserveraddress;
    private String producergroup;
    private DefaultMQProducer gatewayProducer;

    @Bean
    public DefaultMQProducer getOrderProduce(){
        gatewayProducer = new DefaultMQProducer();
        gatewayProducer.setNamesrvAddr(nameserveraddress);
        gatewayProducer.setProducerGroup(producergroup);
        try {
            gatewayProducer.start();
            log.info("初始化消息队列OrderProducer成功");
        } catch (MQClientException e) {
            log.error("初始化消息队列OrderProducer失败",e);
        }
        return gatewayProducer;
    }

    /**
     * 发送消息
     * @return
     */
    public boolean sendMessage(){
        Message msg = new Message("orderMessage", "push", "1", "Just for push1.".getBytes());
        try {
            SendResult result = gatewayProducer.send(msg);
            System.out.println("id:" + result.getMsgId() + " result:" + result.getSendStatus());

        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return true;
    }

}
