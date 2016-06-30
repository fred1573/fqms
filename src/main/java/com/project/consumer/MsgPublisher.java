package com.project.consumer;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @author frd
 */
@Component
public class MsgPublisher implements ApplicationEventPublisherAware, IMsgPublisher {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        publisher.publishEvent(event);
    }
}
