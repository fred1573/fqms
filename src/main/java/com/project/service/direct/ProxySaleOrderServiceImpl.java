package com.project.service.direct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.direct.ProxySaleOrderForm;
import com.project.bean.direct.ProxySaleOrderVo;
import com.project.bean.direct.SubOrder;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.service.proxysale.ProxyOrderService;
import com.project.utils.*;
import com.project.utils.encode.PassWordUtil;
import com.project.utils.time.DateUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sam on 2015/11/18.
 */
@Service("proxySaleOrderService")
@Transactional
public class ProxySaleOrderServiceImpl implements ProxySaleOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ProxySaleOrderServiceImpl.class);
    @Autowired
    private ProxyOrderService proxyOrderService;

    @Override
    public void cancelOrder(String mark, String channelOrderNo, String channelId) {

        // 获取时间戳
        long timestamp = System.currentTimeMillis();
        // 获取签名
        String signature = PassWordUtil.getDirectSignature(timestamp);
        StringBuilder url = new StringBuilder();
        // 拼接接口请求域名、端口号
        url.append(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        Map<String, Object> paramMap = new HashMap<>();
        //拼接服务名称
        url.append(ApiURL.OMS_CANCEL_ORDER);
        paramMap.put("otaId", Constants.OMS_PROXY_PID);
        paramMap.put("channelId", channelId);
        paramMap.put("paidAmount", 0);
        paramMap.put("otaOrderNo", channelOrderNo);
        paramMap.put("signature", signature);
        paramMap.put("timestamp", timestamp);
        String object = new HttpUtil().httpPost(url.toString(), paramMap);
        if (null == object) {
            throw new RuntimeException("OMS接口返回数据为空");
        }
        JSONObject jsonObject = JSON.parseObject(object);
        String status = jsonObject.getString("status");
        if (!status.equals("200")) {
            throw new RuntimeException("OMS接口请求失败，原因" + jsonObject.getString("message"));
        }
    }

    @Override
    public JSONObject findProxySaleOrderFromOMS(ProxySaleOrderForm proxySaleOrderForm, boolean isPage) {
        List<Integer> innIdWithName = new ArrayList<>();
        String innIdWithNameStr = "";
        if (null != proxySaleOrderForm.getQueryType() && proxySaleOrderForm.getQueryType().equals("5")) {
            String value = proxySaleOrderForm.getQueryValue();
            if (null != value && value != "") {
                innIdWithName = proxyOrderService.findInnIdWithName(value);
                if (CollectionsUtil.isNotEmpty(innIdWithName)) {
                    for (Integer id : innIdWithName) {
                        if (null != id) {
                            innIdWithNameStr += id + ",";
                        }
                    }
                }
            }
        }
        // 设置默认查询开始时间
        if (StringUtils.isBlank(proxySaleOrderForm.getStartDate())) {
            proxySaleOrderForm.setStartDate(getCurrentDate());
        }
        // 设置默认查询结束时间
        if (StringUtils.isBlank(proxySaleOrderForm.getEndDate())) {
            proxySaleOrderForm.setEndDate(getCurrentDate());
        }
        // 设置默认时间查询类型
        String searchTimeType = proxySaleOrderForm.getSearchTimeType();
        if (StringUtils.isBlank(searchTimeType)) {
            proxySaleOrderForm.setSearchTimeType("CREATE");
        }
        String channelId = proxySaleOrderForm.getChannelId();
        if (StringUtils.isBlank(channelId)) {
            channelId = Constants.OMS_PROXY_PID;
        }
        String otaId = proxySaleOrderForm.getOtaId();
        if (StringUtils.isBlank(otaId)) {
            otaId = Constants.OMS_PROXY_PID;
        }

        // 获取时间戳
        long timestamp = System.currentTimeMillis();
        // 获取签名
        String signature = PassWordUtil.getDirectSignature(timestamp,otaId,Constants.OMS_DX_NAME,Constants.OMS_DX_PWD);
        if (otaId.equals(Constants.OMS_PROXY_CREDIT_PID)) {
            signature = PassWordUtil.getDirectSignature(timestamp, otaId, Constants.OMS_XYZ_NAME, Constants.OMS_XYZ_PWD);
        }
        StringBuilder url = new StringBuilder();
        // 拼接接口请求域名、端口号
        url.append(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        Map<String, Object> paramMap = new HashMap<>();
        if (isPage) {
            // 拼接服务名称
            url.append(ApiURL.OMS_QUERY_ORDER);
            paramMap.put("page", proxySaleOrderForm.getPage());
            paramMap.put("rows", proxySaleOrderForm.getRows());
        } else {
            url.append(ApiURL.OMS_QUERY_ALL_ORDER);
        }
        paramMap.put("signature", signature);
        paramMap.put("timestamp", timestamp);
        paramMap.put("otaId", otaId);
        paramMap.put("queryValue", proxySaleOrderForm.getQueryValue());
        paramMap.put("orderType", proxySaleOrderForm.getSearchTimeType());
        paramMap.put("channelId", channelId);
        paramMap.put("status", proxySaleOrderForm.getOrderStatus());
        paramMap.put("startDate", proxySaleOrderForm.getStartDate());
        paramMap.put("strategyType", proxySaleOrderForm.getStrategyType());
        paramMap.put("endDate", proxySaleOrderForm.getEndDate());
        paramMap.put("channelCode", proxySaleOrderForm.getChildChannelId());
        paramMap.put("queryType", proxySaleOrderForm.getQueryType());
        paramMap.put("innIdListStr", innIdWithNameStr);
        String result = new HttpUtil().httpPost(url.toString(), paramMap);
        if (StringUtils.isBlank(result)) {
            throw new RuntimeException("OMS接口没有响应");
        }
        JSONObject jsonObject = JSON.parseObject(result);
        String status = jsonObject.getString("status");
        if (!"200".equals(status)) {
            throw new RuntimeException("OMS接口请求失败,原因：" + jsonObject.getString("message"));
        }
        return jsonObject;
    }

    @Override
    public List<ProxySaleOrderVo> findProxySaleOrderVoList(JSONObject jsonObject) {
        Map<Integer, String> listMap = proxyOrderService.findUserWithInnId();
        List<ProxySaleOrderVo> orderList = null;
        JSONArray rows = jsonObject.getJSONArray("rows");
        String userName = null;
        if (!CollectionUtils.isEmpty(rows)) {
            orderList = new ArrayList<>();
            for (int i = 0; i < rows.size(); i++) {
                String order = rows.get(i).toString();
                ProxySaleOrderVo proxySaleOrderVo = JSONObject.parseObject(order, ProxySaleOrderVo.class);
                //合并子订单
                List<SubOrder> subOrders = combineOrder(proxySaleOrderVo.getChannelOrderList());
                proxySaleOrderVo.setChannelOrderList(subOrders);
                //获取客户经理
                userName = listMap.get(proxySaleOrderVo.getInnId());
                proxySaleOrderVo.setCustomerManager(userName);
                orderList.add(proxySaleOrderVo);
            }
        }
        return orderList;
    }

    /**
     * 根据入店离店日期获取住宿天数
     *
     * @param begin
     * @param end
     * @return
     */
    private String getNightNums(String begin, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long night = null;
        try {
            Date beginD = dateFormat.parse(begin);
            Date endD = dateFormat.parse(end);
            night = (endD.getTime() - beginD.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            logger.error("子订单合并获取住宿夜数-日期转化出错");
        }
        return night.toString();
    }

    /**
     * 获取最大日期
     *
     * @param dates
     * @return
     */
    private String getEndDate(List<String> dates) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> dateList = new ArrayList<>();
        for (String s : dates) {
            try {
                dateList.add(simpleDateFormat.parse(s));
            } catch (ParseException e) {
                logger.error("子订单合并获取最大时间-日期转化出错");
            }
        }

        Date endDate = dateList.get(0);
        for (int i = 1; i < dateList.size(); i++) {

            if (dateList.get(i).after(endDate)) {
                endDate = dateList.get(i);
            }
        }

        return simpleDateFormat.format(endDate);
    }

    /**
     * 获取最小日期
     *
     * @param dates
     * @return
     */
    private String getBeginDate(List<String> dates) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> dateList = new ArrayList<>();
        for (String s : dates) {
            try {
                dateList.add(simpleDateFormat.parse(s));
            } catch (ParseException e) {
                logger.error("子订单合并获取最小时间-日期转化出错");
            }
        }
        Date beginDate = dateList.get(0);
        for (int i = 1; i < dateList.size(); i++) {
            if (dateList.get(i).before(beginDate)) {
                beginDate = dateList.get(i);
            }
        }
        return simpleDateFormat.format(beginDate);
    }

    /**
     * 根据status找到需要合并的子订单
     *
     * @param list
     * @return
     */
    private Map<Integer, List<SubOrder>> combinOrder(List<SubOrder> list) {
        int a = 0;
        Map<Integer, List<SubOrder>> map = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus() > 0) {
                List<SubOrder> subOrders = new ArrayList<>();
                subOrders.add(list.get(i));
                SubOrder subOrder = list.get(i);
                for (int j = i + 1; j < list.size(); j++) {
                    if (subOrder.getStatus() == (list.get(j).getStatus()) && (list.get(j).getStatus()) > 0) {
                        subOrders.add(list.get(j));
                        list.get(j).setStatus(0);
                    }
                }
                map.put(a++, subOrders);
            }
        }

        return map;
    }

    /**
     * 判断日期是否连续
     *
     * @return
     */
    private boolean dateS(SubOrder subOrder1, SubOrder subOrder2) {
        if (isSamePriceAndRoomType(subOrder1, subOrder2)) {
            if (!subOrder1.isEqStatus() && !subOrder2.isEqStatus()) {
                if (subOrder1.getCheckInAt().equals(subOrder2.getCheckOutAt()) || subOrder2.getCheckInAt().equals(subOrder1.getCheckOutAt())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断房间类型(去除价格相等条件)
     *
     * @param subOrder1
     * @param subOrder2
     * @return
     */
    private boolean isSamePriceAndRoomType(SubOrder subOrder1, SubOrder subOrder2) {
        if (subOrder1 != null && subOrder2 != null) {

            String channelRoomTypeName1 = subOrder1.getChannelRoomTypeName();
            String channelRoomTypeName2 = subOrder2.getChannelRoomTypeName();
            if (StringUtils.isNotBlank(channelRoomTypeName1) && StringUtils.isNotBlank(channelRoomTypeName2)) {
                if (channelRoomTypeName1.equals(channelRoomTypeName2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 更改订单识别码
     *
     * @param list
     * @return
     */
    private List<SubOrder> updateStatus(List<SubOrder> list) {
        for (int a = 1; a <= list.size(); a++) {
            list.get(a - 1).setStatus(a);
        }
        // 筛选单价、方形、日期都相同的子订单
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (dateEq(list.get(i), list.get(j))) {
                    list.get(j).setStatus(list.get(i).getStatus());
                    list.get(i).setEqStatus(true);
                    list.get(j).setEqStatus(true);
                }
            }
        }
        // 筛选单价、方形、日期连续的子订单
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (dateS(list.get(i), list.get(j))) {
                    list.get(j).setStatus(list.get(i).getStatus());
                }
            }
        }

        return list;
    }

    /**
     * 判断日期相等
     *
     * @return
     */
    private boolean dateEq(SubOrder a, SubOrder b) {
        if (isSamePriceAndRoomType(a, b)) {
            boolean bi = a.getCheckInAt().equals(b.getCheckInAt());
            boolean bj = a.getCheckOutAt().equals(b.getCheckOutAt());
            return bi && bj;
        }
        return false;
    }

    /**
     * 合并订单
     * ``
     *
     * @param subOrders
     * @return
     */
    private SubOrder getCombineOrder(List<SubOrder> subOrders) {
        SubOrder sub = new SubOrder();
        if (subOrders.size() > 1) {
            SubOrder firstSub = subOrders.get(0);
            try {
                sub = (SubOrder) BeanUtils.cloneBean(firstSub);
            } catch (Exception e) {
                logger.error("子订单合并-克隆SubOrder对象出错");
            }

            //日期相同类型订单
            if (firstSub.isEqStatus()) {
                int nums = 0;
                for (SubOrder s : subOrders) {
                    nums += s.getRoomTypeNums();
                }
                //房间数需要重新计算
                sub.setRoomTypeNums(nums);
                return sub;
            }

            //日期连续类型订单
            List<String> dates = new ArrayList<>();
            for (SubOrder subOrder : subOrders) {
                dates.add(subOrder.getCheckInAt());
                dates.add(subOrder.getCheckOutAt());
            }
            String inTime = getBeginDate(dates);
            String outTime = getEndDate(dates);
            String nights = getNightNums(inTime, outTime);
            sub.setCheckInAt(inTime);
            sub.setCheckOutAt(outTime);
            sub.setNightNumber(nights);
            return sub;
        } else {
            return subOrders.get(0);
        }
    }


    /**
     * 合并子订单
     *
     * @param subOrders
     * @return
     */
    private List<SubOrder> combineOrder(List<SubOrder> subOrders) {
        if (CollectionsUtil.isNotEmpty(subOrders)) {
            //修改订单识别码
            List<SubOrder> list = updateStatus(subOrders);
            Map<Integer, List<SubOrder>> integerListMap = combinOrder(list);

            List<SubOrder> combineSub = new ArrayList<>();
            for (Iterator<Integer> keys = integerListMap.keySet().iterator(); keys.hasNext(); ) {
                Integer key = keys.next();
                List<SubOrder> subs = integerListMap.get(key);

                if (!subs.isEmpty()) {
                    //获取合并处理后的订单
                    SubOrder subOrder = getCombineOrder(subs);
                    combineSub.add(subOrder);
                }

            }
            return combineSub;
        }
        return null;
    }


    @Override
    public Map<String, String> findOrderTotalInfo(JSONObject jsonObject) {
        Map<String, String> dataMap = new HashMap<>();
        String totalResult = jsonObject.getString("result");
        if (StringUtils.isNotBlank(totalResult)) {
            JSONObject totalObject = JSON.parseObject(totalResult);
            dataMap.put("total", totalObject.getString("orderNums"));
            dataMap.put("nightNum", totalObject.getString("nightNum"));
            dataMap.put("totalPrice", totalObject.getString("totalPrice"));
            dataMap.put("totalExtraPrice", totalObject.getString("totalExtraPrice"));
        }
        return dataMap;
    }

    @Override
    public PageUtil getPage(JSONObject jsonObject, int pageSize, int currentPage) {
        PageUtil pageUtil = null;
        String total = jsonObject.getString("total");
        JSONArray rows = jsonObject.getJSONArray("rows");
        if (!CollectionUtils.isEmpty(rows)) {
            pageUtil = new PageUtil(pageSize, Integer.parseInt(total), currentPage);
        }
        return pageUtil;
    }

    @Override
    public Map<String, String> getChannelInfoFromOMS() {
        // 获取时间戳
        long timestamp = System.currentTimeMillis();
        // 获取签名
        String signature = PassWordUtil.getDirectSignature(timestamp);
        StringBuilder stringBuilder = new StringBuilder();
        // 拼接接口请求域名、端口号
        stringBuilder.append(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        // 拼接服务名称
        stringBuilder.append(ApiURL.OMS_QUERY_ORDER_OTA);
        stringBuilder.append("?signature=").append(signature);
        stringBuilder.append("&timestamp=").append(timestamp);
        stringBuilder.append("&pid=").append(Constants.OMS_PROXY_PID);
        stringBuilder.append("&otaId=").append(Constants.OMS_PROXY_PID);
        String result = new HttpUtil().get(stringBuilder.toString());
        Map<String, String> otaMap = new HashMap<>();
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if ("200".equals(status)) {
                JSONArray data = jsonObject.getJSONArray("rows");
                if (!CollectionUtils.isEmpty(data)) {
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject jsonObject1 = JSON.parseObject(data.get(i).toString());
                        String otaId = jsonObject1.getString("otaId");
                        String name = jsonObject1.getString("name");
                        if ("代销平台".equals(name)) {
                            continue;
                        }
                        otaMap.put(otaId, name);
                    }
                }
            }
        }
        return otaMap;
    }

    @Override
    public Map<String, String> getChildChannelInfoFromOMS(String parentChannelId) {
        if (parentChannelId == null) {
            return null;
        }
        // 获取时间戳
        long timestamp = System.currentTimeMillis();
        // 获取签名
        String signature = PassWordUtil.getDirectSignature(timestamp);
        StringBuilder stringBuilder = new StringBuilder();
        // 拼接接口请求域名、端口号
        stringBuilder.append(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        // 拼接服务名称
        stringBuilder.append(ApiURL.OMS_CHILD_OTA);
        stringBuilder.append("?signature=").append(signature);
        stringBuilder.append("&timestamp=").append(timestamp);
        stringBuilder.append("&pid=").append(parentChannelId);
        String result = new HttpUtil().get(stringBuilder.toString());
        Map<String, String> otaMap = new HashMap<>();
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if ("200".equals(status)) {
                JSONArray data = jsonObject.getJSONArray("data");
                if (!CollectionUtils.isEmpty(data)) {
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject jsonObject1 = JSON.parseObject(data.get(i).toString());
                        String channelCode = jsonObject1.getString("channelCode");
                        String channelCodeName = jsonObject1.getString("channelCodeName");
                        otaMap.put(channelCode, channelCodeName);
                    }
                }
            }
        }
        return otaMap;
    }

    @Override
    public void exportExcel(HttpServletResponse response, ProxySaleOrderForm proxySaleOrderForm) {
        JSONObject result = findProxySaleOrderFromOMS(proxySaleOrderForm, false);
        List<ProxySaleOrderVo> proxySaleOrderVoList = findProxySaleOrderVoList(result);
        buildExcel(response, proxySaleOrderVoList);
    }

    /**
     * 构建Excel数据
     *
     * @param response
     * @param proxySaleOrderVoList
     */
    private void buildExcel(HttpServletResponse response, List<ProxySaleOrderVo> proxySaleOrderVoList) {
        OutputStream os = null;
        try {
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.addHeader("Content-Disposition", new String(("attachment; filename=" + "代销订单" + ".xls").getBytes("GBK"), "ISO-8859-1")); // 针对中文文件名
            os = response.getOutputStream();
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 第一个sheet是总表，统计客栈全部数据
            HSSFSheet totalSheet = workbook.createSheet("代销订单");
            // 普通字体样式
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            // 加粗字体样式
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            totalSheet.autoSizeColumn(8, true);
            totalSheet.setDefaultColumnWidth(18);
            String[] header = {"分销商", "子分销商", "价格模式", "区域", "目的地", "订单状态", "客栈名称", "客栈ID", "代销经理", "分销商总价/预付金额", "番茄总调价", "分销商订单号", "OMS订单号", "客人姓名", "手机号码", "房型", "房间数", "夜数", "总间夜数", "入住时间", "离店时间", "下单日期"};
            // 构建表头
            buildExcelSheetHeader(totalSheet, header, boldCellStyle);
            fillExcelData(totalSheet, proxySaleOrderVoList, normalCellStyle);
            workbook.write(os);
        } catch (Exception e) {
            throw new RuntimeException("表格导出时出错!", e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 构建Excel表头
     *
     * @param sheet
     * @param header
     * @param hcs
     */
    private void buildExcelSheetHeader(HSSFSheet sheet, String[] header, HSSFCellStyle hcs) {
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(hcs);
        }
    }

    /**
     * 填充Excel表格数据
     *
     * @param sheet
     * @param proxySaleOrderVoList
     * @param hcs2
     */
    private void fillExcelData(HSSFSheet sheet, List<ProxySaleOrderVo> proxySaleOrderVoList, HSSFCellStyle hcs2) {
        hcs2.setWrapText(true);

        if (CollectionsUtil.isNotEmpty(proxySaleOrderVoList)) {
            String priceStrategyType;
            for (int i = 0; i < proxySaleOrderVoList.size(); i++) {
                ProxySaleOrderVo proxySaleOrderVo = proxySaleOrderVoList.get(i);
                // 去除表头，数据从第一行开始
                HSSFRow row = sheet.createRow(i + 1);
                HSSFCell cell = row.createCell(0);
                // 设置渠道名称
                cell.setCellValue(proxySaleOrderVo.getChannelName());
                // 设置子分销商名称
                cell = row.createCell(1);
                cell.setCellValue(proxySaleOrderVo.getChannelCodeName());
                // 设置价格模式
                cell = row.createCell(2);
                priceStrategyType = proxySaleOrderVo.getStrategyType();
                if (null != priceStrategyType) {
                    if (priceStrategyType.equals("1")) {
                        cell.setCellValue("精品(活动)");
                    }
                    if (priceStrategyType.equals("2")) {
                        cell.setCellValue("普通(卖)");
                    }
                    if (priceStrategyType.equals("3")) {
                        cell.setCellValue("普通(底)");
                    }

                }
                // 设置区域
                cell = row.createCell(3);
                cell.setCellValue(proxySaleOrderVo.getCity());
                //设置目的地
                cell = row.createCell(4);
                cell.setCellValue(proxySaleOrderVo.getRegionName());
                // 设置订单状态
                cell = row.createCell(5);
                cell.setCellValue(proxySaleOrderVo.getConName());
                // 设置客栈名称
                cell = row.createCell(6);
                cell.setCellValue(proxySaleOrderVo.getInnName());
                //设置客栈ID
                cell = row.createCell(7);
                cell.setCellValue(proxySaleOrderVo.getInnId());
                //设置客户经理
                cell = row.createCell(8);
                cell.setCellValue(proxySaleOrderVo.getCustomerManager());
                // 设置分销商总价、预付金额
                cell = row.createCell(9);
                cell.setCellValue(proxySaleOrderVo.getTotalAmount() + "/" + proxySaleOrderVo.getPaidAmount());
                //设置番茄总调价
                cell = row.createCell(10);
                cell.setCellValue(proxySaleOrderVo.getExtraPrice());
                // 设置分销商订单号
                cell = row.createCell(11);
                cell.setCellValue(proxySaleOrderVo.getChannelOrderNo());
                // 设置OMS订单号
                cell = row.createCell(12);
                cell.setCellValue(proxySaleOrderVo.getOrderNo());
                // 设置客人姓名
                cell = row.createCell(13);
                cell.setCellValue(proxySaleOrderVo.getUserName());
                // 设置手机号码
                cell = row.createCell(14);
                cell.setCellValue(proxySaleOrderVo.getContact());
                List<SubOrder> channelOrderList = proxySaleOrderVo.getChannelOrderList();
                if (CollectionsUtil.isNotEmpty(channelOrderList)) {
                    String channelRoomTypeName = "";
                    String roomTypeNums = "";
                    String nightNum = "";
                    String bookPrice = "";
                    String checkInAt = "";
                    String checkInOut = "";
                    int size = channelOrderList.size();
                    for (int j = 0; j < size; j++) {
                        SubOrder subOrder = channelOrderList.get(j);
                        channelRoomTypeName += subOrder.getChannelRoomTypeName();
                        if (j < size - 1) {
                            channelRoomTypeName += "\r\n";
                        }
                        roomTypeNums += subOrder.getRoomTypeNums();
                        if (j < size - 1) {
                            roomTypeNums += "\r\n";
                        }
                        nightNum += subOrder.getNightNumber();
                        if (j < size - 1) {
                            nightNum += "\r\n";
                        }
                        bookPrice += subOrder.getBookPrice();
                        if (j < size - 1) {
                            bookPrice += "\r\n";
                        }
                        checkInAt += subOrder.getCheckInAt();
                        if (j < size - 1) {
                            checkInAt += "\r\n";
                        }
                        checkInOut += subOrder.getCheckOutAt();
                        if (j < size - 1) {
                            checkInOut += "\r\n";
                        }
                    }
                    // 设置房型
                    cell = row.createCell(15);
                    cell.setCellValue(channelRoomTypeName);
                    // 设置房间数
                    cell = row.createCell(16);
                    cell.setCellValue(roomTypeNums);
                    // 设置夜数
                    cell = row.createCell(17);
                    cell.setCellValue(nightNum);
                    //总间夜数
                    cell = row.createCell(18);
                    cell.setCellValue(proxySaleOrderVo.getRoomNights());

                    // 设置入住时间
                    cell = row.createCell(19);
                    cell.setCellValue(checkInAt);
                    // 设置离店时间
                    cell = row.createCell(20);
                    cell.setCellValue(checkInOut);
                }
                // 设置下单日期
                cell = row.createCell(21);
                cell.setCellValue(proxySaleOrderVo.getOrderTime());
            }
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow hr = sheet.getRow(i + 1);
                if (hr != null) {
                    for (int k = 0; k < hr.getLastCellNum(); k++) {
                        HSSFCell hc = hr.getCell(k);
                        if (hc != null) {
                            hc.setCellStyle(hcs2);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取当前时间的字符串格式
     *
     * @return
     */
    private String getCurrentDate() {
        return DateUtil.format(new Date());
    }
}
