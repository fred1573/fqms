package com.project.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tomato.mq.client.event.listener.MsgEventListener;
import com.tomato.mq.client.event.model.MsgEvent;
import com.tomato.mq.client.event.publisher.MsgEventPublisher;
import com.tomato.mq.support.message.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

/**
 * @author frd
 */
public class MsgConsumer implements MsgEventListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(MsgConsumer.class);

    @Autowired
    private IMsgPublisher msgPublisher;

    public MsgConsumer(String consumerId) {
        LOGGER.info("-------- start listener -----------consumerId:" + consumerId);
        MsgEventPublisher.getInstance().addListener(this, MessageType.SYS_EVENT, consumerId);
    }

    @Override
    public void onEvent(MsgEvent msgEvent) {
        LOGGER.info("---------------sys-msg coming:" + msgEvent.getSource().toString());
        JSONObject jsonObject = JSON.parseObject(msgEvent.getSource().toString());
        String bizType = jsonObject.getString("bizType");
        String content = jsonObject.getString("content");
        ApplicationEvent event = EventBuilder.build(bizType, content);
        if(event != null) {
            msgPublisher.publishEvent(event);
        }
    }

}
