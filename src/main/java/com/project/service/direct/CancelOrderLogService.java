package com.project.service.direct;

import com.project.entity.proxysale.CancelOrderLog;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by admin on 2016/4/28.
 */
@Component
@Transactional
public interface CancelOrderLogService {
    /**
     * 保存取消订单操作日志
     */
    void save(String mark, String channelOrderNo);

    /**
     * 根据渠道订单号查询是否存在操作记录
     *
     * @param channelNo
     * @return
     */
    CancelOrderLog findCancelOrderLogWithChannelNo(String channelNo);

    /**
     * 记录取消订单操作日志
     *
     * @param remark
     * @param channelNo
     */
    void SaveOperateLog(String remark, String channelNo);
}
