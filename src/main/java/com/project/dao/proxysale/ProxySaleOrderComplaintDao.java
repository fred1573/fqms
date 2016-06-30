package com.project.dao.proxysale;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.project.bean.proxysale.OrderComplaintSearch;
import com.project.entity.proxysale.ProxySaleOrderComplaint;
import com.project.entity.proxysale.ProxySaleSubOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yuneng.huang on 2016/6/13.
 */
public interface ProxySaleOrderComplaintDao {
    void insert(ProxySaleOrderComplaint orderComplaint);

    ProxySaleOrderComplaint selectByOrderNo(String orderNo);

    ProxySaleOrderComplaint selectById(Long id);

    void update(ProxySaleOrderComplaint orderComplaint);

    PageList<ProxySaleOrderComplaint> selectByPage(@Param("pageBounds") PageBounds pageBounds,@Param("complaintSearch") OrderComplaintSearch complaintSearch);

    Integer selectInnCountBySearch(@Param("complaintSearch")OrderComplaintSearch complaintSearch);

    void insertSubOrderList(List<ProxySaleSubOrder> subOrderList);
}
