package com.project.consumer;

import org.springframework.context.ApplicationEvent;

/**
 * @author frd
 */
public interface IMsgPublisher {

    void publishEvent(ApplicationEvent event);
}
