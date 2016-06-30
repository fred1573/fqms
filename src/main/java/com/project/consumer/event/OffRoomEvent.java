package com.project.consumer.event;

import org.springframework.context.ApplicationEvent;

/**
 * 处理OMS关房结果的事件
 * Created by sam on 2016/4/11.
 */
public class OffRoomEvent extends ApplicationEvent {

    public OffRoomEvent(Object source) {
        super(source);
    }
}
