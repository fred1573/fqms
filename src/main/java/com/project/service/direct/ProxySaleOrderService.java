package com.project.service.direct;

import com.alibaba.fastjson.JSONObject;
import com.project.bean.direct.ProxySaleOrderForm;
import com.project.bean.direct.ProxySaleOrderVo;
import com.project.utils.PageUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * 代销订单管理业务逻辑处理类
 * Created by sam on 2015/11/18.
 */
@Component
@Transactional
public interface ProxySaleOrderService {

    /**
     * 请求OMS查询代销订单
     * @param proxySaleOrderForm
     * @return 订单的json字符串格式
     */
    JSONObject findProxySaleOrderFromOMS(ProxySaleOrderForm proxySaleOrderForm, boolean isPage);

    /**
     * 通过OMS接口查询代销订单
     * @param jsonObject 请求oms接口返回的json对象
     * @return 封装后的订单对象
     */
    List<ProxySaleOrderVo> findProxySaleOrderVoList(JSONObject jsonObject);

    /**
     * 根据OMS接口返回的数据，查询订单的统计数据
     * @param jsonObject 请求oms接口返回的json对象
     * @return 统计信息
     */
    Map<String, String> findOrderTotalInfo(JSONObject jsonObject);

    /**
     * 构造分页对象
     * @param jsonObject 请求oms接口返回的json对象
     * @return 分页对象
     */
    PageUtil getPage(JSONObject jsonObject, int pageSize, int currentPage);

    /**
     * 通过OMS查询接口获取渠道ID
     * @return
     */
    Map<String, String> getChannelInfoFromOMS();

    /**
     * 根据父渠道ID查询子渠道的集合
     * @param parentChannelId
     * @return
     */
    Map<String, String> getChildChannelInfoFromOMS(String parentChannelId);

    /**
     * 将代销订单按照查询条件筛选后，导出Excel
     * @param response 响应对象
     * @param proxySaleOrderForm 查询过滤条件
     */
    void exportExcel(HttpServletResponse response, ProxySaleOrderForm proxySaleOrderForm);

    /**
     * OMS取消订单
     * @param channelOrderNo
     */
    void cancelOrder(String mark,String channelOrderNo,String channelId);
}
