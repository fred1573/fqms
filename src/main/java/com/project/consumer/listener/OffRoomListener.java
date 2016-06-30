package com.project.consumer.listener;

import com.project.consumer.event.OffRoomEvent;
import com.project.service.proxysale.OnOffRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * 监听OMS关房处理结果的事件
 * @author 番茄桑
 */
@Component
@Transactional
public class OffRoomListener implements ApplicationListener<OffRoomEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OffRoomListener.class);

    @Autowired
    private OnOffRoomService onOffRoomService;

    @Override
    public void onApplicationEvent(OffRoomEvent event) {
        String content = event.getSource().toString();
        LOGGER.info("------------------OffRoomListener:" + content + "-----------------");
        onOffRoomService.processOmsFailRoomOff(content);
    }
}
