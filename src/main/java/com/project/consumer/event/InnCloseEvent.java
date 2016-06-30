package com.project.consumer.event;

import org.springframework.context.ApplicationEvent;

/**
 * 处理分销商关房结果的事件
 * Created by sam on 2016/4/11.
 */
public class InnCloseEvent extends ApplicationEvent {

    public InnCloseEvent(Object source) {
        super(source);
    }
}
