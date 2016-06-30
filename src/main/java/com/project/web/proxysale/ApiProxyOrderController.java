package com.project.web.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.vo.AjaxBase;
import com.project.common.Constants;
import com.project.entity.ota.OtaInfo;
import com.project.entity.proxysale.*;
import com.project.service.ota.OtaInfoService;
import com.project.service.proxysale.FailedOrderService;
import com.project.service.proxysale.ProxyInnService;
import com.project.service.proxysale.ProxyOrderService;
import com.project.utils.ProxyOrderIdGenerator;
import com.project.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 从oms下单
 * Created by Administrator on 2015/7/3.
 */
@Controller
@RequestMapping("/proxysale/api/order")
public class ApiProxyOrderController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiProxyOrderController.class);

    @Autowired
    private OtaInfoService otaInfoService;
    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private FailedOrderService failedOrderService;
    @Autowired
    private ProxyOrderService orderService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public AjaxBase createOrder(@RequestParam("order")String orderJson){
        try {
            ProxyParentOrder parentOrder = check(orderJson);
            orderService.createOrder(parentOrder);
            return new AjaxBase(Constants.HTTP_OK, "");
        } catch (Exception e) {
            LOGGER.error("-------------下单失败:" + e.getMessage() + "-------------");
            return new AjaxBase(Constants.HTTP_500, e.getMessage());
        }
    }

    private ProxyParentOrder check(String orderJson) {
        String error;
        if(StringUtils.isBlank(orderJson)){
            throw new RuntimeException("参数空了");
        }
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(orderJson);
        } catch (Exception e) {
            error = "json数据格式错误";
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), orderJson, error));
            throw new RuntimeException(error);
        }
        ProxyParentOrder parentOrder = new ProxyParentOrder();

        /*
        设置渠道商
         */
        Integer otaId = jsonObject.getInteger("otaId");
        OtaInfo otaInfo = otaInfoService.getByOtaId(otaId);
        if(otaInfo == null){
            error = "渠道商不存在";
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), orderJson, error));
            throw new RuntimeException(error);
        }
        parentOrder.setOtaId(otaInfo.getOtaId());

        /*
        设置渠道订单号
         */
        String otaOrderNo = jsonObject.getString("otaOrderNo");
        if(StringUtils.isBlank(otaOrderNo)){
            error = "渠道订单号不能为空";
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), orderJson, error));
            throw new RuntimeException(error);
        }
        parentOrder.setOtaOrderNo(otaOrderNo);

        /*
        设置代销客栈
         */
        Integer innId = jsonObject.getInteger("innId");
        if(innId == null){
            error = "客栈ID不能为空";
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), otaOrderNo, orderJson, error));
            throw new RuntimeException(error);
        }
        ProxyInn proxyInn = proxyInnService.findByInnId(innId);
        if(proxyInn == null){
            error = "客栈ID不存在";
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), otaOrderNo, orderJson, error));
            throw new RuntimeException(error);
        }
        parentOrder.setProxyInn(proxyInn);

        /*
        设置价格模式
         */
        Short orderPricePattern = null;
        Integer accountId = jsonObject.getInteger("accountId");
        if(accountId == null){
            error = "accountId不能为空";
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), otaOrderNo, orderJson, error));
            throw new RuntimeException(error);
        }
        for (PricePattern pattern : proxyInn.getPricePatterns()) {
            if(pattern.getOuterId() == null){
                continue;
            }
            if(pattern.getOuterId().intValue() == accountId.intValue()){
                orderPricePattern = pattern.getPattern();
            }
        }
        if(orderPricePattern == null){
            error = "无效的accountId";
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), otaOrderNo, orderJson, error));
            throw new RuntimeException(error);
        }
        parentOrder.setPricePattern(orderPricePattern);

        /*
        设置房间数量，每个子订单的房间数量都为此值
         */
        Integer roomTypeNum = jsonObject.getInteger("roomTypeNum");
        if(roomTypeNum == null){
            roomTypeNum = 1;
        }
        if(roomTypeNum <= 0){
            error = "roomTypeNum应大于0";
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), otaOrderNo, orderJson, error));
            throw new RuntimeException(error);
        }
        parentOrder.setRoomTypeNum(roomTypeNum);

        /*
        设置订单状态，默认为suc，取消后置为cancel
         */
        parentOrder.setStatus(ProxyParentOrder.SUC);

        /*
        设置子订单
         */
        JSONArray childOrders = jsonObject.getJSONArray("childOrders");
        if(childOrders == null || childOrders.size() <= 0){
            error = "子订单为空,fatal error";
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), otaOrderNo, orderJson, error));
            throw new RuntimeException(error);
        }
        try {
            analyizeChildOrders(parentOrder, childOrders);
        } catch (Exception e) {
            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), otaOrderNo, orderJson, e.getMessage()));
            throw new RuntimeException(e.getMessage());
        }
        parentOrder.setId(ProxyOrderIdGenerator.generate());
        parentOrder.setCreateTime(new Date());

        //比例同步时间
        Date perTime = jsonObject.getDate("perTime");
        if(perTime == null){
//            error = "请设置比例同步时间";
//            failedOrderService.create(new ProxyFailedOrder(ProxyOrderIdGenerator.generate(), orderJson, error));
//            throw new RuntimeException(error);
            perTime = new Date();
        }
        parentOrder.setPerTime(perTime);

        return parentOrder;
    }

    /**
     * 解析出子订单，并设置进父订单
     * @param proxyParentOrder 父订单
     * @param childOrders  子订单
     */
    private void analyizeChildOrders(ProxyParentOrder proxyParentOrder, JSONArray childOrders){
        Set<ProxyOrder> proxyOrders = new HashSet<>();
        for (int i = 0; i < childOrders.size(); i++) {
            JSONObject childJson = (JSONObject) childOrders.get(i);
            ProxyOrder proxyOrder = analyizeChildOrder(childJson);
            proxyOrder.setParentOrder(proxyParentOrder);
            proxyOrders.add(proxyOrder);
        }
        proxyParentOrder.setChildOrders(proxyOrders);
    }

    /**
     * 解析出一个子订单
     * @param childJson 子订单json
     * @return 父订单
     */
    private ProxyOrder analyizeChildOrder(JSONObject childJson) {
        BigDecimal bookRoomPrice = childJson.getBigDecimal("bookRoomPrice");
        Date checkInAt = childJson.getDate("checkInAt");
        Date checkOutAt = childJson.getDate("checkOutAt");
        Integer roomTypeId = childJson.getInteger("roomTypeId");
        String roomTypeName = childJson.getString("roomTypeName");
        if(!(bookRoomPrice.compareTo(new BigDecimal(0)) > 0)){
            throw new RuntimeException("预计金额应大于0");
        }
        if(checkInAt.after(checkOutAt)){
            throw new RuntimeException("退房日期应晚于入住日期");
        }
        if(StringUtils.isBlank(roomTypeName) || roomTypeId == null){
            throw new RuntimeException("房型ID或房型名称为空");
        }
        ProxyOrder proxyOrder = new ProxyOrder();
        proxyOrder.setBookRoomPrice(bookRoomPrice);
        proxyOrder.setCheckInAt(checkInAt);
        proxyOrder.setCheckOutAt(checkOutAt);
        proxyOrder.setRoomTypeId(roomTypeId);
        proxyOrder.setRoomTypeName(roomTypeName);
        proxyOrder.setId(ProxyOrderIdGenerator.generate());
        return proxyOrder;
    }

    @RequestMapping(value = "/{otaOrderNo}/cancel", method = RequestMethod.POST)
    @ResponseBody
    public AjaxBase cancel(@RequestParam("otaId")Integer otaId,
                             @PathVariable("otaOrderNo")String otaOrderNo,
                             @RequestParam("penalty")BigDecimal penalty){
        ProxyParentOrder parentOrder;
        try {
            parentOrder = orderService.findByOtaOrderNoAndOtaId(otaOrderNo, otaId);
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, e.getMessage());
        }
        if(parentOrder == null){
            return new AjaxBase(Constants.HTTP_500, "未找到该订单,取消失败");
        }

        orderService.cancel(parentOrder, penalty);
        return new AjaxBase(Constants.HTTP_OK, "");
    }

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("innId",4880);
        json.put("accountId",8153);
        json.put("otaId",903);
        json.put("otaOrderNo","c80166");
        json.put("roomTypeNum",1);

        JSONArray array = new JSONArray();
        JSONObject child = new JSONObject();
        child.put("bookRoomPrice", 400);
        child.put("checkInAt", "2015-07-03");
        child.put("checkOutAt", "2015-07-04");
        child.put("roomTypeId", 400);
        child.put("roomTypeName", "大床房");
        array.add(child);
        json.put("childOrders", array);

        System.out.println(json.toJSONString());
    }


}
