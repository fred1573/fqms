package com.project.consumer;

import com.project.consumer.event.InnCloseEvent;
import com.project.consumer.event.OffRoomEvent;
import com.project.consumer.event.PriceAuditEvent;
import com.project.consumer.event.UpdateInnEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

/**
 * @author frd
 */
public class EventBuilder {

    private static final String UPDATE_INN_INFO = "UPDATE_OR_ADD_INN_INFO";
    private static final String PRICE_AUDIT = "PRICE_AUDIT";
    private static final String SPECIAL_DATE_CLOSE_INN = "SPECIAL_DATE_CLOSE_INN";
    private static final String INN_CLOSE = "INN_CLOSE";

    public static ApplicationEvent build(String bizType, String content) {
        if (StringUtils.isNotBlank(bizType)) {
            switch (bizType) {
                case UPDATE_INN_INFO:
                    return new UpdateInnEvent(content);
                case PRICE_AUDIT:
                    return new PriceAuditEvent(content);
                case SPECIAL_DATE_CLOSE_INN:
                    return new OffRoomEvent(content);
                case INN_CLOSE:
                    return new InnCloseEvent(content);
                default:
            }
        }
        return null;
    }
}
