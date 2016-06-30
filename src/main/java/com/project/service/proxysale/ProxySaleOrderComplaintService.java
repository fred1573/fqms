package com.project.service.proxysale;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.project.bean.proxysale.OrderComplaintSearch;
import com.project.entity.proxysale.ProxySaleOrderComplaint;

import java.io.OutputStream;

/**
 * 代销订单客诉处理服务
 *
 * @author yuneng.huang on 2016/6/12.
 */
public interface ProxySaleOrderComplaintService {

    /**
     * 根据订单id 查询该订单是否已经存在投诉
     *
     * @param orderNo oms订单号
     * @return true 已经存在至少一条投诉记录
     */
    boolean isExistComplaint(String orderNo);


    /**
     * 保存一条订单投诉
     *
     * @param orderComplaint 投诉对象
     */
    void save(ProxySaleOrderComplaint orderComplaint);


    /**
     * 分页查询投诉列表
     *
     * @param pageBounds      分页对象
     * @param complaintSearch 搜索条件
     * @return 投诉列表，不会返回null
     */
    PageList<ProxySaleOrderComplaint> findByPage(PageBounds pageBounds, OrderComplaintSearch complaintSearch);

    /**
     * 查询满足条件的客栈数
     *
     * @param complaintSearch 搜索条件
     * @return 0没有满足条件的客栈
     */
    int findInnCountBySearch(OrderComplaintSearch complaintSearch);

    void exportExcel(OutputStream os, OrderComplaintSearch complaintSearch);
}
