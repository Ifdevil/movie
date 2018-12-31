package com.stylefeng.guns.rest.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
public class MQConsumerMsgListenerProcessor implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        if(CollectionUtils.isEmpty(msgs)){
            log.info("接受到的消息为空，不处理，直接返回成功");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        //处理消息
        for (MessageExt msg : msgs) {
            try {
                System.out.println(new String(msg.getBody()) + ":" + msg.toString());
                String topic = msg.getTopic();
                if(topic == "order"){

                }
            }catch (Exception e){
                e.printStackTrace();
                //假如接受消息时抛出异常，设定重试次数
                int reconsumTimes = msg.getReconsumeTimes();
                if(reconsumTimes == 3){
                    log.info(msg.getMsgId()+"已重试3次，取消重试，补偿处理");
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
