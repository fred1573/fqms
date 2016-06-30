package com.project.dao.proxysale;

import com.project.entity.proxysale.OrderComplaintProcessLog;

import java.util.List;

/**
 * @author yuneng.huang on 2016/6/13.
 */
public interface OrderComplaintProcessLogDao {

    void insert(OrderComplaintProcessLog processLog);

    List<OrderComplaintProcessLog> selectByOrderComplaintId(Long complaintId);

    List<OrderComplaintProcessLog> selectByOrderNo(String orderNo);
}
