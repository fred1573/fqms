package com.project.consumer.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author frd
 */
public class PriceAuditEvent extends ApplicationEvent {

    public PriceAuditEvent(Object source) {
        super(source);
    }
}
