package com.project.service.proxysale;

import com.project.entity.proxysale.OrderComplaintProcessLog;

import java.util.List;

/**
 * 供销订单投诉跟进记录服务接口
 *
 * @author yuneng.huang on 2016/6/13.
 */
public interface OrderComplaintProcessLogService {

    /**
     * 保存一条跟进记录
     *
     * @param processLog 跟进记录对象
     */
    void save(OrderComplaintProcessLog processLog);


    /**
     * 根据投诉记录id查询跟进记录
     * @param complaintId 投诉记录id
     * @return
     */
    List<OrderComplaintProcessLog> findByOrderComplaintId(Long complaintId);

    /**
     * 根据OMS订单id查询跟进记录
     * @param orderNo OMS订单id
     * @return
     */
    List<OrderComplaintProcessLog> findByOrderNo(String orderNo);
}
