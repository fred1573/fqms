package com.project.service.finance;

import com.project.bean.finance.BarAndLineData;
import com.project.bean.finance.DataForm;
import com.project.bean.finance.PieData;
import com.project.dao.finance.FinanceOrderDao;
import com.project.entity.finance.FinanceParentOrder;
import com.project.utils.CollectionsUtil;
import com.project.utils.FinanceHelper;
import com.project.utils.time.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sam on 2015/12/24.
 */
@Service("dataService")
@Transactional
public class DataServiceImpl implements DataService {
    @Resource
    private FinanceOrderDao financeOrderDao;
    @Resource
    private FinanceHelper financeHelper;

    @Override
    public Map<String, Object> getStatisticsSaleData(DataForm dataForm) {
        Map<String, Object> dataMap = new HashMap<>();
        String beginDate = dataForm.getBeginDate();
        String endDate = dataForm.getEndDate();
        Integer channelId = dataForm.getChannelId();
        boolean isAcceptedOnly = dataForm.getIsAcceptedOnly();
        // 获取总订单数、总间夜数、总订单金额
        Map<String, Object> totalMap = financeOrderDao.selectOrderAmount(beginDate, endDate, null, isAcceptedOnly);
        // 查询指定分销商的订单数、总间夜数、总订单金额
        Map<String, Object> channelMap = financeOrderDao.selectOrderAmount(beginDate, endDate, channelId, isAcceptedOnly);

        // 渠道订单总数
        BigDecimal channelOrderAmount = getCountFromKey(channelMap, "orders");
        dataMap.put("channelOrderAmount", channelOrderAmount);
        // 渠道间夜数
        BigDecimal channelRoomNightAmount = getCountFromKey(channelMap, "room_nights");
        dataMap.put("channelRoomNightAmount", channelRoomNightAmount);
        // 渠道订单总金额
        BigDecimal channelPriceAmount = getCountFromKey(channelMap, "total_amount");
        dataMap.put("channelPriceAmount", channelPriceAmount);

        // 渠道订单量占比=渠道订单量/总订单量*100
        BigDecimal channelOrderAmountRatio = getRatioFromKey(totalMap, channelMap, "orders");
        dataMap.put("channelOrderAmountRatio", channelOrderAmountRatio);
        // 渠道间夜数占比=渠道间夜数/总间夜数*100
        BigDecimal channelRoomNightAmountRatio = getRatioFromKey(totalMap, channelMap, "room_nights");
        dataMap.put("channelRoomNightAmountRatio", channelRoomNightAmountRatio);
        // 渠道订单总额占比=渠道订单总金额/总订单金额*100
        BigDecimal channelPriceAmountRatio = getRatioFromKey(totalMap, channelMap, "total_amount");
        dataMap.put("channelPriceAmountRatio", channelPriceAmountRatio);

        // 渠道总间数
        BigDecimal roomsAmount = getCountFromKey(channelMap, "rooms");
        // 渠道总夜数
        BigDecimal nightsAmount = getCountFromKey(channelMap, "nights");
        // 渠道总提前订房天数
        BigDecimal reservationDaysAmount = getCountFromKey(channelMap, "reservation_days");
        // 渠道总停留天数
        BigDecimal stayDaysAmount = getCountFromKey(channelMap, "stay_days");


        // 获取已售客栈数量
        int soldInnCount = financeOrderDao.selectSoldInnCount(beginDate, endDate, channelId, isAcceptedOnly);
        dataMap.put("soldInnCount", soldInnCount);

        // 日平均间夜价格＝总金额／总间夜数
        BigDecimal dailyAverageRoomNightPrice = getTrade(channelPriceAmount, channelRoomNightAmount, 2);
        dataMap.put("dailyAverageRoomNightPrice", dailyAverageRoomNightPrice);

        // 日间夜价格中位数=将所有订单的单价排序，然后取最中间的数（如果是偶数则取中间的两个数然后除以2）
        BigDecimal midNum = getMidNum(beginDate, endDate, channelId, isAcceptedOnly);
        dataMap.put("midNum", midNum);

        // 订单间数比＝总间数／订单数
        BigDecimal orderRoomRatio = getPercentage(roomsAmount, channelOrderAmount, 2);
        dataMap.put("orderRoomRatio", orderRoomRatio);
        // 订单夜数比＝总夜数／订单数
        BigDecimal nightRoomRatio = getPercentage(nightsAmount, channelOrderAmount, 2);
        dataMap.put("nightRoomRatio", nightRoomRatio);
        // 平均提前订房天数=总（入住日期-下单日期）/订单数
        BigDecimal advanceBookDay = getTrade(reservationDaysAmount, channelOrderAmount, 2);
        dataMap.put("advanceBookDay", advanceBookDay);
        // 平均停留天数=总（离店日期-入住日期）/订单数
        BigDecimal averageStayDay = getTrade(stayDaysAmount, channelOrderAmount, 2);
        dataMap.put("averageStayDay", averageStayDay);
        return dataMap;
    }

    /**
     * 根据key查找渠道指定项目的统计结果
     *
     * @param channelMap 渠道订单统计数据结果集
     * @return
     */
    private BigDecimal getCountFromKey(Map<String, Object> channelMap, String key) {
        if (channelMap != null && !channelMap.isEmpty()) {
            String orders = String.valueOf(channelMap.get(key));
            if (StringUtils.isNotBlank(orders) && !orders.equals("null")) {
                return new BigDecimal(orders);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 根据key查找指定项目统计结果比率
     *
     * @param totalMap   总订单统计数据结果集
     * @param channelMap 渠道订单统计数据结果集
     * @return
     */
    private BigDecimal getRatioFromKey(Map<String, Object> totalMap, Map<String, Object> channelMap, String key) {
        // 获取总量
        BigDecimal totalOrderAmount = getCountFromKey(totalMap, key);
        // 获取渠道量
        BigDecimal channelOrderAmount = getCountFromKey(channelMap, key);
        return getPercentage(channelOrderAmount, totalOrderAmount, 2);
    }

    /**
     * 获取两个BigDecimal的商
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param accuracy 精度
     * @return
     */
    private BigDecimal getTrade(BigDecimal dividend, BigDecimal divisor, int accuracy) {
        if (!BigDecimal.ZERO.equals(divisor)) {
            return dividend.divide(divisor, accuracy, BigDecimal.ROUND_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取两个BigDecimal的百分比
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param accuracy 精度
     * @return
     */
    private BigDecimal getPercentage(BigDecimal dividend, BigDecimal divisor, int accuracy) {
        if (!BigDecimal.ZERO.equals(divisor)) {
            return dividend.divide(divisor, 2, BigDecimal.ROUND_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取房间价格中位数
     * 日间夜价格中位数=将所有订单的单价排序，然后取最中间的数（如果是偶数则取中间的两个数然后除以2）
     *
     * @param beginTime      下单的开始时间
     * @param endTime        下单的结束时间
     * @param channelId      渠道ID
     * @param isAcceptedOnly 是否只统计已接受订单（即status=1）
     * @return
     */
    private BigDecimal getMidNum(String beginTime, String endTime, Integer channelId, boolean isAcceptedOnly) {
        if (beginTime.equals(endTime)) {
            beginTime=beginTime+" 0:00:00";
            endTime=endTime+" 23:59:59";
        }
        List<BigDecimal> prices = financeOrderDao.getAllOrderPrices(beginTime, endTime, channelId, isAcceptedOnly);
        if (CollectionsUtil.isNotEmpty(prices)) {
            int size = prices.size();
            BigDecimal mid;
            if (size % 2 == 0) {
                int i = size / 2;
                mid = prices.get(i).add(prices.get(i - 1));
                mid = mid.divide(new BigDecimal(2), 2, BigDecimal.ROUND_UP);
            } else {
                mid = prices.get((size - 1) / 2);
            }
            return mid;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BarAndLineData getStatisticsBarData(DataForm dataForm) {
        BarAndLineData barAndLineData = new BarAndLineData();
        String beginDate = dataForm.getBeginDate();
        String endDate = dataForm.getEndDate();
        List<String> days = DateUtil.getDays(beginDate, endDate);
        // 获取X轴的天数
        barAndLineData.setAxis(days);
        List<List<String>> series = new ArrayList<>();
        List<String> amountsList = new ArrayList<>();
        List<String> ordersList = new ArrayList<>();
        List<String> nightsList = new ArrayList<>();
        // 获取订单金额 获取订单量 获取间夜量
        List<Map<String, Object>> list = financeOrderDao.selectOrderAmountList(beginDate, endDate, dataForm.getChannelId(), dataForm.getIsAcceptedOnly());
        Map<String, Map<String, Object>> dataMap = buildDataMapList(list);
        if (CollectionsUtil.isNotEmpty(days) && dataMap != null) {
            DecimalFormat format = new DecimalFormat("0.##");
            for (String day : days) {
                Map<String, Object> map = dataMap.get(day);
                if (map != null) {
                    String amountsStr = String.valueOf(map.get("amounts"));
                    if (StringUtils.isNotBlank(amountsStr)) {
                        amountsList.add(format.format(new BigDecimal(amountsStr)));
                    } else {
                        amountsList.add("0");
                    }
                    ordersList.add(String.valueOf(map.get("orders")));
                    nightsList.add(String.valueOf(map.get("nights")));
                } else {
                    amountsList.add("0");
                    ordersList.add("0");
                    nightsList.add("0");
                }
            }
        }
        series.add(amountsList);
        series.add(ordersList);
        series.add(nightsList);
        barAndLineData.setSeries(series);
        return barAndLineData;
    }

    /**
     * 将查询数据库返回的查询结果根据日期封装成Map，key为分组后的日期，value为该日期的统计结果map
     *
     * @param list
     * @return
     */
    private Map<String, Map<String, Object>> buildDataMapList(List<Map<String, Object>> list) {
        Map<String, Map<String, Object>> dataMap = null;
        if (CollectionsUtil.isNotEmpty(list)) {
            dataMap = new HashMap<>();
            for (Map<String, Object> map : list) {
                dataMap.put(String.valueOf(map.get("days")), map);
            }
        }
        return dataMap;
    }

    /**
     * 封装时间段和相应订单属性
     *
     * @param dataForm
     * @return
     */
    @Override
    public PieData getPieByTime(DataForm dataForm) {
        PieData pieData = new PieData();
        // 时间段
        List<String> timeList = new ArrayList<>();
        // 订单数
        List<String> orderAmountList = new ArrayList<>();
        List<Map<String, Object>> dataMapList = financeOrderDao.selectOrderAmountListByTime(dataForm.getBeginDate(), dataForm.getEndDate(), dataForm.getChannelId());
        if (CollectionsUtil.isNotEmpty(dataMapList)) {
            for (Map<String, Object> dataMap : dataMapList) {
                String times = getTimeFormatFromHour(String.valueOf(dataMap.get("times")));
                timeList.add(times);
                orderAmountList.add(String.valueOf(dataMap.get("orders")));
                dataMap.put("zt", times);
            }
        }
        pieData.setKeys(timeList);
        pieData.setValues(orderAmountList);
        pieData.setListMap(dataMapList);
        return pieData;
    }

    /**
     * 根据小时数生成时间的描述字段，如13-->13:01-14:00
     *
     * @param hour
     * @return
     */
    public String getTimeFormatFromHour(String hour) {
        int timeInt = (int) Double.parseDouble(hour);
        StringBuilder time = new StringBuilder();
        time.append(timeInt);
        time.append(":01");
        time.append("-");
        time.append(++timeInt);
        time.append(":00");
        return time.toString();
    }

    @Override
    public Map<String, Object> getStatisticsTableData(DataForm dataForm) {
        String beginDate = dataForm.getBeginDate();
        String endDate = dataForm.getEndDate();
        Integer channelId = dataForm.getChannelId();
        boolean isAcceptedOnly = dataForm.getIsAcceptedOnly();
        List<Map<String, Object>> roomNightRank = financeOrderDao.selectInnRank(beginDate, endDate, channelId, isAcceptedOnly, "room_nights", 10);
        List<Map<String, Object>> totalAmountRank = financeOrderDao.selectInnRank(beginDate, endDate, channelId, isAcceptedOnly, "total_amount", 10);
        List<Map<String, Object>> ordersRank = financeOrderDao.selectInnRank(beginDate, endDate, channelId, isAcceptedOnly, "orders", 10);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("roomNightRank", roomNightRank);
        dataMap.put("totalAmountRank", totalAmountRank);
        dataMap.put("ordersRank", ordersRank);
        return dataMap;
    }

    /**
     * 以状态为关键字封装订单数量
     */
    @Override
    public PieData getPieData(DataForm dataForm) {
        String beginDate = dataForm.getBeginDate();
        String endDate = dataForm.getEndDate();
        PieData pieData = new PieData();
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<Map<String, Object>> mapList = financeOrderDao.selectOrderAmountListByStatus(beginDate, endDate, dataForm.getChannelId());
        for (Map<String, Object> map : mapList) {
            keys.add(String.valueOf(map.get("zt")));
            values.add(String.valueOf(map.get("orders")));
        }
        pieData.setKeys(keys);
        pieData.setValues(values);
        pieData.setListMap(mapList);
        return pieData;
    }

    @Override
    public int getAllOrderAmount(DataForm dataForm) {
        return financeOrderDao.selectAllOrderAmount(dataForm.getBeginDate(), dataForm.getEndDate(), dataForm.getChannelId());
    }


    @Override
    public void repairData() {
        List<FinanceParentOrder> financeParentOrderList = financeOrderDao.getAll();
        if (CollectionsUtil.isNotEmpty(financeParentOrderList)) {
            for (FinanceParentOrder financeParentOrder : financeParentOrderList) {
                financeHelper.packFinanceOrderNewFields(financeParentOrder);
                financeOrderDao.save(financeParentOrder);
            }
        }
    }
}
