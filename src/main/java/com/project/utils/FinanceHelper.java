package com.project.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.bo.ChannelSettlementData;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.proxysale.PricePatternDao;
import com.project.dao.proxysale.PriceStrategyDao;
import com.project.dao.proxysale.ProxyInnDao;
import com.project.entity.finance.*;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.PriceStrategy;
import com.project.utils.encode.PassWordUtil;
import com.project.utils.time.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 用于财务对象的帮助类
 * Created by 番茄桑 on 2015/8/20.
 */
public class FinanceHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceHelper.class);
    private static final String SHORT_TIME_PATTERNS = "yyyy-MM";

    @Resource
    private PricePatternDao pricePatternDao;

    @Resource
    private PriceStrategyDao priceStrategyDao;

    @Resource
    private ProxyInnDao proxyInnDao;

    /**
     * 获取财务对账的时间区间
     * key格式为：YYYY-MM,value格式为:YYYY.MM.dd-YYYY.MM.dd
     *
     * @return
     */
    public Map<String, String> getFinanceTimeMap() {
        // 获取配置文件中的对账的开始时间
        String financeBeginTimeStr = Constants.FINANCE_BEGIN_TIME;
        if (StringUtils.isBlank(financeBeginTimeStr)) {
            throw new RuntimeException("配置文件中的对账起始时间不存在");
        }
        Date date = null;
        try {
            // 配置文件读取对账开始月份
            date = DateUtils.parseDate(financeBeginTimeStr, SHORT_TIME_PATTERNS);
        } catch (ParseException e) {
            throw new RuntimeException("配置文件中的对账起始时间格式不合法");
        }
        Calendar beginCalendar = DateUtils.toCalendar(date);
        Calendar endCalendar = DateUtils.toCalendar(new Date());
        // 获得对账截止月份
        endCalendar.add(Calendar.MONTH, -1);
        Map<String, String> timeMap = new HashMap<>();
        while (beginCalendar.before(endCalendar)) {
            int beginDate = beginCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
            int endDate = beginCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            String format = DateFormatUtils.format(beginCalendar, "yyyy.MM");
            StringBuilder timeValue = new StringBuilder(format);
            timeValue.append(".0" + beginDate + "-");
            timeValue.append(format);
            timeValue.append("." + endDate);
            timeMap.put(DateFormatUtils.format(beginCalendar, SHORT_TIME_PATTERNS), timeValue.toString());
            beginCalendar.add(Calendar.MONTH, 1);
        }
        return timeMap;
    }

    /**
     * 拼接请求OMS同步渠道订单接口的URL
     *
     * @return OMS同步渠道订单接口的URL
     */
    private String getUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        // 拼接接口请求域名、端口号
        stringBuilder.append(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        // 拼接服务名称
        stringBuilder.append(ApiURL.OMS_FINANCE_ORDER);
        return stringBuilder.toString();
    }

    /**
     * 设置请求接口的基本参数
     *
     * @return
     */
    private Map<String, Object> getBaseParamMap() {
        Map<String, Object> paramsMap = new HashMap<>();
        // 获取时间戳
        long timestamp = System.currentTimeMillis();
        paramsMap.put("timestamp", timestamp);
        // 获取签名
        paramsMap.put("signature", PassWordUtil.getDirectSignature(timestamp));
        // 设置父渠道ID
        paramsMap.put("channelId", Constants.OMS_PROXY_PID);
        paramsMap.put("otaId", Constants.OMS_PROXY_PID);
        return paramsMap;
    }

    /**
     * 根据账期获取订单的借口请求参数
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public Map<String, Object> getCheckParamForPeriod(String beginDate, String endDate) {
        Map<String, Object> paramsMap = getBaseParamMap();
        paramsMap.put("orderType", "CHECK_OUT");
        // 设置同步开始时间
        paramsMap.put("startDate", beginDate);
        // 设置同步结束时间
        paramsMap.put("endDate", endDate);
        return paramsMap;
    }

    /**
     * 按照账期查询订单
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public List<FinanceParentOrder> getCheckOutForPeriodFinanceOrderFromOMS(String beginDate, String endDate) {
        Map<String, Object> checkOutParamMap = getCheckParamForPeriod(beginDate, endDate);
        return getFinanceOrderFromOMS(checkOutParamMap);
    }


    /**
     * 设置结算的接口请求参数
     *
     * @param settlementTime
     * @return
     */
    private Map<String, Object> getCheckOutParamMap(String settlementTime) {
        Map<String, Object> paramsMap = getBaseParamMap();
        paramsMap.put("orderType", "CHECK_OUT ");
        if (settlementTime.indexOf("至") < -1) {
            throw new RuntimeException("账期格式错误");
        }
        String[] period = settlementTime.split("至");
        if (period == null || period.length != 2) {
            throw new RuntimeException("账期格式错误");
        }
        // 设置同步开始时间
        paramsMap.put("startDate", period[0]);
        // 设置同步结束时间
        paramsMap.put("endDate", period[1]);
        return paramsMap;
    }

    /**
     * 设置结算当前月的借口请求参数
     *
     * @param beginDate
     * @return
     */
    private Map<String, Object> getCheckParamForNowMonthMap(String beginDate) {
        Map<String, Object> paramsMap = getBaseParamMap();
        paramsMap.put("orderType", "CHECK_OUT ");
        // 设置同步开始时间
        paramsMap.put("startDate", beginDate);
        // 设置同步结束时间
        paramsMap.put("endDate", Constants.GRAB_FINANCEORDER_END_TIME);
        return paramsMap;
    }

    /**
     * 按照退房时间查询指定结算月份的订单
     *
     * @param settlementTime
     * @return
     */
    public List<FinanceParentOrder> getCheckOutFinanceOrderFromOMS(String settlementTime) {
        Map<String, Object> checkOutParamMap = getCheckOutParamMap(settlementTime);
        return getFinanceOrderFromOMS(checkOutParamMap);
    }

    /**
     * 按照退房时间查询当前结算月份的订单
     *
     * @param beginDate
     * @return
     */
    public List<FinanceParentOrder> getCheckOutForNowMonthFinanceOrderFromOMS(String beginDate) {
        Map<String, Object> checkOutParamMap = getCheckParamForNowMonthMap(beginDate);
        return getFinanceOrderFromOMS(checkOutParamMap);
    }

    /**
     * 请求OMS接口同步用于财务结算的订单信息
     *
     * @return
     */
    public List<FinanceParentOrder> getFinanceOrderFromOMS(Map<String, Object> paramsMap) {
        String result = HttpUtil.httpPost(getUrl(), paramsMap, false);
        List<FinanceParentOrder> financeParentOrderList = null;
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if ("200".equals(status)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (!CollectionUtils.isEmpty(rows)) {
                    financeParentOrderList = new ArrayList<>();
                    for (int i = 0; i < rows.size(); i++) {
                        String order = rows.get(i).toString();
                        financeParentOrderList.add(JSONObject.parseObject(order, FinanceParentOrder.class));
                    }
                }
            }
        }
        return financeParentOrderList;
    }

    /**
     * 根据客栈ID获取与客栈结算的支付信息
     *
     * @param innId
     * @return
     */
    public FinanceInnSettlementInfo getInnInfo(Integer innId) {
        FinanceInnSettlementInfo financeInnSettlementInfo;
        String pmsUrl = SystemConfig.PROPERTIES.get(SystemConfig.PMS_URL) + ApiURL.PMS_INN_INFO + innId + "/false";
        String result = new HttpUtil().get(pmsUrl);
        if (StringUtils.isBlank(result)) {
            LOGGER.error("PMS接口没有响应");
            return null;
        }
        JSONObject resultObject = JSON.parseObject(result);
        int status = resultObject.getIntValue("status");
        if (status != 200) {
            LOGGER.error("PMS接口请求失败，原因：" + resultObject.getString("message"));
            return null;
        }
        JSONObject innAdmin = resultObject.getJSONObject("innAdmin");
        if (innAdmin == null) {
            LOGGER.error("PMS接口获取客栈信息失败");
            return null;
        }
        JSONObject inn = innAdmin.getJSONObject("inn");
        if (inn == null) {
            LOGGER.error("PMS接口获取客栈信息失败");
            return null;
        }
        financeInnSettlementInfo = new FinanceInnSettlementInfo();
        // 设置PMS客栈ID
        financeInnSettlementInfo.setId(inn.getInteger("id"));
        JSONObject region = inn.getJSONObject("region");
        if (region != null) {
            // 设置目的地ID
            financeInnSettlementInfo.setRegionId(region.getInteger("id"));
            // 设置目的地名称
            financeInnSettlementInfo.setRegionName(region.getString("name"));
            // 设置城市编号
            financeInnSettlementInfo.setCityCode(region.getString("cityCode"));
            // 设置城市名称
            financeInnSettlementInfo.setCityName(region.getString("cityName"));
        }
        financeInnSettlementInfo.setInnContact(innAdmin.getString("mobile"));
        Integer bankType = inn.getInteger("bankType");
        if (bankType != null) {
            if (bankType.equals(1)) {
                financeInnSettlementInfo.setBankType("个人");
            } else {
                financeInnSettlementInfo.setBankType("对公");
            }
        }
        // 获取客栈名称
        String innName = inn.getString("name");
        // 获取客栈内部名称
        String realName = inn.getString("realName");
        if (StringUtils.isNotBlank(realName)) {
            innName = realName;
        }
        financeInnSettlementInfo.setInnName(innName);
        financeInnSettlementInfo.setBankAccount(inn.getString("bankAccount"));
        financeInnSettlementInfo.setBankCode(inn.getString("bankCode"));
        financeInnSettlementInfo.setBankName(inn.getString("bankName"));
        financeInnSettlementInfo.setBankRegion(inn.getString("bankRegion"));
        financeInnSettlementInfo.setBankProvince(inn.getString("bankProvince"));
        financeInnSettlementInfo.setBankCity(inn.getString("bankCity"));
        financeInnSettlementInfo.setWxOpenId(StringUtils.defaultString(innAdmin.getString("wxOpenId"), ""));
        financeInnSettlementInfo.setContact1(StringUtils.defaultString(inn.getString("contact1"), ""));
        financeInnSettlementInfo.setContact2(StringUtils.defaultString(inn.getString("contact2"), ""));
        return financeInnSettlementInfo;
    }

    /**
     * 根据接口返回的订单对象封装对账需要的主订单中的自有属性
     * 包括：底价模式番茄加价比例、卖价时渠道佣金比例、卖价时番茄佣金比例、费用类型
     *
     * @param financeParentOrder
     */
    public void packFinanceOrder(FinanceParentOrder financeParentOrder) {
        // 设置产生周期默认等于本账期
        financeParentOrder.setProduceTime(financeParentOrder.getSettlementTime());
        buildInnAmount(financeParentOrder);
        // 设置底价模式的番茄加价比例
        buildIncreaseRate(financeParentOrder);
        // 设置卖价模式的番茄佣金比例和渠道佣金比例
        buildCommissionRate(financeParentOrder);
        // 设置订单的费用类型
        buildCostType(financeParentOrder);
      /*  // 设置结算月份
        buildLastCheckOutDate(financeParentOrder);*/
        // 设置渠道结算金额
        buildChannelSettlementAmount(financeParentOrder);
        // 设置客栈结算金额
        buildInnSettlementAmount(financeParentOrder);
        // 设置番茄结算金额
        buildFqSettlementAmount(financeParentOrder);
        // 封装子订单的属性
        packFinanceSubOrder(financeParentOrder);
        // 封装统计扩展字段
        packFinanceOrderNewFields(financeParentOrder);
    }

    /**
     * 构造客栈订单金额
     *
     * @param financeParentOrder
     */
    private void buildInnAmount(FinanceParentOrder financeParentOrder) {
        BigDecimal innAmount = null;
        BigDecimal channelAmount = financeParentOrder.getChannelAmount();
        BigDecimal extraPrice = financeParentOrder.getExtraPrice();
        if (extraPrice == null || extraPrice.equals(BigDecimal.ZERO)) {
            innAmount = channelAmount;
        } else {
            if (channelAmount != null) {
                innAmount = channelAmount.subtract(extraPrice);
            }
        }
        financeParentOrder.setInnAmount(innAmount);
        Set<FinanceOrder> channelOrderList = financeParentOrder.getChannelOrderList();
        if (CollectionsUtil.isNotEmpty(channelOrderList)) {
            for (FinanceOrder financeOrder : channelOrderList) {
                BigDecimal childInnAmount = null;
                BigDecimal childChannelAmount = financeOrder.getChannelAmount();
                BigDecimal childExtraPrice = financeOrder.getExtraPrice();
                if (childExtraPrice == null || childExtraPrice.equals(BigDecimal.ZERO)) {
                    childInnAmount = childChannelAmount;
                } else {
                    if (childChannelAmount != null) {
                        childInnAmount = childChannelAmount.subtract(childExtraPrice);
                    }
                }
                financeOrder.setInnAmount(childInnAmount);
            }
            financeParentOrder.setChannelOrderList(channelOrderList);
        }
    }

    /**
     * 封装对账需要的主订单中的部分属性
     *
     * @param financeParentOrder
     */
    public void packFinanceOrderNewFields(FinanceParentOrder financeParentOrder) {
        // 设置间夜数
        buildRoomsNights(financeParentOrder);
        //设置提前预定天数
        buildReservationDays(financeParentOrder);
        //设置停留天数
        buildStayDays(financeParentOrder);
       //设置房间数和夜数
        buildRoomsAndNights(financeParentOrder);
    }

    private void buildRoomsAndNights(FinanceParentOrder financeParentOrder) {
        List<UtilsOrder> utilsOrders = getUtilsOrders(financeParentOrder);
        List<UtilsOrder> utilsOrderList = combineRoom(utilsOrders);
        UtilsOrder utilsOrder = combineNight(utilsOrderList);
        financeParentOrder.setNights(utilsOrder.getNights());
        financeParentOrder.setRooms(utilsOrder.getRooms());
    }

    //抽离出房间数，夜数，住离日期
    public List<UtilsOrder> getUtilsOrders(FinanceParentOrder financeParentOrder) {
        Set<FinanceOrder> orders = financeParentOrder.getChannelOrderList();
        List<UtilsOrder> utilsOrders = new ArrayList<>();
        if (CollectionsUtil.isNotEmpty(orders)) {
            for (FinanceOrder financeOrder : orders) {
                UtilsOrder utilsOrder = new UtilsOrder();
                utilsOrder.setRooms(financeOrder.getRoomTypeNums());
                utilsOrder.setNights(financeOrder.getNights());
                utilsOrder.setCheckIn(financeOrder.getCheckInAt());
                utilsOrder.setCheckOut(financeOrder.getCheckOutAt());
                utilsOrders.add(utilsOrder);
            }
        }
        return utilsOrders;
    }

    /**
     * 根据住离日期相同，统计合并房间数
     *
     * @param list
     * @return
     */
    public List<UtilsOrder> combineRoom(List<UtilsOrder> list) {
        List<UtilsOrder> utilsOrderList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j <list.size(); j++) {
                if (isSampleDate(list.get(i), list.get(j))) {
                    list.get(i).setRooms(list.get(i).getRooms() + 1);
                    list.remove(j);
                }
            }
            utilsOrderList.add(list.get(i));
        }
        return utilsOrderList;
    }

    /**
     * 根据住离日期连续，统计夜数
     *
     * @param list
     * @return
     */
    private UtilsOrder combineNight(List<UtilsOrder> list) {
        Long night = null;
        List<Date> dateListIn = new ArrayList<>();
        List<Date> dateListOut = new ArrayList<>();
        for (UtilsOrder u : list) {
            dateListOut.add(u.getCheckOut());
            dateListIn.add(u.getCheckIn());
        }
        Date checkIn = DateUtil.getBeginDate(dateListIn);
        Date checkOut = DateUtil.getEndDate(dateListOut);
        night = (checkOut.getTime() - checkIn.getTime()) / (24 * 60 * 60 * 1000);
        UtilsOrder utilsOrder = list.get(0);
        utilsOrder.setNights(Integer.parseInt(night.toString()));
        return utilsOrder;
    }

    /**
     * 判断住离日期是否相同
     *
     * @param u1
     * @param u2
     * @return
     */
    public boolean isSampleDate(UtilsOrder u1, UtilsOrder u2) {
        boolean in = u1.getCheckIn().equals(u2.getCheckIn());
        boolean out = u1.getCheckOut().equals(u2.getCheckOut());
        return in && out;
    }

    /**
     * 住离日期是否连续
     *
     * @param u1
     * @param u2
     * @return
     */
    public boolean isSeriesDate(UtilsOrder u1, UtilsOrder u2) {
        boolean a = u1.getCheckIn().equals(u2.getCheckOut());
        boolean b = u1.getCheckOut().equals(u2.getCheckIn());
        return a || b;
    }


    /**
     * 计算间夜数
     *
     * @param financeParentOrder
     */
    public void buildRoomsNights(FinanceParentOrder financeParentOrder) {
        Set<FinanceOrder> channelOrderList = financeParentOrder.getChannelOrderList();
        int num = 0;
        if (CollectionsUtil.isNotEmpty(channelOrderList)) {
            for (FinanceOrder financeOrder : channelOrderList) {
                // 获得夜数
                Integer nights = financeOrder.getNights();
                // 获得房间数
                Integer roomTypeNums = financeOrder.getRoomTypeNums();
                num += nights * roomTypeNums;
            }
            financeParentOrder.setRoomNights(num);
        }
    }

    /**
     * 卖价模式下，渠道的分佣比例
     *
     * @param financeParentOrder
     * @return
     */
    private void buildCommissionRate(FinanceParentOrder financeParentOrder) {
        // 获取价格策略，(1:底价 2:卖价)
        Short priceStrategy = financeParentOrder.getPriceStrategy();
        if (priceStrategy == null) {
            LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]价格策略为空，无法计算卖价模式分佣比例");
            return;
        }
        if (priceStrategy == 2 || priceStrategy == 3) {
            // 对应tomato_proxysale_price_pattern表的outer_id
            Integer accountId = financeParentOrder.getAccountId();
            if (accountId == null) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]accountId为空，无法计算卖价模式分佣比例");
                return;
            }
            // 渠道ID
            Short channelId = financeParentOrder.getChannelId();
            if (channelId == null) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]channelId为空，无法计算卖价模式分佣比例");
                return;
            }
            // 下单时间
            Date orderTime = financeParentOrder.getOrderTime();
            if (orderTime == null) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]orderTime为空，无法计算卖价模式分佣比例");
                return;
            }
            // 查询卖价策略中番茄的抽佣比例
            PricePattern pricePattern = pricePatternDao.getByOuterId(accountId);
            if (pricePattern == null) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]获取下单时间番茄分佣比例失败，无法计算卖价模式分佣比例");
                return;
            }
            // 查询卖价策略中渠道的抽佣比例
            PriceStrategy strategy = priceStrategyDao.findHistory(channelId.intValue(), orderTime, priceStrategy);
            if (strategy == null) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]获取下单时间渠道分佣比例失败，无法计算卖价模式分佣比例");
                return;
            }
            Float percentage = pricePattern.getPercentage();
            if (percentage == null) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]番茄分佣比例无效，无法计算卖价模式分佣比例");
                return;
            }
            BigDecimal fqPercentage = BigDecimal.valueOf(percentage);
            if (!isValidRate(fqPercentage)) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]番茄分佣比例无效，无法计算卖价模式分佣比例");
                return;
            }
            Float percentage1 = strategy.getPercentage();
            if (percentage1 == null) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]渠道分佣比例无效，无法计算卖价模式分佣比例");
                return;
            }
            BigDecimal channelPercentage = BigDecimal.valueOf(percentage1);
            if (!isValidRate(channelPercentage)) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]渠道分佣比例无效，无法计算卖价模式分佣比例");
                return;
            }
            // 封装卖价时渠道佣金比例
            financeParentOrder.setChannelCommissionRate(channelPercentage);
            // 封装卖价时番茄佣金比例
            // 番茄抽佣比为番茄抽佣减去渠道抽佣
            financeParentOrder.setFqCommissionRate(fqPercentage.subtract(channelPercentage));
        }
    }

    /**
     * 判定订单的费用类型
     *
     * @param financeParentOrder
     * @return
     */
    private void buildCostType(FinanceParentOrder financeParentOrder) {
        // 获取订单状态
        String status = financeParentOrder.getStatus();
        // 订单已付金额
        BigDecimal paidAmount = financeParentOrder.getPaidAmount();
        // 订单状态为3（取消），并且订单已付金额中有值
        if ("3".equals(status) && isValidRate(paidAmount)) {
            financeParentOrder.setCostType((short) 2);
        }
        // 默认为房费
        financeParentOrder.setCostType((short) 1);
    }

    /**
     * 计算底价模式中，番茄的加价比例
     *
     * @param financeParentOrder
     * @return
     */
    private void buildIncreaseRate(FinanceParentOrder financeParentOrder) {
        // 获取价格策略，(1:底价 2:卖价)
        Short priceStrategy = financeParentOrder.getPriceStrategy();
        if (priceStrategy == null) {
            LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]价格策略为空，无法计算底价模式番茄加价比例");
            return;
        }
        if (priceStrategy == 1) {
            // 渠道ID
            Short channelId = financeParentOrder.getChannelId();
            if (channelId == null) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]渠道ID为空，无法计算底价模式番茄加价比例");
                return;
            }
            // 下单时间
            Date orderTime = financeParentOrder.getOrderTime();
            if (orderTime != null) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]下单时间为空，无法计算底价模式番茄加价比例");
                return;
            }
            PriceStrategy strategy = priceStrategyDao.findHistory(channelId.intValue(), orderTime, priceStrategy);
            // 如果没有查询到精品的加价比例，默认为0
            if (strategy == null) {
                financeParentOrder.setIncreaseRate(BigDecimal.ZERO);
                return;
            }
            // 获得番茄给渠道的加价比例
            BigDecimal percentage = BigDecimal.valueOf(strategy.getPercentage());
            if (!isValidRate(percentage)) {
                LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]无效的加价比例，无法计算底价模式番茄加价比例");
                return;
            }
            financeParentOrder.setIncreaseRate(percentage);
        }
    }


    /**
     * 计算客栈的结算金额
     *
     * @param financeParentOrder
     * @return
     */
    public void buildInnSettlementAmount(FinanceParentOrder financeParentOrder) {
        BigDecimal innSettlementAmount = null;
        // 获得价格策略
        Short priceStrategy = financeParentOrder.getPriceStrategy();
        if (priceStrategy == null) {
            LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]价格策略为空，无法计算客栈结算金额");
            return;
        }
        // 订单总金额
        BigDecimal totalAmount = financeParentOrder.getTotalAmount();
        if (totalAmount == null) {
            LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]订单价格为空，无法计算客栈结算金额");
            return;
        }
        // 1为底价模式，
        if (priceStrategy == 1) {
            // 底价模式下，订单总价即是客栈结算金额
            // 底价模式存在算法问题，OMS没有提供客栈原始价格，且对小数部分进行了特殊处理，如果底价加价比例不等于0无法计算正确的结算金额
            innSettlementAmount = totalAmount;
        } else {
            // 卖价模式中番茄的分佣比例
            BigDecimal fqCommissionRate = financeParentOrder.getFqCommissionRate();
            // 卖价模式中渠道的分佣比例
            BigDecimal channelCommissionRate = financeParentOrder.getChannelCommissionRate();
            if (fqCommissionRate != null && isValidRate(channelCommissionRate)) {
                // 获取总抽佣比例
                BigDecimal totalRate = fqCommissionRate.add(channelCommissionRate);
                // 卖转底模式特殊处理
                if (priceStrategy == 3) {
                    // 卖转底模式使用渠道订单价格进行结算
                    totalAmount = financeParentOrder.getChannelAmount();
                    if (totalAmount == null) {
                        LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]渠道订单价格为空，无法计算客栈结算金额");
                        return;
                    }
                }
                // 获取加减金额
                BigDecimal extraPrice = financeParentOrder.getExtraPrice();
                if (extraPrice != null) {
                    // 减去加价的部分
                    totalAmount = totalAmount.subtract(extraPrice);
                }
                // 订单总价*（1-番茄佣金比例-渠道佣金比例）    ==》100*1*1*（1-13%）=87
                innSettlementAmount = totalAmount.multiply(BigDecimal.ONE.subtract(totalRate.divide(BigDecimal.valueOf(100))));
                // 处理小数部分的数值
                innSettlementAmount = floatPriceModel(innSettlementAmount);
            }
        }
        financeParentOrder.setInnSettlementAmount(innSettlementAmount);
    }

    /**
     * 计算番茄分佣金额
     *
     * @param financeParentOrder
     * @return
     */
    public void buildFqSettlementAmount(FinanceParentOrder financeParentOrder) {
        BigDecimal fqSettlementAmount = BigDecimal.ZERO;
        // 获取分销商结算金额
        BigDecimal channelSettlementAmount = financeParentOrder.getChannelSettlementAmount();
        // 获取客栈结算金额
        BigDecimal innSettlementAmount = financeParentOrder.getInnSettlementAmount();
        if (channelSettlementAmount != null && innSettlementAmount != null) {
            // 番茄结算金额为分销商结算金额-客栈结算金额
            fqSettlementAmount = channelSettlementAmount.subtract(innSettlementAmount);
        }
        financeParentOrder.setFqSettlementAmount(fqSettlementAmount);
    }

    /**
     * 计算渠道结算金额
     *
     * @param financeParentOrder
     * @return
     */
    public void buildChannelSettlementAmount(FinanceParentOrder financeParentOrder) {
        // 获取价格模式，1：底价，2：卖价，3：卖转底
        Short priceStrategy = financeParentOrder.getPriceStrategy();
        if (priceStrategy == null) {
            LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]价格策略为空，无法计算渠道结算金额");
            return;
        }
        BigDecimal channelSettlementAmount = null;
        // 订单金额（渠道真实价格）
        BigDecimal totalAmount = financeParentOrder.getTotalAmount();
        if (totalAmount == null) {
            LOGGER.error("订单[" + financeParentOrder.getChannelOrderNo() + "]订单价格为空，无法计算渠道结算金额");
            return;
        }
        // 1为底价模式
        if (priceStrategy == 1) {
            // 20160122迭代底价结算时，渠道结算金额=channelAmount（运营调价后价格），底价不能加价
            channelSettlementAmount = totalAmount;
        }
        // 2为卖价模式
        if (priceStrategy == 2) {
            // 获得卖价模式，渠道佣金比例
            BigDecimal channelCommissionRatio = financeParentOrder.getChannelCommissionRate();
            if (isValidRate(channelCommissionRatio)) {
                // 订单价格*（1-渠道佣金比例）    ==》100（1-8%）=92
                channelSettlementAmount = totalAmount.multiply(BigDecimal.ONE.subtract(channelCommissionRatio.divide(BigDecimal.valueOf(100))));
                // 处理小数部分的数值
                channelSettlementAmount = floatPriceModel(channelSettlementAmount);
            } else {
                LOGGER.error("订单[" + financeParentOrder.getChannelName() + "]的订单[" + financeParentOrder.getChannelOrderNo() + "]，渠道分佣比例不存在，无法核对");
            }
        }
        // 3为卖转底模式
        if (priceStrategy == 3) {
            channelSettlementAmount = totalAmount;
        }
        financeParentOrder.setChannelSettlementAmount(channelSettlementAmount);
    }

    /**
     * 验证比例是否合法
     *
     * @param rate 加价或者佣金比例
     * @return 不等于null，qie不等于0时为有效的比例值
     */
    public boolean isValidRate(BigDecimal rate) {
        return rate != null && !BigDecimal.ZERO.equals(rate);
    }

    /**
     * 卖价模式小数部分的处理
     *
     * @param price 卖价
     * @return 保留两位小数，四舍五入
     */
    public BigDecimal floatPriceModel(BigDecimal price) {
        return price.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 根据接口返回的订单对象，封装对账需要的子订单中的自有属性
     * 包括：子订单中的入住夜数、渠道商结算金额、番茄结算金额、客栈结算金额
     *
     * @param financeParentOrder
     */
    public void packFinanceSubOrder(FinanceParentOrder financeParentOrder) {
        // 子订单列表
        Set<FinanceOrder> channelOrderList = financeParentOrder.getChannelOrderList();
        if (!CollectionUtils.isEmpty(channelOrderList)) {
            for (FinanceOrder financeOrder : channelOrderList) {
                // 入住日期,格式yyyy-MM-dd
                Date checkInAt = financeOrder.getCheckInAt();
                // 退房日期,格式yyyy-MM-dd
                Date checkOutAt = financeOrder.getCheckOutAt();
                // 获得该订单的夜数
                int orderNights = DateUtil.getDifferDay(checkInAt, checkOutAt);
                financeOrder.setNights(orderNights);
                // 设置子订单关联的主订单对象，对main_id赋值
                financeOrder.setFinanceParentOrder(financeParentOrder);
            }
        }
    }

    /**
     * 获取结算月份
     *
     * @return
     */
    public String getSettlementTime(List<FinanceAccountPeriod> financeAccountPeriodList) {
        if (CollectionsUtil.isNotEmpty(financeAccountPeriodList)) {
            return financeAccountPeriodList.get(0).getSettlementTime();
        }
        return null;
    }

    /**
     * 获取结算月份
     *
     * @return
     */
    public String getSettlementTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        return DateFormatUtils.format(calendar, "yyyy-MM");
    }

    /**
     * 计算停留天数
     *
     * @param financeParentOrder
     * @return
     */
    public void buildStayDays(FinanceParentOrder financeParentOrder) {
        // 获取入住日期
        Date checkInAt = financeParentOrder.getCheckInAt();
        // 获得离店日期
        Date checkOutAt = financeParentOrder.getCheckOutAt();
        if (checkInAt != null && checkOutAt != null) {
            financeParentOrder.setStayDays(DateUtil.getDifferDay(checkInAt, checkOutAt));
        } else {
            financeParentOrder.setStayDays(-1);
            LOGGER.error("订单ID[" + financeParentOrder.getId() + "], 入住天数获取存在异常");
        }
    }

    /**
     * 计算提前预定时间
     *
     * @param financeParentOrder
     * @return
     */
    public void buildReservationDays(FinanceParentOrder financeParentOrder) {
        // 获取入住日期
        Date checkInAt = financeParentOrder.getCheckInAt();
        // 获取下单日期
        Date orderTime = financeParentOrder.getOrderTime();
        if (checkInAt != null && orderTime != null) {
            financeParentOrder.setReservationDays(DateUtil.getDifferDay(orderTime, checkInAt));
        } else {
            financeParentOrder.setReservationDays(-1);
            LOGGER.error("订单ID[" + financeParentOrder.getId() + "],预定天数获取存在异常");
        }
    }

    /**
     * 获取当前登录用户名
     *
     * @return
     */
    public String getCurrentUser() {
        return SpringSecurityUtil.getCurrentUser().getUsername();
    }

    /**
     * 返回全部特殊结算的账单的状态
     *
     * @return
     */
    public static String getSpecialOrderStatus() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'");
        stringBuilder.append(StringUtils.join(FinanceSpecialOrder.SPECIAL_STATUS, "','"));
        stringBuilder.append("'");
        return stringBuilder.toString();
    }

    /**
     * 根据关键字获取特殊订单状态
     *
     * @param key（debit：赔付，refund：退款，replenishment：补款）
     * @return
     */
    public static String getStatusByKey(String key) {
        String status = null;
        if (StringUtils.isNotBlank(key)) {
            if (FinanceSpecialOrder.STATUS_KEY_DEBIT.equals(key)) {
                status = FinanceSpecialOrder.DEBIT_STATUS;
            } else if (FinanceSpecialOrder.STATUS_KEY_REFUND.equals(key)) {
                status = FinanceSpecialOrder.REFUND_STATUS;
            } else if (FinanceSpecialOrder.STATUS_KEY_REPLENISHMENT.equals(key)) {
                status = FinanceSpecialOrder.REPLENISHMENT_STATUS;
            }
        }
        return status;
    }

    /**
     * 根据分销商ID、分销商名称、状态分组统计查询后，按照分销商ID对数据进行封装
     * @param financeChannelSettlementMap
     * @return
     */
    public Collection<ChannelSettlementData> combineMap(List<Map<String, Object>> financeChannelSettlementMap) {
        Map<Integer, ChannelSettlementData> result = new HashMap<>();
        for (Map<String, Object> map : financeChannelSettlementMap) {
            Integer channelId = Integer.parseInt(String.valueOf(map.get("id")));
            ChannelSettlementData data = result.get(channelId);
            if (data == null) {
                data = new ChannelSettlementData();
                data.setChannelId(Integer.parseInt(String.valueOf(map.get("id"))));
                data.setChannelName(String.valueOf(map.get("name")));
            }
            String status = String.valueOf(map.get("status"));
            if(isFilterStatus(status)) {
                // 设置订单总数量
                int orderAmount = data.getOrderAmount() + Integer.parseInt(String.valueOf(map.get("total")));
                data.setOrderAmount(orderAmount);
                // 设置客栈订单总金额
                BigDecimal innOrderAmount = data.getInnOrderAmount().add(NumberUtil.wrapNull((BigDecimal) map.get("orders")));
                data.setInnOrderAmount(innOrderAmount);
                // 设置分销商订单总金额
                BigDecimal channelOrderAmount = data.getChannelOrderAmount().add(NumberUtil.wrapNull((BigDecimal) map.get("channel")));
                data.setChannelOrderAmount(channelOrderAmount);
            }
            // 1 正常有效订单状态
            if ("1".equals(status)) {
                data.setChannelSettlementAmount(NumberUtil.wrapNull((BigDecimal) map.get("amount")));
                data.setInnSettlementAmount(NumberUtil.wrapNull((BigDecimal) map.get("inn")));
                data.setFqNormalOrderIncome(NumberUtil.wrapNull((BigDecimal) map.get("fq_settlement_amount")));
            } else if (FinanceSpecialOrder.DEBIT_STATUS.equals(status)) {
                data.setChannelDebit(NumberUtil.wrapNull((BigDecimal) map.get("channel_debit")));
                data.setInnPayment(NumberUtil.wrapNull((BigDecimal) map.get("inn_payment")));
            } else if (FinanceSpecialOrder.REFUND_STATUS.equals(status)) {
                data.setChannelRefund(NumberUtil.wrapNull((BigDecimal) map.get("channel_refund")));
                data.setInnRefund(NumberUtil.wrapNull((BigDecimal) map.get("inn_refund")));
            } else if (FinanceSpecialOrder.REPLENISHMENT_STATUS.equals(status)) {
                data.setTotalFqRefundCommission(NumberUtil.wrapNull((BigDecimal) map.get("total_fq_refund_commission")));
                data.setFqReplenishment(NumberUtil.wrapNull((BigDecimal) map.get("fq_replenishment")));
            }
            result.put(channelId, data);
        }
        return result.values();
    }



    /**
     * 是否是需要过滤的状态，1、66、77、88是需要结算的，其他状态不需要结算
     * @param status
     * @return
     */
    private static boolean isFilterStatus(String status) {
        List<String> specialStatus = Arrays.asList(FinanceSpecialOrder.SPECIAL_STATUS);
        List<String> list = new ArrayList<>(specialStatus);
        list.add(FinanceParentOrder.STATUS_ACCEPTED);
        if (list.contains(status)) {
            return true;
        }
        return false;
    }
}