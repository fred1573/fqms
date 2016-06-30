package com.project.consumer.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author frd
 */
public class UpdateInnEvent extends ApplicationEvent {

    public UpdateInnEvent(Object source) {
        super(source);
    }

}
