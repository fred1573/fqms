package com.project.service.finance;

import com.project.bean.bo.ChannelSettlementData;
import com.project.bean.excel.ExcelSheetBean;
import com.project.bean.finance.AjaxChannelReconciliation;
import com.project.bean.finance.ChannelReconciliation;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.finance.*;
import com.project.entity.finance.*;
import com.project.entity.ota.OtaInfo;
import com.project.service.ota.OtaInfoService;
import com.project.utils.*;
import com.tomasky.msp.client.model.BalanceModel;
import com.tomasky.msp.client.model.PendingNotify;
import com.tomasky.msp.client.service.impl.MessageManageServiceImpl;
import com.tomasky.msp.client.support.MessageBuilder;
import com.tomasky.msp.enumeration.SmsChannel;
import com.tomasky.msp.model.WeChatMessage;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单业务逻辑处理类
 * Created by 番茄桑 on 2015/8/31.
 */
@Service("financeOrderService")
@Transactional
public class FinanceOrderServiceImpl implements FinanceOrderService {
    @Resource
    private FinanceOperationLogDao financeOperationLogDao;
    @Resource
    private FinanceAccountPeriodDao financeAccountPeriodDao;
    @Resource
    private FinanceOrderDao financeOrderDao;
    @Resource
    private FinanceHelper financeHelper;
    @Resource
    private FinanceChannelSettlementDao financeChannelSettlementDao;
    @Resource
    private FinanceInnSettlementDao financeInnSettlementDao;
    @Resource
    private OtaInfoService otaInfoService;
    @Resource
    private FinanceInnChannelSettlementDao financeInnChannelSettlementDao;
    @Resource
    private FinanceInnSettlementInfoDao financeInnSettlementInfoDao;
    @Resource
    private FinanceArrearInnService financeArrearInnService;
    @Resource
    private FinanceSpecialOrderDao financeSpecialOrderDao;
    @Resource
    private FinanceManualOrderDao financeManualOrderDao;
    @Resource
    private FinanceOutNormalExportService financeOutNormalExportService;
    @Resource
    private FinanceOutSpecialOrDelayExportService financeOutSpecialOrDelayExportService;
    @Resource
    private FinanceOutArrearsExportService financeOutArrearsExportService;
    @Resource
    private FinanceArrearInnDao financeArrearInnDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceOrderServiceImpl.class);

    @Override
    public void createFinanceOrder(FinanceParentOrder financeParentOrder) {
        updateFinanceOrder(financeParentOrder);
    }

    @Override
    public void updateFinanceOrder(FinanceParentOrder financeParentOrder) {
        // 封装订单表的属性
        financeHelper.packFinanceOrder(financeParentOrder);
        financeOrderDao.save(financeParentOrder);
    }

    @Override
    public void createFinanceOrderWithPeriod(FinanceParentOrder financeParentOrder) {
        FinanceParentOrder oldFinanceOrder = financeOrderDao.findById(financeParentOrder.getId());
        //更新数据库中的financeParentOrder
        if (oldFinanceOrder != null) {
            oldFinanceOrder.setStatus(financeParentOrder.getStatus());
            // 同步更新接口返回的子渠道
            oldFinanceOrder.setChannelId(financeParentOrder.getChannelId());
            oldFinanceOrder.setChannelName(financeParentOrder.getChannelName());
            String settlementTime = financeParentOrder.getSettlementTime();
            oldFinanceOrder.setSettlementTime(settlementTime);
            oldFinanceOrder.setProduceTime(settlementTime);
            oldFinanceOrder.setSettlementStatus("0");
            oldFinanceOrder.setChannelCode(financeParentOrder.getChannelCode());
            financeOrderDao.save(oldFinanceOrder);
        } else {
            // 封装订单表的属性
            updateFinanceOrder(financeParentOrder);
        }
    }

    @Override
    public void repairFinanceOrder(FinanceParentOrder financeParentOrder) {
        FinanceParentOrder oldFinanceOrder = financeOrderDao.findById(financeParentOrder.getId());
        if (oldFinanceOrder != null) {
            oldFinanceOrder.setSettlementTime(financeParentOrder.getSettlementTime());
            financeOrderDao.save(oldFinanceOrder);
        } else {
            // 封装订单表的属性
            updateFinanceOrder(financeParentOrder);
        }
    }

    @Override
    public List<FinanceChannelSettlement> getFinanceParentOrderByChannel(String settlementTime) {
        return financeChannelSettlementDao.selectFinanceChannelSettlement(settlementTime);
    }

    @Override
    public Page<FinanceChannelSettlement> getFinanceParentOrderByChannel(Page<FinanceChannelSettlement> page, String settlementTime, String channelName, String auditStatus, Boolean isArrival, boolean isPage) {
        return financeChannelSettlementDao.selectFinanceChannelSettlement(page, settlementTime, channelName, auditStatus, isArrival, isPage);
    }

    @Override
    public Map<String, Object> getFinanceChannelSettlementCount(String settlementTime, Boolean isArrival) {
        if (StringUtils.isNotBlank(settlementTime)) {
            Map<String, Object> dataMap = financeChannelSettlementDao.getFinanceChannelSettlementCount(settlementTime, isArrival);
            Map<String, Object> arrivedataMap = financeChannelSettlementDao.getFinanceChannelSettlementCount(settlementTime, true);
            dataMap.put("arrivalChannels", arrivedataMap.get("channels"));
            dataMap.put("arrivalAmounts", arrivedataMap.get("amounts"));
            return dataMap;
        }
        return null;
    }

    @Override
    public Page<FinanceParentOrder> findChannelIncomeOrderList(Page<FinanceParentOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String auditStatus, Boolean isArrival, Short priceStrategy, boolean isPage) {
        return financeOrderDao.selectFinanceParentOrderPage(page, innId, channelId, settlementTime, channelOrderNo, auditStatus, isArrival, priceStrategy, isPage);
    }

    @Override
    public Map<String, Object> getChannelOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String auditStatus, Short priceStrategy) {
        List<Map<String, Object>> maps = financeOrderDao.selectChannelOrderCount(innId, channelId, settlementTime, channelOrderNo, auditStatus, priceStrategy, null);
        if (!CollectionsUtil.isEmpty(maps)) {
            return maps.get(0);
        }
        return null;
    }

    @Override
    public Page<FinanceInnSettlement> findFinanceInnSettlementList(Page<FinanceInnSettlement> page, String innName, String settlementTime, String confirmStatus, String settlementStatus, Boolean isTagged, boolean isPage, Boolean isMatch, String status) {
        Page<FinanceInnSettlement> financeInnSettlementPage = financeInnSettlementDao.selectFinanceInnSettlementList(page, innName, settlementTime, confirmStatus, settlementStatus, isTagged, isPage, status, isMatch);
        if (financeInnSettlementPage != null) {
            List<FinanceInnSettlement> result = (List<FinanceInnSettlement>) financeInnSettlementPage.getResult();
            if (CollectionsUtil.isNotEmpty(result)) {
                for (FinanceInnSettlement financeInnSettlement : result) {
                    FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
                    if (financeInnSettlementInfo != null) {
                        Integer innId = financeInnSettlementInfo.getId();
                        FinanceInnSettlementInfo innInfo = financeHelper.getInnInfo(innId);
                        if (innInfo != null) {
                            BeanUtil.copy(innInfo, financeInnSettlementInfo);
                        }
                        financeInnSettlement.setFinanceInnSettlementInfo(financeInnSettlementInfo);
                        financeInnSettlementDao.save(financeInnSettlement);
                        financeInnSettlement.setPayment(getInnChannelRealPayment(innId, settlementTime, null));
                    }
                }
            }
        }
        return financeInnSettlementPage;
    }

    /**
     * 获取实付金额
     *
     * @param innId
     * @param settlementTime
     * @param isMatch
     * @return
     */
    private BigDecimal getInnChannelRealPayment(Integer innId, String settlementTime, Boolean isMatch) {
        List<Map<String, Object>> mapList = financeInnChannelSettlementDao.selectTotalPayment(innId, settlementTime, isMatch);
        if (CollectionsUtil.isNotEmpty(mapList)) {
            Map<String, Object> dataMap = mapList.get(0);
            Object payment = dataMap.get("payment");
            if (payment != null) {
                return new BigDecimal(String.valueOf(payment));
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Map<String, Object> getFinanceInnSettlementCount(String settlementTime, String settlementStatus, String status) {
        List<Map<String, Object>> maps = financeInnSettlementDao.selectFinanceInnSettlementCount(settlementTime, settlementStatus, status);
        if (!CollectionsUtil.isEmpty(maps)) {
            return maps.get(0);
        }
        return null;
    }

    @Override
    public Map<String, Object> getInnOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String auditStatus, Short priceStrategy, Boolean isArrival) {
        List<Map<String, Object>> maps = financeOrderDao.selectInnOrderCount(innId, channelId, settlementTime, channelOrderNo, auditStatus, priceStrategy, isArrival);
        if (!CollectionsUtil.isEmpty(maps)) {
            return maps.get(0);
        }
        return null;
    }

    @Override
    public void updateFinanceChannelSettlementStatus(Integer id, Integer channelId, String settlementTime) {
        // 记录操作日志
        FinanceChannelSettlement financeChannelSettlement = financeChannelSettlementDao.findFinanceChannelSettlementById(id);
        if (financeChannelSettlement == null) {
            throw new RuntimeException("渠道对账对象不存在");
        }
        financeOperationLogDao.save(getFinanceOperationLogReceived(financeChannelSettlement));
        // 更新渠道对账表
        financeChannelSettlementDao.updateFinanceChannelSettlementArrival(id);
        // 更新订单表
        financeOrderDao.updateFinanceParentOrderArrival(channelId, settlementTime);
    }

    /**
     * 根据渠道对账对象构造操作日志对象
     *
     * @param financeChannelSettlement 渠道对账对象
     * @return
     */
    private FinanceOperationLog getFinanceOperationLogReceived(FinanceChannelSettlement financeChannelSettlement) {
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setChannelId(financeChannelSettlement.getChannelId());
        String channelName = financeChannelSettlement.getChannelName();
        String settlementTimeOld = financeChannelSettlement.getSettlementTime();
        financeOperationLog.setChannelName(channelName);
        StringBuilder stringBuilder = new StringBuilder("已确认收到：");
        stringBuilder.append(channelName + ",");
        stringBuilder.append(settlementTimeOld + ",");
        stringBuilder.append("共计" + financeChannelSettlement.getTotalOrder() + "个订单,");
        stringBuilder.append(financeChannelSettlement.getChannelSettlementAmount() + "元");
        financeOperationLog.setOperateContent(stringBuilder.toString());
        financeOperationLog.setOperateType("2");
        financeOperationLog.setOperateUser(getCurrentUser());
        financeOperationLog.setSettlementTime(settlementTimeOld);
        return financeOperationLog;
    }

    @Override
    public void updateInnSettlementStatus(Integer id, String settlementStatus, String settlementTime,Integer innId) {
        if (StringUtils.isBlank(settlementStatus)) {
            throw new RuntimeException("客栈确认状态不能为空");
        }
        FinanceInnSettlement financeInnSettlement = financeInnSettlementDao.selectFinanceInnSettlementById(id);
        if (financeInnSettlement == null) {
            throw new RuntimeException("客栈对账对象不存在");
        }
        FinanceOperationLog financeOperationLogSettlement = getFinanceOperationLogSettlement(financeInnSettlement, settlementStatus);
        financeOperationLogDao.save(financeOperationLogSettlement);
        financeInnSettlementDao.updateInnSettlement(id, settlementStatus);
        financeOrderDao.updateOrderWithBalance(settlementTime, innId, settlementStatus);
        // 如果是确认结算，发送微信、短信
        if ("1".equals(settlementStatus)) {
            //发送微信通知
            sendSettlementMessage(financeInnSettlement);
            //发送短信通知
            sendSettlementSms(financeInnSettlement);
        }
    }

    @Override
    public void updateFinanceInnSettlementTag(Integer id, Boolean isTagged) {
        financeInnSettlementDao.updateFinanceInnSettlementTag(id, isTagged);
    }

    /**
     * 根据客栈对账对象构造操作日志对象
     *
     * @param financeInnSettlement 客栈对账对象
     * @return
     */
    private FinanceOperationLog getFinanceOperationLogSettlement(FinanceInnSettlement financeInnSettlement, String settlementStatus) {
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        String settlementTime = financeInnSettlement.getSettlementTime();
        financeOperationLog.setSettlementTime(settlementTime);
        financeOperationLog.setOperateUser(getCurrentUser());
        financeOperationLog.setOperateType("4");
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
        financeOperationLog.setInnId(financeInnSettlementInfo.getId());
        String innName = financeInnSettlementInfo.getInnName();
        financeOperationLog.setInnName(innName);
        String settlementStatusStr = "已结算";
        if ("2".equals(settlementStatus)) {
            settlementStatusStr = "纠纷延期";
        }
        StringBuilder stringBuilder = new StringBuilder("【" + innName + "】");
        stringBuilder.append(settlementTime + ",");
        stringBuilder.append("结算状态修改为:" + settlementStatusStr);
        stringBuilder.append(",共计" + financeInnSettlement.getTotalOrder() + "个订单,");
        stringBuilder.append(financeInnSettlement.getInnSettlementAmount() + "元");
        financeOperationLog.setOperateContent(stringBuilder.toString());
        return financeOperationLog;
    }

    @Override
    public void updateFinanceInnSettlementStatus(Integer innId, String settlementTime) {
        financeInnSettlementDao.updateFinanceInnSettlementConfirm(innId, settlementTime);
    }

    /**
     * 封装获取的番茄展示金额
     *
     * @param mapList
     * @return
     */
    private Map<Short, BigDecimal> packFqTemp(List<Map<String, Object>> mapList) {
        Map<Short, BigDecimal> map = new HashMap<>();
        if (CollectionsUtil.isNotEmpty(mapList)) {
            for (Map<String, Object> m : mapList) {
                Short id = (Short) m.get("id");
                BigDecimal fqTemp = (BigDecimal) m.get("fq");
                map.put(id, fqTemp);
            }
        }
        return map;
    }

    @Override
    public void createFinanceChannelSettlementList(String settlementTime) {
        try {
            long begin = System.currentTimeMillis();
            List<Map<String, Object>> financeChannelSettlementMap = financeOrderDao.getFinanceChannelSettlement(settlementTime, true);
            List<Map<String, Object>> fqTemp = financeOrderDao.getFqTemp(settlementTime);
            Map<Short, BigDecimal> map1 = packFqTemp(fqTemp);
            if (!CollectionsUtil.isEmpty(financeChannelSettlementMap)) {
                // 查询数据库中原有的渠道结算记录
                List<FinanceChannelSettlement> financeChannelSettlementList = financeChannelSettlementDao.selectFinanceChannelSettlement(settlementTime);
                if (CollectionsUtil.isNotEmpty(financeChannelSettlementList)) {
                    // 封装最新的渠道结算统计数据
                    Map<Integer, Map<String, Object>> effectiveIChannelSettlement = getEffectiveInnSettlement(financeChannelSettlementMap);
                    // 用于保存数据库中已有，单最新的统计查询中没有的渠道ID
                    List<Integer> deleteInnIdList = new ArrayList<>();
                    for (FinanceChannelSettlement financeChannelSettlement : financeChannelSettlementList) {
                        Integer channelId = financeChannelSettlement.getChannelId();
                        Map<String, Object> map = effectiveIChannelSettlement.get(channelId);
                        if (map == null) {
                            deleteInnIdList.add(channelId);
                        }
                    }
                    if (CollectionsUtil.isNotEmpty(deleteInnIdList)) {
                        String deleteInnIds = StringUtils.join(deleteInnIdList.toArray(), ",");
                        // 删除无订单的分销商统计信息数据
                        financeChannelSettlementDao.batchDeleteFinanceChannelSettlement(settlementTime, deleteInnIds);
                    }
                }

                Collection<ChannelSettlementData> channelSettlementDatas = financeHelper.combineMap(financeChannelSettlementMap);

                for (ChannelSettlementData data : channelSettlementDatas) {
                    // 获取渠道ID
                    Integer channelId = data.getChannelId();
                    FinanceChannelSettlement financeChannelSettlement = financeChannelSettlementDao.selectFinanceChannelSettlement(channelId, settlementTime);
                    if (financeChannelSettlement == null) {
                        financeChannelSettlement = new FinanceChannelSettlement();
                    }
                    financeChannelSettlement.setChannelId(channelId);
                    // 获取渠道名称
                    financeChannelSettlement.setChannelName(data.getChannelName());

                    // 获取渠道结算金额
                    BigDecimal channelSettlementAmount = data.getChannelSettlementAmount();
                    financeChannelSettlement.setChannelSettlementAmount(channelSettlementAmount);

                    // 获取渠道扣番茄金额（赔付)
                    BigDecimal channelDebit = data.getChannelDebit();
                    financeChannelSettlement.setChannelDebit(channelDebit);
                    // 获取渠道扣番茄退款金额
                    BigDecimal channelRefund = data.getChannelRefund();
                    financeChannelSettlement.setChannelRefund(channelRefund);

                    // 番茄退款佣金
                    BigDecimal totalFqRefundCommission = data.getTotalFqRefundCommission();
                    BigDecimal fqTe = map1.get(channelId.shortValue());
                    if (null == fqTe) {
                        fqTe = BigDecimal.ZERO;
                    }
                    financeChannelSettlement.setFqTemp(fqTe);
                    financeChannelSettlement.setChannelRealAmount(channelSettlementAmount.subtract(channelDebit).subtract(channelRefund).subtract(totalFqRefundCommission).add(fqTe));
                    // 获取番茄实际收入
                    Map<String, Object> objectMap = financeOrderDao.findRealChannelSettlement(settlementTime, channelId);
                    financeChannelSettlement.setFqRealIncome((BigDecimal) objectMap.get("real"));
                    // 设置结算时间
                    financeChannelSettlement.setSettlementTime(settlementTime);
                    // 设置无订单赔付总金额
                    financeChannelSettlement.setNoOrderDebitAmount(NumberUtil.wrapNull(financeManualOrderDao.getAmount(channelId, settlementTime)));
                    packRefundAmount(financeChannelSettlement, settlementTime, channelId);
                    // 保存渠道结算对象
                    financeChannelSettlementDao.save(financeChannelSettlement);
                }
            }
            LOGGER.info("创建或更新指定账期的分销商结算记录：" + (System.currentTimeMillis() - begin));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装补款明细
     *
     * @param financeChannelSettlement
     * @param settlementTime
     * @param channelId
     * @return
     */
    private void packRefundAmount(FinanceChannelSettlement financeChannelSettlement, String settlementTime, Integer channelId) {
        List<Map<String, Object>> refundDetail = financeInnChannelSettlementDao.getRefundDetail(settlementTime);
        if (CollectionsUtil.isNotEmpty(refundDetail)) {
            Integer refundId;
            BigDecimal refunded;
            BigDecimal current;
            BigDecimal next;
            for (Map<String, Object> objectMap1 : refundDetail) {
                refundId = (Integer) objectMap1.get("id");
                if (null != refundId && refundId == channelId) {
                    refunded = (BigDecimal) objectMap1.get("refunded");
                    current = (BigDecimal) objectMap1.get("cur");
                    next = (BigDecimal) objectMap1.get("next");
                    financeChannelSettlement.setRefundedAmount(refunded);
                    financeChannelSettlement.setCurrentRefundAmount(current);
                    financeChannelSettlement.setNextRefundAmount(next);
                }
            }
        }
    }

    public Map<Integer, SettlementAmount> combineSettlementAmount(List<Map<String, Object>> statisticMap) {
        if (CollectionsUtil.isNotEmpty(statisticMap)) {
            Map<Integer, SettlementAmount> maps = new HashMap<>();
            Integer innId;
            BigDecimal channelAmount;
            BigDecimal innAmount;
            for (Map<String, Object> map : statisticMap) {
                innId = (Integer) map.get("id");
                channelAmount = (BigDecimal) map.get("channel");
                innAmount = (BigDecimal) map.get("inn");
                SettlementAmount settlementAmount = new SettlementAmount();
                settlementAmount.setChannelAmount(channelAmount);
                settlementAmount.setInnAmount(innAmount);
                maps.put(innId, settlementAmount);
            }
            return maps;
        }
        return null;
    }

    public Map<Integer, BigDecimal> staticticChannelRealSettlement(List<Map<String, Object>> mapList) {
        if (CollectionsUtil.isNotEmpty(mapList)) {
            Map<Integer, BigDecimal> smap = new HashMap<>();
            Integer innId;
            BigDecimal realAmount;
            for (Map<String, Object> map : mapList) {
                innId = (Integer) map.get("id");
                realAmount = (BigDecimal) map.get("channel");
                smap.put(innId, realAmount);
            }
            return smap;
        }

        return null;
    }

    @Override
    public void createFinanceInnSettlementList(String settlementTime) {
        try {
            long begin = System.currentTimeMillis();
            List<Map<String, Object>> financeInnSettlementMap = financeOrderDao.getFinanceInnSettlement(settlementTime);
            //统计正常结算
            List<Map<String, Object>> statisticMap = financeOrderDao.statisticAmount(settlementTime);
            //客栈正常结算统计
            Map<Integer, SettlementAmount> integerSettlementAmountMap = combineSettlementAmount(statisticMap);
            //统计实际结算
            List<Map<String, Object>> mapList = financeOrderDao.staticticRealSettlement(settlementTime);
            Map<Integer, BigDecimal> integerRealBigDecimalMap = staticticChannelRealSettlement(mapList);
            if (!CollectionsUtil.isEmpty(financeInnSettlementMap)) {
                // 查询数据库中原有的结算记录
                List<FinanceInnSettlement> financeInnSettlements = financeInnSettlementDao.selectFinanceInnSettlementBySettlementTime(settlementTime, false);
                Map<Integer, FinanceInnSettlement> innSettlementMap = null;
                if (CollectionsUtil.isNotEmpty(financeInnSettlements)) {
                    innSettlementMap = new HashMap<>();
                    // 封装最新的客栈结算统计数据
                    Map<Integer, Map<String, Object>> effectiveInnSettlement = getEffectiveInnSettlement(financeInnSettlementMap);
                    // 用于保存数据库中已有，单最新的统计查询中没有的客栈ID
                    List<Integer> deleteInnIdList = new ArrayList<>();
                    for (FinanceInnSettlement financeInnSettlement : financeInnSettlements) {
                        innSettlementMap.put(financeInnSettlement.getFinanceInnSettlementInfo().getId(), financeInnSettlement);
                        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
                        Integer innId = financeInnSettlementInfo.getId();
                        Map<String, Object> map = effectiveInnSettlement.get(innId);
                        if (map == null) {
                            deleteInnIdList.add(innId);
                        }
                    }
                    if (CollectionsUtil.isNotEmpty(deleteInnIdList)) {
                        String deleteInnIds = StringUtils.join(deleteInnIdList.toArray(), ",");
                        // 删除无订单的客栈统计信息数据
                        financeInnSettlementDao.batchDeleteFinanceInnSettlement(settlementTime, deleteInnIds);
                    }
                }
                // 删除账期为本期且挂账的记录
                financeArrearInnService.deleteFinanceArrearsInn(settlementTime);
                for (Map<String, Object> map : financeInnSettlementMap) {
                    int innId = Integer.parseInt(String.valueOf(map.get("id")));
                    FinanceInnSettlement financeInnSettlement = null;
                    if (innSettlementMap != null) {
                        financeInnSettlement = innSettlementMap.get(innId);
                    }
                    if (financeInnSettlement == null) {
                        financeInnSettlement = new FinanceInnSettlement();
                        financeInnSettlement.setConfirmStatus("0");
                        financeInnSettlement.setSettlementStatus("0");
                        // 数据库查询是否有结算信息
                        FinanceInnSettlementInfo innInfo = financeInnSettlementInfoDao.financeInnSettlementInfoWithId(innId);
                        if (innInfo == null) {
                            innInfo = financeHelper.getInnInfo(innId);
                        }
                        financeInnSettlement.setFinanceInnSettlementInfo(innInfo);
                    }
                    BigInteger special = (BigInteger) map.get("special");
                    int sum = special.intValue();
                    if (null != special && sum > 0) {
                        financeInnSettlement.setIsSpecial(true);
                    } else {
                        financeInnSettlement.setIsSpecial(false);
                    }
                    //封装数据
                    packageData(financeInnSettlement, map, settlementTime, integerSettlementAmountMap, integerRealBigDecimalMap);
                    //挂账处理
                    isArrears(innId, financeInnSettlement, settlementTime);
                    //特殊订单处理
                    isSpecialInn(innId, settlementTime, financeInnSettlement);

                    financeInnSettlementDao.save(financeInnSettlement);
                }
            }
            LOGGER.info("创建或更新指定账期的客栈结算记录用时：" + (System.currentTimeMillis() - begin));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BigDecimal warp(BigDecimal bigDecimal) {
        return bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
    }

    //封装数据
    public void packageData(FinanceInnSettlement financeInnSettlement, Map<String, Object> map, String settlementTime, Map<Integer, SettlementAmount> settlementAmountMap, Map<Integer, BigDecimal> realAmountMap) {
        financeInnSettlement.setFqSettlementAmount((BigDecimal) map.get("fqs"));
        financeInnSettlement.setTotalOrder(Integer.parseInt(String.valueOf(map.get("total"))));
        Integer innId = financeInnSettlement.getFinanceInnSettlementInfo().getId();

        SettlementAmount settlementAmount = settlementAmountMap.get(innId);
        BigDecimal channelSettlementAmount = BigDecimal.ZERO;
        BigDecimal innSettlementAmount = BigDecimal.ZERO;
        if (null != settlementAmount) {
            channelSettlementAmount = warp(settlementAmount.getChannelAmount());
            innSettlementAmount = warp(settlementAmount.getInnAmount());
        }
        financeInnSettlement.setChannelSettlementAmount(channelSettlementAmount);
        //获取正常订单客栈结算金额
        financeInnSettlement.setInnSettlementAmount(innSettlementAmount);

        //特殊结算分销商应扣款金额

        BigDecimal channelRealSettlement = realAmountMap.get(financeInnSettlement.getFinanceInnSettlementInfo().getId());
        if (channelRealSettlement == null) {
            channelRealSettlement = BigDecimal.ZERO;
        }
        channelRealSettlement = financeInnSettlement.getChannelSettlementAmount().subtract(channelRealSettlement);
        //渠道商实际结算金额
        financeInnSettlement.setChannelRealSettlement(channelRealSettlement);
        // 设置客栈订单总金额
        financeInnSettlement.setTotalAmount((BigDecimal) map.get("orders"));
        financeInnSettlement.setSettlementTime(settlementTime);
        financeInnSettlement.setRoomNights(Integer.parseInt(String.valueOf(map.get("rns"))));
        // 设置分销商订单总额
        financeInnSettlement.setChannelAmount((BigDecimal) map.get("channel"));

        BigDecimal subtract = BigDecimal.ZERO;
        BigDecimal ip = (BigDecimal) map.get("ip");
        BigDecimal ir = (BigDecimal) map.get("ir");
        BigDecimal fr = (BigDecimal) map.get("fr");
        if (ip == null) {
            ip = BigDecimal.ZERO;
        }
        if (ir == null) {
            ir = BigDecimal.ZERO;
        }
        if (fr == null) {
            fr = BigDecimal.ZERO;
        }
        financeInnSettlement.setInnPayment(ip);
        financeInnSettlement.setFqReplenishment(fr);
        financeInnSettlement.setRefundAmount(ir);

        BigDecimal add = (ip).add(ir);
        subtract = add.subtract(fr);

        //经赔付处理后的客栈结算金额
        if (financeInnSettlement.getInnSettlementAmount() != null) {
            financeInnSettlement.setAfterPaymentAmount(financeInnSettlement.getInnSettlementAmount().subtract(subtract));
            BigDecimal realPayment = getInnChannelRealPayment(financeInnSettlement.getFinanceInnSettlementInfo().getId(), settlementTime, null);
            BigDecimal decimal = financeInnSettlement.getAfterPaymentAmount();
            if (null == decimal) {
                decimal = BigDecimal.ZERO;
            }
            if (realPayment != null) {
                //账实是否相符
                if (realPayment.compareTo(decimal) == 0) {
                    financeInnSettlement.setIsMatch(true);
                } else {
                    financeInnSettlement.setIsMatch(false);
                }
            } else {
                financeInnSettlement.setIsMatch(false);
            }
        }

    }

    //特殊订单处理
    public void isSpecialInn(Integer innId, String settlementTime, FinanceInnSettlement financeInnSettlement) {
        List<FinanceArrearInn> financeArrearsInnList = financeArrearInnDao.selectPastFinanceArrearInn(innId, settlementTime);
        if (CollectionsUtil.isEmpty(financeArrearsInnList)) {
            //如果实际应结算金额小于0，挂账
            if (financeInnSettlement.getAfterPaymentAmount() != null) {
                if (financeInnSettlement.getAfterPaymentAmount().compareTo(BigDecimal.ZERO) < 0) {
                    financeInnSettlement.setIsArrears("3");
                    FinanceArrearInn financeArrearInn = new FinanceArrearInn();
                    financeArrearInn.setInnId(financeInnSettlement.getFinanceInnSettlementInfo().getId());
                    financeArrearInn.setArrearRemaining(financeInnSettlement.getAfterPaymentAmount().multiply(new BigDecimal(-1)));
                    financeArrearInn.setArrearPast(BigDecimal.ZERO);
                    financeArrearInn.setSettlementTime(settlementTime);
                    financeArrearInnDao.save(financeArrearInn);
                    financeInnSettlement.setArrearsRemaining(financeInnSettlement.getAfterPaymentAmount().multiply(new BigDecimal(-1)));
                    financeInnSettlement.setArrearsPast(BigDecimal.ZERO);
                }
            }

        }
    }

    //挂账处理
    public void isArrears(Integer innId, FinanceInnSettlement financeInnSettlement, String settlementTime) {
        //判断是否挂账
        List<FinanceArrearInn> financeArrearsInnList = financeArrearInnDao.selectPastFinanceArrearInn(innId, settlementTime);
        if (CollectionsUtil.isNotEmpty(financeArrearsInnList)) {
            FinanceArrearInn financeArrearInn = financeArrearsInnList.get(0);

            if (financeArrearInn.getArrearRemaining() != null) {
                //最新挂账记录挂账金额情况
                int i = financeArrearInn.getArrearRemaining().compareTo(BigDecimal.ZERO);
                //客栈挂账是否大于0
                if (i == 1) {
                    FinanceArrearInn financeArrearInnNew = new FinanceArrearInn();

                    BigDecimal afterPaymentAmount = financeInnSettlement.getAfterPaymentAmount();
                    if (afterPaymentAmount != null) {
                        int j = afterPaymentAmount.compareTo(BigDecimal.ZERO);
                        //本期结算小于0，客栈继续挂账
                        if (j <= 0) {
                            financeInnSettlement.setIsArrears("3");

                            //本期结算为负，挂账金额+结算金额
                            if (j != 0) {
                                BigDecimal afterAmount = afterPaymentAmount.multiply(new BigDecimal(-1));
                                financeInnSettlement.setArrearsRemaining(financeArrearInn.getArrearRemaining().add(afterAmount));
                                financeArrearInnNew.setArrearRemaining(financeArrearInn.getArrearRemaining().add(afterAmount));
                                financeInnSettlement.setArrearsPast(financeArrearInn.getArrearRemaining());
                                financeArrearInnNew.setArrearPast(financeArrearInn.getArrearRemaining());

                            } else {
                                financeInnSettlement.setArrearsRemaining(financeArrearInn.getArrearRemaining());
                                financeArrearInnNew.setArrearRemaining(financeArrearInn.getArrearRemaining());
                                financeInnSettlement.setArrearsPast(financeArrearInn.getArrearPast());
                                financeArrearInnNew.setArrearPast(financeArrearInn.getArrearPast());

                            }
                            financeArrearInnNew.setInnId(financeArrearInn.getInnId());

                            financeArrearInnNew.setSettlementTime(settlementTime);
                            financeArrearInnNew.setStatus("3");
                        }
                        //本期结算大于0，进行平账
                        else {
                            //客栈结算
                            if (afterPaymentAmount.compareTo(financeArrearInn.getArrearRemaining()) >= 0) {
                                //平账结算,保存最新挂账记录
                                financeArrearInnNew.setArrearPast(financeArrearInn.getArrearRemaining());
                                financeArrearInnNew.setInnId(financeArrearInn.getInnId());
                                financeArrearInnNew.setArrearRemaining(BigDecimal.ZERO);
                                financeArrearInnNew.setSettlementTime(settlementTime);
                                financeArrearInnNew.setStatus("1");
                                //客栈平账
                                financeInnSettlement.setIsArrears("1");
                                financeInnSettlement.setArrearsPast(financeArrearInn.getArrearRemaining());
                                financeInnSettlement.setArrearsRemaining(BigDecimal.ZERO);
                                //平账后客栈结算金额
                                financeInnSettlement.setAfterArrearsAmount(afterPaymentAmount.subtract(financeArrearInn.getArrearRemaining()));
                                BigDecimal realPayment = getInnChannelRealPayment(financeInnSettlement.getFinanceInnSettlementInfo().getId(), settlementTime, null);
                                BigDecimal arrears = financeInnSettlement.getAfterArrearsAmount();

                                if (null == arrears) {
                                    arrears = BigDecimal.ZERO;
                                }
                                //账实是否相符
                                if (realPayment.compareTo(arrears) == 0) {
                                    financeInnSettlement.setIsMatch(true);
                                } else {
                                    financeInnSettlement.setIsMatch(false);
                                }
                            }
                            //部分平账
                            else {
                                financeArrearInnNew.setArrearPast(financeArrearInn.getArrearRemaining());
                                financeArrearInnNew.setInnId(financeArrearInn.getInnId());
                                financeArrearInnNew.setArrearRemaining(financeArrearInn.getArrearRemaining().subtract(afterPaymentAmount));
                                financeInnSettlement.setArrearsPast(financeArrearInn.getArrearRemaining());
                                financeInnSettlement.setArrearsRemaining(financeArrearInn.getArrearRemaining().subtract(afterPaymentAmount));
                                financeArrearInnNew.setSettlementTime(settlementTime);
                                financeArrearInnNew.setStatus("2");
                                financeInnSettlement.setIsArrears("2");
                                //部分平账后客栈结算金额
                                financeInnSettlement.setAfterArrearsAmount(afterPaymentAmount.subtract(financeArrearInn.getArrearRemaining()));
                            }
                        }
                    }

                    financeArrearInnDao.save(financeArrearInnNew);
                }
            } else {
                financeInnSettlement.setIsArrears("0");
            }
        } else {
            financeInnSettlement.setIsArrears("0");
        }
    }

    /**
     * 根据账期查询正常订单的客栈结算金额和分销商结算金额
     *
     * @param settlementTime
     * @return
     */
    private Map<String, SettlementAmount> getNormalInnChannelSettlementMap(String settlementTime) {
        // 正常订单结算金额
        List<Map<String, Object>> mapList = financeOrderDao.statisticChannelAmount(settlementTime);
        Map<String, SettlementAmount> stringSettlementAmountMap = null;
        if (CollectionsUtil.isNotEmpty(mapList)) {
            Integer innId;
            Short channelId;
            BigDecimal channelAmount;
            BigDecimal innAmount;
            String ids;
            stringSettlementAmountMap = new HashMap<>();
            for (Map<String, Object> map : mapList) {
                innId = (Integer) map.get("id");
                channelId = (Short) map.get("channelid");
                channelAmount = (BigDecimal) map.get("channel");
                innAmount = (BigDecimal) map.get("inn");
                SettlementAmount settlementAmount = new SettlementAmount();
                settlementAmount.setInnAmount(innAmount);
                settlementAmount.setChannelAmount(channelAmount);
                ids = String.valueOf(innId) + "-" + String.valueOf(channelId);
                stringSettlementAmountMap.put(ids, settlementAmount);
            }
        }
        return stringSettlementAmountMap;
    }

    /**
     * 统计特殊账单中分销商结算金额
     *
     * @param settlementTime
     * @return
     */
    private Map<String, BigDecimal> getSpecialChannelSettlementMap(String settlementTime) {
        // 统计特殊订单结算
        List<Map<String, Object>> map1 = financeOrderDao.statisticChannelRealSettlement(settlementTime);
        Map<String, BigDecimal> decimalMap = null;
        if (CollectionsUtil.isNotEmpty(map1)) {
            Integer innId;
            Short channelId;
            String ids;
            BigDecimal realAmount;
            decimalMap = new HashMap<>();
            for (Map<String, Object> map : map1) {
                innId = (Integer) map.get("id");
                channelId = (Short) map.get("channel_id");
                ids = String.valueOf(innId) + "-" + String.valueOf(channelId);
                realAmount = (BigDecimal) map.get("channel");
                decimalMap.put(ids, realAmount);
            }
        }
        return decimalMap;
    }

    /**
     * 根据账期查询番茄补款记录
     *
     * @param settlementTime
     * @return
     */
    private Map<String, BigDecimal> getFqRefundContractsMap(String settlementTime) {
        Map<String, BigDecimal> fqRefundContractsMap = null;
        // 查询番茄补款记录
        List<Map<String, Object>> dataMap = financeOrderDao.getFqRefundContractsAmount(settlementTime);
        if (CollectionsUtil.isNotEmpty(dataMap)) {
            fqRefundContractsMap = new HashMap<>();
            for (Map<String, Object> map : dataMap) {
                String innId = String.valueOf(map.get("inn_id"));
                String channelId = String.valueOf(map.get("channel_id"));
                String contactsStatus = String.valueOf(map.get("contacts_status"));
                Object amountObj = map.get("amount");
                BigDecimal amount = BigDecimal.ZERO;
                if (amountObj != null) {
                    amount = new BigDecimal(String.valueOf(amountObj));
                }
                fqRefundContractsMap.put(innId + "-" + channelId + "-" + contactsStatus, amount);
            }
        }
        return fqRefundContractsMap;
    }

    @Override
    public void createFinanceInnChannelSettlementList(String settlementTime) {
        try {
            long begin = System.currentTimeMillis();
            List<Map<String, Object>> financeInnSettlementMap = financeOrderDao.getFinanceInnChannelSettlement(settlementTime);
            if (!CollectionsUtil.isEmpty(financeInnSettlementMap)) {
                // 统计正常订单结算金额
                Map<String, SettlementAmount> channelSettlementAmountMap = getNormalInnChannelSettlementMap(settlementTime);
                // 统计特殊账单中分销商结算金额
                Map<String, BigDecimal> decimalMap = getSpecialChannelSettlementMap(settlementTime);
                // 查询数据库中原有的结算记录
                List<FinanceInnChannelSettlement> financeInnChannelSettlements = financeInnChannelSettlementDao.selectFinanceInnChannelSettlementBySettlementTime(settlementTime);
                // 查询番茄补款记录
                Map<String, BigDecimal> fqRefundContractsMap = getFqRefundContractsMap(settlementTime);
                Map<String, FinanceInnChannelSettlement> financeInnChannelSettlementMap = null;
                // 删除已经没有有效订单的结算记录
                if (CollectionsUtil.isNotEmpty(financeInnChannelSettlements)) {
                    financeInnChannelSettlementMap = new HashMap<>();
                    // 封装最新的客栈结算统计数据
                    Map<String, Map<String, Object>> effectiveInnSettlement = getEffectiveInnChannelSettlement(financeInnSettlementMap);
                    // 用于保存数据库中已有，但最新的统计查询中没有的客栈ID
                    List<Integer> deleteInnIdList = new ArrayList<>();
                    for (FinanceInnChannelSettlement financeInnChannelSettlement : financeInnChannelSettlements) {
                        Integer innId = financeInnChannelSettlement.getFinanceInnSettlementInfo().getId();
                        Integer channelId = financeInnChannelSettlement.getChannelId();
                        financeInnChannelSettlementMap.put(innId + "-" + channelId, financeInnChannelSettlement);
                        Map<String, Object> map = effectiveInnSettlement.get(innId + "-" + channelId);
                        if (map == null) {
                            deleteInnIdList.add(financeInnChannelSettlement.getId());
                        }
                    }
                    if (CollectionsUtil.isNotEmpty(deleteInnIdList)) {
                        String ids = StringUtils.join(deleteInnIdList.toArray(), ",");
                        // 删除无订单的客栈统计信息数据
                        financeInnChannelSettlementDao.batchDeleteFinanceInnChannelSettlement(ids);
                    }
                }
                for (Map<String, Object> map : financeInnSettlementMap) {
                    int innId = Integer.parseInt(String.valueOf(map.get("id")));
                    int channelId = Integer.parseInt(String.valueOf(map.get("channel_id")));
                    String ids = String.valueOf(innId) + "-" + String.valueOf(channelId);
                    SettlementAmount settlementAmount = channelSettlementAmountMap.get(ids);
                    FinanceInnChannelSettlement financeInnChannelSettlement = null;
                    if (financeInnChannelSettlementMap != null) {
                        financeInnChannelSettlement = financeInnChannelSettlementMap.get(ids);
                    }
                    if (financeInnChannelSettlement == null) {
                        financeInnChannelSettlement = new FinanceInnChannelSettlement();
                        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlementInfoDao.financeInnSettlementInfoWithId(innId);
                        if (financeInnSettlementInfo == null) {
                            financeInnSettlementInfo = financeHelper.getInnInfo(innId);
                        }
                        financeInnChannelSettlement.setFinanceInnSettlementInfo(financeInnSettlementInfo);
                    }
                    financeInnChannelSettlement.setFqSettlementAmount((BigDecimal) map.get("fqs"));
                    financeInnChannelSettlement.setTotalOrder(Integer.parseInt(String.valueOf(map.get("total"))));

                    BigDecimal channelSettlementAmount = BigDecimal.ZERO;
                    BigDecimal innSettlementAmount = BigDecimal.ZERO;
                    if (null != settlementAmount) {
                        channelSettlementAmount = warp(settlementAmount.getChannelAmount());
                        innSettlementAmount = warp(settlementAmount.getInnAmount());
                    }

                    financeInnChannelSettlement.setInnSettlementAmount(innSettlementAmount);
                    financeInnChannelSettlement.setChannelSettlementAmount(channelSettlementAmount);

                    // 设置客栈订单总额
                    financeInnChannelSettlement.setTotalAmount((BigDecimal) map.get("orders"));
                    // 设置渠道订单总额
                    financeInnChannelSettlement.setChannelAmount((BigDecimal) map.get("channel"));

                    financeInnChannelSettlement.setSettlementTime(settlementTime);
                    financeInnChannelSettlement.setRoomNights(Integer.parseInt(String.valueOf(map.get("rns"))));
                    financeInnChannelSettlement.setChannelId(Integer.parseInt(String.valueOf(map.get("channel_id"))));
                    financeInnChannelSettlement.setInnPayment((BigDecimal) map.get("ip"));
                    financeInnChannelSettlement.setFqReplenishment((BigDecimal) map.get("fr"));
                    financeInnChannelSettlement.setRefundAmount((BigDecimal) map.get("ir"));
                    financeInnChannelSettlement.setFqBearAmount((BigDecimal) map.get("fb"));
                    financeInnChannelSettlement.setFqIncomeAmount((BigDecimal) map.get("fi"));
                    financeInnChannelSettlement.setFqRefundCommissionAmount((BigDecimal) map.get("frc"));
                    financeInnChannelSettlement.setChannelName(String.valueOf(map.get("name")));
                    financeInnChannelSettlement.setFqTemp((BigDecimal) map.get("temp"));
                    //往来款清零重新统计
                    financeInnChannelSettlement.setAftFqRefundContacts(BigDecimal.ZERO);
                    financeInnChannelSettlement.setCurFqRefundContacts(BigDecimal.ZERO);
                    if (fqRefundContractsMap != null) {
                        // 获取后期挂1
                        BigDecimal aftFqRefundContacts = BigDecimal.ZERO;
                        BigDecimal temp1 = fqRefundContractsMap.get(ids + "-" + 1);
                        if (temp1 != null) {
                            aftFqRefundContacts = temp1;
                        }
                        // 获取本期平2
                        BigDecimal curFqRefundContacts = BigDecimal.ZERO;
                        BigDecimal temp2 = fqRefundContractsMap.get(ids + "-" + 2);
                        if (temp2 != null) {
                            curFqRefundContacts = temp2;
                        }
                        financeInnChannelSettlement.setCurFqRefundContacts(curFqRefundContacts);
                        financeInnChannelSettlement.setAftFqRefundContacts(aftFqRefundContacts);
                    }
                    //分销商扣款金额
                    BigDecimal decimal = decimalMap.get(ids);
                    financeInnChannelSettlement.setChannelRealSettlementAmount(
                            NumberUtil.wrapNull(financeInnChannelSettlement.getChannelSettlementAmount())
                                    .subtract(NumberUtil.wrapNull(decimal)));

                    financeInnChannelSettlement.setFqNormalIncome((BigDecimal) map.get("fqreal"));
                    //判断客栈是否有特殊订单
                    BigInteger sum = (BigInteger) map.get("special");
                    int intSum = sum.intValue();
                    if (intSum > 0) {
                        financeInnChannelSettlement.setIsSpecial(true);
                    } else {
                        financeInnChannelSettlement.setIsSpecial(false);
                    }
                    BigDecimal bigDecimal1 = financeInnChannelSettlement.getInnSettlementAmount();
                    BigDecimal pay = financeInnChannelSettlement.getInnPayment();
                    BigDecimal refund = financeInnChannelSettlement.getRefundAmount();
                    BigDecimal fq = financeInnChannelSettlement.getFqReplenishment();
                    if (null == pay) {
                        pay = BigDecimal.ZERO;
                    }
                    if (null == refund) {
                        refund = BigDecimal.ZERO;
                    }
                    if (null == fq) {
                        fq = BigDecimal.ZERO;
                    }
                    BigDecimal realPay = financeInnChannelSettlement.getInnSettlementAmount().subtract(pay.add(refund.subtract(fq)));
                    financeInnChannelSettlement.setInnRealSettlement(realPay);
                    financeInnChannelSettlementDao.save(financeInnChannelSettlement);
                }
            } else {
                financeInnChannelSettlementDao.deleteWithSettlementTime(settlementTime);
            }
            LOGGER.info("创建或更新指定账期的客栈分销商结算记录：" + (System.currentTimeMillis() - begin));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 将数据库中统计查询的客栈统计数据，以分销商ID为key封装成新的map
     *
     * @param dataList
     * @return
     */
    private Map<Integer, Map<String, Object>> getEffectiveInnSettlement(List<Map<String, Object>> dataList) {
        Map<Integer, Map<String, Object>> channelIdMap = null;
        if (CollectionsUtil.isNotEmpty(dataList)) {
            channelIdMap = new HashMap<>();
            for (Map<String, Object> map : dataList) {
                channelIdMap.put(Integer.parseInt(String.valueOf(map.get("id"))), map);
            }
        }
        return channelIdMap;
    }

    /**
     * 客栈渠道结算对象集合封装为Map，提高遍历效率
     *
     * @param dataList
     * @return
     */
    private Map<String, Map<String, Object>> getEffectiveInnChannelSettlement(List<Map<String, Object>> dataList) {
        Map<String, Map<String, Object>> innChannelMap = null;
        if (CollectionsUtil.isNotEmpty(dataList)) {
            innChannelMap = new HashMap<>();
            for (Map<String, Object> map : dataList) {
                String channelId = String.valueOf(map.get("channel_id"));
                String InnId = String.valueOf(map.get("id"));
                innChannelMap.put(InnId + "-" + channelId, map);
            }
        }
        return innChannelMap;
    }

    @Override
    public void exportChannelOrder(HttpServletResponse response, String exportSettlementTime, Short exportChannelId) {
        OutputStream os = null;
        try {
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.addHeader("Content-Disposition", new String(("attachment; filename=" + "分销商结算-" + exportSettlementTime + ".xls").getBytes("GBK"), "ISO-8859-1")); // 针对中文文件名
            os = response.getOutputStream();
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 构建标题单元格样式
            HSSFCellStyle hcs = ExcelExportUtil.buildTitleCellStyle(workbook);
            // 构建普通单元格样式
            HSSFCellStyle hcs2 = ExcelExportUtil.buildNormalCellStyle(workbook);
            // 第一个sheet是总表，统计客栈全部数据
            HSSFSheet totalSheet = workbook.createSheet("总表");
            fillChannelTotalSheetData(totalSheet, exportSettlementTime, hcs, hcs2);
            // 构建单元格数据
            List<ExcelSheetBean> excelSheetBeans = buildChannelOrderData(exportChannelId, exportSettlementTime);
            if (CollectionsUtil.isNotEmpty(excelSheetBeans)) {
                for (ExcelSheetBean esb : excelSheetBeans) {
                    if (esb != null) {
                        HSSFSheet sheet = null;
                        String sheetName = StringUtils.defaultString(ExcelUtil.getSheetNameByInnName(esb.getSheetName()));
                        sheet = workbook.createSheet(sheetName);
                        sheet.autoSizeColumn(8, true);
                        sheet.setDefaultColumnWidth(18);

                        ExcelExportUtil.createExcelSheet(esb.getTableHeader(), esb.getPropertySequence(), esb.getList(), sheet, esb.getClassName(), hcs, hcs2);
                    }
                }
            }
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

    @Override
    public void asynchronousExportChannelOrder(HttpServletRequest request, String settlementTime, Short exportChannelId) throws Exception {
        FileOutputStream fileOutputStream = null;
        try {
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 构建标题单元格样式
            HSSFCellStyle hcs = ExcelExportUtil.buildTitleCellStyle(workbook);
            // 构建普通单元格样式
            HSSFCellStyle hcs2 = ExcelExportUtil.buildNormalCellStyle(workbook);
            // 第一个sheet是总表，统计客栈全部数据
            HSSFSheet totalSheet = workbook.createSheet("总表");
            fillChannelTotalSheetData(totalSheet, settlementTime, hcs, hcs2);
            // 构建单元格数据
            List<ExcelSheetBean> excelSheetBeans = buildChannelOrderData(exportChannelId, settlementTime);
            if (CollectionsUtil.isNotEmpty(excelSheetBeans)) {
                for (ExcelSheetBean esb : excelSheetBeans) {
                    if (esb != null) {
                        HSSFSheet sheet = null;
                        String sheetName = StringUtils.defaultString(ExcelUtil.getSheetNameByInnName(esb.getSheetName()));
                        sheet = workbook.createSheet(sheetName);
                        sheet.autoSizeColumn(8, true);
                        sheet.setDefaultColumnWidth(18);

                        ExcelExportUtil.createExcelSheet(esb.getTableHeader(), esb.getPropertySequence(), esb.getList(), sheet, esb.getClassName(), hcs, hcs2);
                    }
                }
            }
            String fileName = "分销商结算-" + settlementTime + "V" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls";
            fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
            String realPath = request.getSession().getServletContext().getRealPath("/") + "download";
            File file = new File(realPath + "/" + fileName);
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            throw new RuntimeException("表格导出时出错!", e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 构建渠道Excel总表
     *
     * @param totalSheet           渠道总表的sheet
     * @param exportSettlementTime 结算周期
     */
    private void fillChannelTotalSheetData(HSSFSheet totalSheet, String exportSettlementTime, HSSFCellStyle hcs1, HSSFCellStyle hcs2) {
        totalSheet.autoSizeColumn(8, true);
        totalSheet.setDefaultColumnWidth(18);
        // 查询渠道结算的统计信息
        Map<String, Object> dataMap = financeChannelSettlementDao.getFinanceChannelSettlementCount(exportSettlementTime, null);
        Map<String, Object> arrivedataMap = financeChannelSettlementDao.getFinanceChannelSettlementCount(exportSettlementTime, true);
        dataMap.put("arrivalChannels", arrivedataMap.get("channels"));
        dataMap.put("arrivalAmounts", arrivedataMap.get("amounts"));
        if (MapUtils.isEmpty(dataMap)) {
            throw new RuntimeException(exportSettlementTime + "没有渠道结算数据");
        }
        // 第一行展示渠道结算的统计数据
        HSSFRow row = totalSheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("合计：" + dataMap.get("channels"));
        cell = row.createCell(1);
        cell.setCellValue(String.valueOf(dataMap.get("orders")));
        cell = row.createCell(2);
        cell.setCellValue(String.valueOf(dataMap.get("amounts")));
        renderedExcel(totalSheet, hcs2, 0);
        // 第二行展示表头
        row = totalSheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("渠道商");
        cell = row.createCell(1);
        cell.setCellValue("订单总数");
        cell = row.createCell(2);
        cell.setCellValue("渠道商结算金额");
        List<FinanceChannelSettlement> financeChannelSettlements = financeChannelSettlementDao.selectFinanceChannelSettlement(exportSettlementTime);
        if (CollectionsUtil.isEmpty(financeChannelSettlements)) {
            throw new RuntimeException(exportSettlementTime + "没有渠道结算数据");
        }
        renderedExcel(totalSheet, hcs1, 1);
        // 第三行开始展示渠道结算的汇总信息
        for (int i = 0; i < financeChannelSettlements.size(); i++) {
            FinanceChannelSettlement financeChannelSettlement = financeChannelSettlements.get(i);
            row = totalSheet.createRow(i + 2);
            cell = row.createCell(0);
            cell.setCellValue(financeChannelSettlement.getChannelName());
            cell = row.createCell(1);
            cell.setCellValue(financeChannelSettlement.getTotalOrder());
            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getChannelSettlementAmount()));
        }
        renderedExcel(totalSheet, hcs2, 2);
    }

    @Override
    public void exportInnOrder(HttpServletResponse response, Integer innId, String settlementTime) throws Exception {
        List<FinanceInnSettlement> financeInnSettlementList = new ArrayList<>();
        List<List<FinanceParentOrder>> financeParentOrderList = new ArrayList<>();
        if (innId != null) {
            // 获取客栈结算数据
            FinanceInnSettlement financeInnSettlement = financeInnSettlementDao.selectFinanceInnSettlement(innId, settlementTime);
            // 获得结算订单集合
            List<FinanceParentOrder> parentOrderList = financeOrderDao.findFinanceParentOrderByInnId(innId, settlementTime);
            financeInnSettlementList.add(financeInnSettlement);
            financeParentOrderList.add(parentOrderList);
        } else {
            // 查询指定结算月份需要结算的客栈ID集合
            List<Integer> innIdList = financeOrderDao.getSettlementInnId(settlementTime);
            if (!CollectionsUtil.isEmpty(innIdList)) {
                for (Integer inn : innIdList) {
                    // 获取客栈结算数据
                    FinanceInnSettlement financeInnSettlement = financeInnSettlementDao.selectFinanceInnSettlement(inn, settlementTime);
                    if (financeInnSettlement != null) {
                        financeInnSettlementList.add(financeInnSettlement);
                        // 获得结算订单集合
                        List<FinanceParentOrder> parentOrderList = financeOrderDao.findFinanceParentOrderByInnId(inn, settlementTime);
                        financeParentOrderList.add(parentOrderList);
                    }
                }
            }
        }
        createFinanceExcel(response, financeInnSettlementList, financeParentOrderList);
    }

    @Override
    public void batchExportInnOrder(HttpServletRequest request, String settlementTime, String status) throws Exception {
        List<List<FinanceInnChannelSettlement>> financeInnChannelSettlementList = new ArrayList<>();
        // 查询指定结算月份需要结算的客栈ID集合
        List<FinanceInnSettlement> list = financeInnSettlementDao.findExportInn(settlementTime, status);
        Integer innId = null;
        if (CollectionsUtil.isNotEmpty(list)) {
            for (FinanceInnSettlement financeInnSettlement : list) {
                innId = financeInnSettlement.getFinanceInnSettlementInfo().getId();
                financeInnSettlement.setPayment(getInnChannelRealPayment(innId, settlementTime, null));
                List<FinanceInnChannelSettlement> innChannelSettlementList = financeInnChannelSettlementDao.findFinanceInnChannelSettlement(innId, settlementTime);
                if (CollectionsUtil.isNotEmpty(innChannelSettlementList)) {
                    financeInnChannelSettlementList.add(innChannelSettlementList);
                }
            }
        }
        //正常结算客栈
        if (StringUtils.isNotBlank(status) && status.equals(FinanceInnSettlement.NORMAL_STATUS)) {
            financeOutNormalExportService.createFinanceExcel(request, list, financeInnChannelSettlementList);
        }
        if (StringUtils.isNotBlank(status) && status.equals(FinanceInnSettlement.SPECIAL_STATUS)) {
            financeOutSpecialOrDelayExportService.createFinanceOutSpecialExcel(request, list, financeInnChannelSettlementList, status);
        }
        if (StringUtils.isNotBlank(status) && status.equals(FinanceInnSettlement.DELAY_STATUS)) {
            financeOutSpecialOrDelayExportService.createFinanceOutSpecialExcel(request, list, financeInnChannelSettlementList, status);
        }
        if (StringUtils.isNotBlank(status) && status.equals(FinanceInnSettlement.LEVEL_ARREARS_STATUS)) {
            financeOutArrearsExportService.createFinanceOutLevelArrearsExcel(request, list, financeInnChannelSettlementList, status);
        }
        if (StringUtils.isNotBlank(status) && status.equals(FinanceInnSettlement.PARTIAL_ARREARS_STATUS)) {
            financeOutArrearsExportService.createFinanceOutPartialArrearsExcel(request, list, financeInnChannelSettlementList, status);
        }
        if (StringUtils.isNotBlank(status) && status.equals(FinanceInnSettlement.ARREARS_STATUS)) {
            financeOutArrearsExportService.createFinanceOutArrearsExcel(request, list, financeInnChannelSettlementList, status);
        }

    }

    private void createFinanceExcel(HttpServletRequest request, List<FinanceInnSettlement> financeInnSettlementList, List<List<FinanceParentOrder>> financeParentOrderList) throws Exception {
        if (CollectionsUtil.isEmpty(financeInnSettlementList)) {
            throw new Exception("当前结算月份没有客栈结算数据");
        }
        if (CollectionsUtil.isEmpty(financeParentOrderList)) {
            throw new Exception("当月结算月份没有客栈结算的订单数据");
        }
//        OutputStream outputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            // 第一个sheet是总表，统计客栈全部数据
            HSSFSheet totalSheet = workbook.createSheet("总表");
            // 构建总表
            fillTotalSheetData(totalSheet, financeInnSettlementList, normalCellStyle, boldCellStyle);
            for (int i = 0; i < financeInnSettlementList.size(); i++) {
                FinanceInnSettlement financeInnSettlement = financeInnSettlementList.get(i);
                FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
                String innName = financeInnSettlementInfo.getInnName();
                String sheetName = getSheetNameByInnName(innName);
                HSSFSheet sheet = workbook.createSheet(sheetName + "(" + financeInnSettlementInfo.getId() + ")");
                sheet.autoSizeColumn(8, true);
                sheet.setDefaultColumnWidth(18);

                // 创建表头
                createExcelTitle(sheet, financeInnSettlement, boldCellStyle);
                List<FinanceParentOrder> parentOrderList = financeParentOrderList.get(i);
                fillExcelData(sheet, parentOrderList, normalCellStyle);
            }

//            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            // 拼接Excel文件名称
            String fileName = getFinanceExcelName(financeInnSettlementList) + "V" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls";
            fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
            // 设置文件名
//            response.addHeader("Content-Disposition", new String(("attachment; filename=" + fileName + ".xls").getBytes("GBK"), "ISO-8859-1"));
//            outputStream = response.getOutputStream();
            // 导出Excel
            String realPath = request.getSession().getServletContext().getRealPath("/") + "download";
            File file = new File(realPath + "/" + fileName);
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            throw new Exception("表格导出时出错!", e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                throw new Exception("表格导出时出错!", e);
            }
        }
    }

    /**
     * 根据客栈名称获取Excel的sheet名称
     * 1、长度超过31自动截取，后面的内容舍弃
     * 2、过滤特殊字符：\ / ? * [ ]
     *
     * @param innName
     * @return
     */
    private String getSheetNameByInnName(String innName) {
        if (StringUtils.isNotBlank(innName)) {
            String regEx = "[\\[\\]\\*\\?\\\\/]";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(innName);
            // 将特殊字符替换为空字符串
            innName = matcher.replaceAll("").trim();
            int length = innName.length();
            // sheet最大支持31位长度，因为要添加客栈ID，格式为（xxxxx），所以最大长度变为24
            int allowLength = 24;
            if (length >= allowLength) {
                innName = innName.substring(0, allowLength);
            }
            return innName;
        }
        return null;
    }


    /**
     * 导出客栈结算明细，兼容批量导出和单个客栈导出
     *
     * @param response                 HTTP响应对象
     * @param financeInnSettlementList 客栈结算详情集合
     * @param financeParentOrderList   客栈结算订单明细集合
     * @throws Exception
     */
    private void createFinanceExcel(HttpServletResponse response, List<FinanceInnSettlement> financeInnSettlementList, List<List<FinanceParentOrder>> financeParentOrderList) throws Exception {
        if (CollectionsUtil.isEmpty(financeInnSettlementList)) {
            throw new Exception("当前结算月份没有客栈结算数据");
        }
        if (CollectionsUtil.isEmpty(financeParentOrderList)) {
            throw new Exception("当月结算月份没有客栈结算的订单数据");
        }
        OutputStream outputStream = null;
        try {
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            // 第一个sheet是总表，统计客栈全部数据
            HSSFSheet totalSheet = workbook.createSheet("总表");
            // 构建总表
            fillTotalSheetData(totalSheet, financeInnSettlementList, normalCellStyle, boldCellStyle);
            for (int i = 0; i < financeInnSettlementList.size(); i++) {
                FinanceInnSettlement financeInnSettlement = financeInnSettlementList.get(i);
                FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
                String innName = financeInnSettlementInfo.getInnName();
                String sheetName = getSheetNameByInnName(innName);
                HSSFSheet sheet = workbook.createSheet(sheetName);
                sheet.autoSizeColumn(8, true);
                sheet.setDefaultColumnWidth(18);

                // 创建表头
                createExcelTitle(sheet, financeInnSettlement, boldCellStyle);
                List<FinanceParentOrder> parentOrderList = financeParentOrderList.get(i);
                fillExcelData(sheet, parentOrderList, normalCellStyle);
            }

            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            // 拼接Excel文件名称
            String fileName = getFinanceExcelName(financeInnSettlementList);
            // 设置文件名
            response.addHeader("Content-Disposition", new String(("attachment; filename=" + fileName + ".xls").getBytes("GBK"), "ISO-8859-1"));
            outputStream = response.getOutputStream();
            // 导出Excel
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new Exception("表格导出时出错!", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                throw new Exception("表格导出时出错!", e);
            }
        }
    }

    /**
     * 创建出账核对的总表
     *
     * @param totalSheet               总表的sheet
     * @param financeInnSettlementList 客栈结算对象集合
     * @param normalCellStyle          普通单元格样式
     * @param boldCellStyle            加粗单元格样式
     */
    private void fillTotalSheetData(HSSFSheet totalSheet, List<FinanceInnSettlement> financeInnSettlementList, HSSFCellStyle normalCellStyle, HSSFCellStyle boldCellStyle) {
        FinanceInnSettlement financeInnSettlement = financeInnSettlementList.get(0);
        totalSheet.autoSizeColumn(8, true);
        totalSheet.setDefaultColumnWidth(18);
        // 创建表头
        buildTotalSheetTitle(totalSheet, financeInnSettlement);
        renderedExcel(totalSheet, boldCellStyle, 0);
        buildTotalSheetData(totalSheet, financeInnSettlementList);
        renderedExcel(totalSheet, normalCellStyle, 2);
    }

    /**
     * 填充出账核算客栈结算总表数据
     *
     * @param totalSheet
     * @param financeInnSettlementList
     */
    private void buildTotalSheetData(HSSFSheet totalSheet, List<FinanceInnSettlement> financeInnSettlementList) {
        for (int i = 0; i < financeInnSettlementList.size(); i++) {
            FinanceInnSettlement financeInnSettlement = financeInnSettlementList.get(i);
            HSSFRow row = totalSheet.createRow(i + 2);
            HSSFCell cell = row.createCell(0);
            FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
            cell.setCellValue(financeInnSettlementInfo.getRegionName());
            cell = row.createCell(1);
            cell.setCellValue(financeInnSettlementInfo.getInnName());
            cell = row.createCell(2);
            cell.setCellValue(buildBankInfo(financeInnSettlement));
            cell = row.createCell(3);
            cell.setCellValue(financeInnSettlement.getTotalOrder());
            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(financeInnSettlement.getTotalAmount()));
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(financeInnSettlement.getChannelAmount()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(financeInnSettlement.getChannelSettlementAmount()));
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(financeInnSettlement.getFqSettlementAmount()));
            cell = row.createCell(8);
            String status = financeInnSettlement.getIsArrears();
            if (StringUtils.isNotBlank(status)) {
                if (status.equals("0")) {
                    cell.setCellValue(financeInnSettlement.getAfterPaymentAmount().toString());
                } else {
                    cell.setCellValue(financeInnSettlement.getAfterArrearsAmount().toString());
                }
            }
            cell = row.createCell(9);
            cell.setCellValue(financeInnSettlementInfo.getInnContact());
        }
    }

    /**
     * 填充出账核算总表表头
     *
     * @param totalSheet
     * @param financeInnSettlement
     */
    private void buildTotalSheetTitle(HSSFSheet totalSheet, FinanceInnSettlement financeInnSettlement) {
        List<Map<String, Object>> dataMapList = financeInnSettlementDao.selectFinanceInnSettlementCount(financeInnSettlement.getSettlementTime(), null, null);
        if (CollectionsUtil.isEmpty(dataMapList)) {
            throw new RuntimeException("本月没有客栈的结算记录");
        }
        Map<String, Object> dataMap = dataMapList.get(0);
        int innCount = Integer.parseInt(String.valueOf(dataMap.get("inncount")));
        int orders = Integer.parseInt(String.valueOf(dataMap.get("orders")));
        BigDecimal total = (BigDecimal) (dataMap.get("total"));
        BigDecimal channels = (BigDecimal) dataMap.get("channels");
        BigDecimal fqs = (BigDecimal) dataMap.get("fqs");
        BigDecimal inns = (BigDecimal) dataMap.get("inns");
        BigDecimal channelamount = (BigDecimal) dataMap.get("channelamount");
        // 第一行展示合计内容
        HSSFRow row = totalSheet.createRow(0);
        HSSFCell cell = row.createCell(1);
        cell.setCellValue("合计:      " + innCount);
        cell = row.createCell(3);
        cell.setCellValue(orders);
        cell = row.createCell(4);
        cell.setCellValue(String.valueOf(total));
        cell = row.createCell(5);
        cell.setCellValue(String.valueOf(channelamount));
        cell = row.createCell(6);
        cell.setCellValue(String.valueOf(channels));
        cell = row.createCell(7);
        cell.setCellValue(String.valueOf(fqs));
        cell = row.createCell(8);
        cell.setCellValue(String.valueOf(inns));
        // 第二行展示表头
        row = totalSheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("城市");
        cell = row.createCell(1);
        cell.setCellValue("客栈名称");
        cell = row.createCell(2);
        cell.setCellValue("收款信息(银行卡)");
        cell = row.createCell(3);
        cell.setCellValue("订单总数(个)");
        cell = row.createCell(4);
        cell.setCellValue("客栈订单总金额");
        cell = row.createCell(5);
        cell.setCellValue("分销商订单总金额");
        cell = row.createCell(6);
        cell.setCellValue("分销商结算金额");
        cell = row.createCell(7);
        cell.setCellValue("番茄收入金额");
        cell = row.createCell(8);
        cell.setCellValue("客栈结算金额");
        cell = row.createCell(9);
        cell.setCellValue("联系电话");
    }

    /**
     * 按照指定样式渲染Excel单元格
     *
     * @param sheet       表格对象
     * @param cellStyle   单元格样式对象
     * @param beginRowNum 开始渲染的行数
     */
    private void renderedExcel(HSSFSheet sheet, HSSFCellStyle cellStyle, int beginRowNum) {
        for (int i = beginRowNum; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow hr = sheet.getRow(i);
            for (int k = 0; k < hr.getLastCellNum(); k++) {
                HSSFCell hc = hr.getCell(k);
                if (hc != null) {
                    hc.setCellStyle(cellStyle);
                }
            }
        }
    }


    private void fillExcelData(HSSFSheet sheet, List<FinanceParentOrder> parentOrderList, HSSFCellStyle normalCellStyle) {
        for (int i = 0; i < parentOrderList.size(); i++) {
            FinanceParentOrder financeParentOrder = parentOrderList.get(i);
            HSSFRow row = sheet.createRow(i + 4);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(financeParentOrder.getChannelName());
            cell = row.createCell(1);
            cell.setCellValue(financeParentOrder.getOrderMode());
            cell = row.createCell(2);
            cell.setCellValue(financeParentOrder.getChannelOrderNo());
            cell = row.createCell(3);
            cell.setCellValue(financeParentOrder.getAuditStatusStr());
            cell = row.createCell(4);
            cell.setCellValue(financeParentOrder.getIsArrivalStr());
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(financeParentOrder.getInnSettlementAmount()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(financeParentOrder.getChannelSettlementAmount()));
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(financeParentOrder.getFqSettlementAmount()));
        }
        for (int i = 4; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow hr = sheet.getRow(i);
            for (int k = 0; k < hr.getLastCellNum(); k++) {
                HSSFCell hc = hr.getCell(k);
                if (hc != null) {
                    hc.setCellStyle(normalCellStyle);
                }
            }
        }
    }

    /**
     * 客栈结算详情集合生成Excel文件名称
     *
     * @param financeInnSettlementList 客栈结算详情集合
     * @return Excel文件名称
     */
    private String getFinanceExcelName(List<FinanceInnSettlement> financeInnSettlementList) {
        FinanceInnSettlement financeInnSettlement = financeInnSettlementList.get(0);
        String settlementTime = financeInnSettlement.getSettlementTime();
        if (financeInnSettlementList.size() == 1) {
            FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
            String innName = financeInnSettlementInfo.getInnName();
            return settlementTime + "_" + innName + "_结算汇总表";
        } else {
            return settlementTime + "客栈结算汇总表";
        }
    }

    private void createExcelTitle(HSSFSheet sheet, FinanceInnSettlement financeInnSettlement, HSSFCellStyle boldCellStyle) {
        // 第一行展示客栈名称+客栈联系电话+结算周期
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 0, 0, 7);
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(1, 1, 0, 7);

        sheet.addMergedRegion(cellRangeAddress1);
        sheet.addMergedRegion(cellRangeAddress2);
        // 第一行展示客栈信息
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue(buildInnInfo(financeInnSettlement));

        // 第二行展示客栈的银行卡信息
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(buildBankInfo(financeInnSettlement));

        // 第三行展示合计数据
        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("合计");
        cell = row.createCell(2);
        cell.setCellValue("总个数:" + financeInnSettlement.getTotalOrder());
        cell = row.createCell(5);
        cell.setCellValue("总金额:" + financeInnSettlement.getInnSettlementAmount());
        // 第四行展示标题
        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("渠道");
        cell = row.createCell(1);
        cell.setCellValue("订单模式");
        cell = row.createCell(2);
        cell.setCellValue("订单号");
        cell = row.createCell(3);
        cell.setCellValue("核单");
        cell = row.createCell(4);
        cell.setCellValue("渠道商款项");
        cell = row.createCell(5);
        cell.setCellValue("客栈结算金额");
        cell = row.createCell(6);
        cell.setCellValue("渠道商结算金额");
        cell = row.createCell(7);
        cell.setCellValue("番茄收入金额");
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow hr = sheet.getRow(i);
            for (int k = 0; k < hr.getLastCellNum(); k++) {
                HSSFCell hc = hr.getCell(k);
                if (hc != null) {
                    hc.setCellStyle(boldCellStyle);
                }
            }
        }
    }

    /**
     * 根据客栈结算对象，构建出账核算EXCEL中的客栈信息
     *
     * @param financeInnSettlement 客栈结算对象
     * @return 客栈信息
     */
    private String buildInnInfo(FinanceInnSettlement financeInnSettlement) {
        StringBuilder innInfo = new StringBuilder();
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
        innInfo.append(financeInnSettlementInfo.getInnName());
        innInfo.append("(");
        innInfo.append(financeInnSettlementInfo.getInnContact());
        innInfo.append(")  ");
        innInfo.append("结算周期:");
        innInfo.append(financeInnSettlement.getSettlementTime());
        return innInfo.toString();
    }

    /**
     * 根据客栈结算对象，构建出账核算EXCEL中的客栈银行卡支付信息
     *
     * @param financeInnSettlement 客栈结算对象
     * @return 银行卡支付信息
     */
    private String buildBankInfo(FinanceInnSettlement financeInnSettlement) {
        StringBuilder bankInfo = new StringBuilder();
        String defaultString = "暂无";
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankType(), defaultString));
        bankInfo.append(":");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankAccount(), defaultString));
        bankInfo.append("/");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankCode(), defaultString));
        bankInfo.append("\r\n");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankProvince(), defaultString));
        bankInfo.append("/");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankCity(), defaultString));
        bankInfo.append("\r\n");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankName(), defaultString));
        bankInfo.append("(");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankRegion(), defaultString));
        bankInfo.append(")");
        return bankInfo.toString();
    }

    @Override
    public List<ExcelSheetBean> buildChannelOrderData(Short channelId, String settlementTime) {
        List<ExcelSheetBean> excelSheetBeans = new ArrayList<>();
        if (channelId != null) {
            List<FinanceParentOrder> financeParentOrderList = financeOrderDao.findFinanceParentOrderByChannelId(channelId, settlementTime);
            ExcelSheetBean excelSheetBean = buildChannelExcelSheetBean(financeParentOrderList);
            excelSheetBeans.add(excelSheetBean);
        } else {
            List<Short> settlementChannelIdList = financeOrderDao.getSettlementChannelId(settlementTime);
            if (!CollectionsUtil.isEmpty(settlementChannelIdList)) {
                for (Short id : settlementChannelIdList) {
                    List<FinanceParentOrder> financeParentOrderList = financeOrderDao.findFinanceParentOrderByChannelId(id, settlementTime);
                    ExcelSheetBean excelSheetBean = buildChannelExcelSheetBean(financeParentOrderList);
                    excelSheetBeans.add(excelSheetBean);
                }
            }
        }
        return excelSheetBeans;
    }

    @Override
    public Page<FinanceInnSettlement> getFinanceInnSettlementList(Integer innId, Integer pageSize, Integer pageNo) {
        Page<FinanceInnSettlement> page = new Page<>();
        page.setOrder(Page.DESC);
        page.setOrderBy("settlement_time");
        page.setPageSize(pageSize);
        page.setPageNo(pageNo);
        return financeInnSettlementDao.selectFinanceInnSettlementListByInnId(page, innId);
    }

    @Override
    public Page<FinanceParentOrder> findInnOrderList(Integer innId, String settlementTime, Integer pageSize, Integer pageNo, Short priceStrategy, Boolean isPage) {
        Page<FinanceParentOrder> page = new Page<>();
        page.setOrder(Page.DESC);
        page.setOrderBy("order_time");
        if (isPage == null) {
            isPage = true;
        }
        if (isPage) {
            page.setPageSize(pageSize);
            page.setPageNo(pageNo);
        }
        return financeOrderDao.selectFinanceParentOrderPage(page, innId, null, settlementTime, null, null, null, priceStrategy, isPage);
    }

    @Override
    public AjaxChannelReconciliation channelReconciliation(MultipartFile file, Integer channelId, String settlementTime) {
        if (file == null) {
            return new AjaxChannelReconciliation(Constants.HTTP_400, "请选择上传文件");
        }
        // 获取文件名称
        String originalFilename = file.getOriginalFilename();
        if (!checkFileName(originalFilename)) {
            return new AjaxChannelReconciliation(Constants.HTTP_400, "文件名格式不合法");
        }
        if (channelId == null) {
            return new AjaxChannelReconciliation(Constants.HTTP_400, "渠道名称不存在");
        }
        if (StringUtils.isBlank(settlementTime)) {
            return new AjaxChannelReconciliation(Constants.HTTP_400, "结算月份不能为空");
        }
        // 获取渠道对账单集合
        List<ChannelReconciliation> channelReconciliationList;
        try {
            channelReconciliationList = getOrderFromExcel(file.getInputStream());
        } catch (Exception e) {
            return new AjaxChannelReconciliation(Constants.HTTP_400, e.getMessage());
        }
        if (CollectionsUtil.isEmpty(channelReconciliationList)) {
            return new AjaxChannelReconciliation(Constants.HTTP_400, "分销商");
        }
        // 获取渠道对账单Map
        Map<String, ChannelReconciliation> channelOrderMap = getChannelOrderMap(channelId, channelReconciliationList);
        // 获取番茄订单Map
        Map<String, ChannelReconciliation> fqReconciliationMap = getFqReconciliationMap(channelId, settlementTime);
        if (channelOrderMap != null && channelOrderMap.size() > 0) {
            try {
                if (!CollectionsUtil.isEmpty(channelReconciliationList)) {
                    // 以分销商对账单为蓝本，进行对账
                    AjaxChannelReconciliation reconciliation = reconciliation(fqReconciliationMap, channelReconciliationList, channelId);
                    // 以番茄对账单为蓝本，进行对账
                    reverseCheck(channelId, fqReconciliationMap, channelOrderMap, reconciliation);
                    FinanceOperationLog financeOperationLog = getFinanceOperationLogReconciliation(reconciliation, channelId, settlementTime);
                    financeChannelSettlementDao.updateFinanceChannelAuditStatus(channelId, settlementTime, reconciliation.getAuditStatus());
                    financeOperationLogDao.save(financeOperationLog);
                    return reconciliation;
                }
            } catch (Exception e) {
                return new AjaxChannelReconciliation(Constants.HTTP_400, e.getMessage());
            }
        }
        return null;
    }

    /**
     * 根据渠分销商ID和账期查询该分销商在本账期应该结算的账单列表，只查询状态为1的账单
     *
     * @param channelId      分销商ID
     * @param settlementTime 结算账期
     * @return
     */
    private Map<String, ChannelReconciliation> getFqReconciliationMap(Integer channelId, String settlementTime) {
        Map<String, ChannelReconciliation> orderMap = null;
        List<Map<String, Object>> dataMapList = financeOrderDao.selectReconciliationOrderMap(channelId, settlementTime);
        if (CollectionsUtil.isNotEmpty(dataMapList)) {
            orderMap = new HashMap<>();
            for (Map<String, Object> map : dataMapList) {
                ChannelReconciliation channelReconciliation = new ChannelReconciliation();
                String channelOrderNo = String.valueOf(map.get("channel_order_no"));
                channelReconciliation.setChannelOrderNo(channelOrderNo);
                String totalAmountStr = String.valueOf(map.get("total_amount"));
                String channelSettlementAmountStr = String.valueOf(map.get("channel_settlement_amount"));
                BigDecimal totalAmount = BigDecimal.ZERO;
                BigDecimal channelSettlementAmount = BigDecimal.ZERO;
                if (StringUtils.isNotBlank(totalAmountStr)) {
                    totalAmount = new BigDecimal(totalAmountStr);
                }
                channelReconciliation.setTotalAmount(totalAmount);
                if (StringUtils.isNotBlank(channelSettlementAmountStr)) {
                    channelSettlementAmount = new BigDecimal(channelSettlementAmountStr);
                }
                channelReconciliation.setChannelSettlementAmount(channelSettlementAmount);
                channelReconciliation.setChannelOrderNo(channelOrderNo);
                orderMap.put(channelId + channelOrderNo, channelReconciliation);
            }
        }
        return orderMap;
    }

    /**
     * 反向核对，以渠道订单为蓝本，比对番茄订单集合中出现的漏单现象
     *
     * @param channelId           渠道ID
     * @param fqReconciliationMap 番茄账单
     * @param channelOrderMap     渠道订单
     * @param reconciliation      对账结果
     */
    private void reverseCheck(Integer channelId, Map<String, ChannelReconciliation> fqReconciliationMap, Map<String, ChannelReconciliation> channelOrderMap, AjaxChannelReconciliation reconciliation) {
        // 以番茄订单为蓝本，渠道对账单中遗漏的订单数量
        int channelMissOrderAmount = 0;
        // 以番茄订单为蓝本，渠道对账单中遗漏的订单号码
        String channelMissOrderNo = "";
        if (fqReconciliationMap != null && !fqReconciliationMap.isEmpty()) {
            Set<String> keySet = fqReconciliationMap.keySet();
            for (String key : keySet) {
                ChannelReconciliation fqReconciliation = fqReconciliationMap.get(key);
                String channelOrderNo = fqReconciliation.getChannelOrderNo();
                ChannelReconciliation channelReconciliation = channelOrderMap.get(channelId + channelOrderNo);
                if (channelReconciliation == null) {
                    channelMissOrderAmount++;
                    channelMissOrderNo += channelOrderNo + ",";
                }
            }
        } else {
            if (channelOrderMap != null && !channelOrderMap.isEmpty()) {
                channelMissOrderAmount = channelOrderMap.size();
                Set<String> keySet = channelOrderMap.keySet();
                for (String key : keySet) {
                    ChannelReconciliation channelReconciliation = channelOrderMap.get(key);
                    channelMissOrderNo += channelReconciliation.getChannelOrderNo() + ",";
                }
            }
        }
        if (channelMissOrderAmount > 0) {
            reconciliation.setAuditStatus("2");
            LOGGER.info("以番茄账单为蓝本核单结果：" + 2);
        }
        LOGGER.info("channelMissOrderAmount：" + channelMissOrderAmount);
        LOGGER.info("channelMissOrderNo：" + channelMissOrderNo);
        reconciliation.setChannelMissOrderAmount(channelMissOrderAmount);
        reconciliation.setChannelMissOrderNo(channelMissOrderNo);
    }

    /**
     * 根据对账结果构造操作日志对象
     *
     * @param reconciliation 对账结果
     * @param channelId      渠道ID
     * @param settlementTime 对账月份
     * @return
     */

    private FinanceOperationLog getFinanceOperationLogReconciliation(AjaxChannelReconciliation reconciliation, Integer channelId, String settlementTime) {
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setChannelId(channelId);
        OtaInfo channel = otaInfoService.getByOtaId(channelId);
        StringBuilder stringBuilder = new StringBuilder("对账结果：");
        String auditStatus = reconciliation.getAuditStatus();
        if (StringUtils.isNotEmpty(auditStatus)) {
            String auditStatusStr = "对账失败";
            if ("1".equals(auditStatus)) {
                auditStatusStr = "对账成功";
            }
            stringBuilder.append(auditStatusStr + ",");
        }
        if (channel != null) {
            String channelName = channel.getName();
            financeOperationLog.setChannelName(channelName);
            stringBuilder.append(channelName + ",");
        }
        stringBuilder.append(settlementTime + ",");
        stringBuilder.append("番茄订单总数：" + reconciliation.getFqOrders());
        stringBuilder.append(",渠道订单总数：" + reconciliation.getChannelOrders());
        stringBuilder.append(",对账成功订单数：" + reconciliation.getSuccessOrders());
        stringBuilder.append(",对账失败订单数：" + reconciliation.getFailureOrders());
        stringBuilder.append(",对账单中遗漏订单数量：" + reconciliation.getChannelMissOrderAmount());
        stringBuilder.append(",对账单中遗漏订单号：" + reconciliation.getChannelMissOrderNo());
        stringBuilder.append(",番茄遗漏订单数量：" + reconciliation.getFqMissOrderAmount());
        stringBuilder.append(",番茄遗漏订单号：" + reconciliation.getFqMissOrderNo());
        financeOperationLog.setOperateContent(stringBuilder.toString());
        financeOperationLog.setOperateType("1");
        financeOperationLog.setOperateUser(getCurrentUser());
        financeOperationLog.setSettlementTime(settlementTime);
        return financeOperationLog;
    }

    /**
     * 以渠道对账单为蓝本进行对账
     *
     * @param fqOrderMap             番茄对账单结合
     * @param channelReconciliations 分销商对账单集合
     * @param channelId              分销商ID
     * @return
     */
    private AjaxChannelReconciliation reconciliation(Map<String, ChannelReconciliation> fqOrderMap, List<ChannelReconciliation> channelReconciliations, Integer channelId) {
        // 本月番茄订单数量
        int fqOrders = fqOrderMap.size();
        // 渠道上传对账单中的订单数量
        int channelOrders = channelReconciliations.size();
        // 对账成功订单数
        int successOrders = 0;
        // 对账失败订单数
        int failureOrders = 0;
        // 以渠道对账单为蓝本，番茄遗漏的订单数量
        int fqMissOrderAmount = 0;
        // 以渠道对账单为蓝本，番茄遗漏的订单号码
        String fqMissOrderNo = "";
        // 核单成功的订单号集合
        List<String> successOrderNo = new ArrayList<>();
        // 核单失败的订单号集合
        List<String> failureOrderNo = new ArrayList<>();
        for (int i = 0; i < channelReconciliations.size(); i++) {
            // 分销商对账对象
            ChannelReconciliation channelReconciliation = channelReconciliations.get(i);
            String channelOrderNo = channelReconciliation.getChannelOrderNo();
            // 番茄对账对象
            ChannelReconciliation fqReconciliation = fqOrderMap.get(channelId + channelOrderNo);
            boolean success;
            if (fqReconciliation == null) {
                fqMissOrderAmount++;
                fqMissOrderNo += channelOrderNo + ",";
            } else {
                try {
                    success = matchOrder(channelReconciliation, fqReconciliation);
                    if (success) {
                        successOrderNo.add(channelOrderNo);
                        successOrders++;
                    } else {
                        failureOrderNo.add(channelOrderNo);
                        failureOrders++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("第" + i + 1 + "行数据格式有误");
                }
            }
        }
        if (CollectionsUtil.isNotEmpty(successOrderNo)) {
            // 500个账单执行一次update
            List<List<String>> lists = CollectionsUtil.splitList(successOrderNo, 500);
            for (List<String> orders : lists) {
                // 更新订单的核单状态
                financeOrderDao.updateFinanceParentOrderAuditStatus(channelId, CollectionsUtil.convertToDBString(orders), "1");
            }
        }
        if (CollectionsUtil.isNotEmpty(failureOrderNo)) {
            List<List<String>> lists = CollectionsUtil.splitList(failureOrderNo, 500);
            for (List<String> orders : lists) {
                // 更新订单的核单状态
                financeOrderDao.updateFinanceParentOrderAuditStatus(channelId, CollectionsUtil.convertToDBString(orders), "2");
            }
        }
        LOGGER.info("fqMissOrderAmount：" + fqMissOrderAmount);
        LOGGER.info("failureOrders：" + failureOrders);
        // 本次对账的结果，1：已核成功，2：已核失败
        String auditStatus = "2";
        if (failureOrders == 0 && successOrders == fqOrders && fqMissOrderAmount < 1) {
            auditStatus = "1";
        }
        LOGGER.info("successOrders：" + successOrders);
        LOGGER.info("fqOrders：" + fqOrders);
        LOGGER.info("分销商订单为蓝本核单结果：" + auditStatus);
        return new AjaxChannelReconciliation(Constants.HTTP_OK, "对账完成", fqOrders, channelOrders, successOrders, fqOrders - successOrders, auditStatus, fqMissOrderAmount, fqMissOrderNo);
    }

    /**
     * 匹配渠道的订单信息和番茄的订单信息
     *
     * @param channelReconciliation 渠道的订单信息，数据来源于上传的Excel
     * @param fqReconciliation      番茄订单
     * @return
     */
    private boolean matchOrder(ChannelReconciliation channelReconciliation, ChannelReconciliation fqReconciliation) {
        // 核对客栈名称
//        if (!financeParentOrder.getInnName().trim().equals(channelReconciliation.getInnName().trim())) {
//            return false;
//        }
        // 核对房型
//        if (!financeParentOrder.getChannelRoomTypeName().equals(channelReconciliation.getChannelRoomTypeName())) {
//            return false;
//        }
        // 核对下单联系人电话
//        if (!financeParentOrder.getContact().trim().equals(channelReconciliation.getContact().trim())) {
//            return false;
//        }
        // 核对入住日期
//        if (!DateUtils.isSameDay(financeParentOrder.getCheckInAt(), channelReconciliation.getCheckInAt())) {
//            return false;
//        }
//        // 核对退房日期
//        if (!DateUtils.isSameDay(financeParentOrder.getCheckOutAt(), channelReconciliation.getCheckOutAt())) {
//            return false;
//        }
        // 核对房间数
//        if (!financeParentOrder.getRoomTypeNums().equals(channelReconciliation.getRoomTypeNums())) {
//            return false;
//        }
        // 核对间夜数
//        if (!financeParentOrder.getRoomNights().equals(channelReconciliation.getNights())) {
//            return false;
//        }
        // 核对价格模式
//        if (!financeParentOrder.getPriceStrategy().equals(channelReconciliation.getPriceStrategy())) {
//            return false;
//        }
        // 核对订单价格，20160125迭代将对比分销商订单价格
        if (fqReconciliation.getTotalAmount().compareTo(channelReconciliation.getTotalAmount().setScale(2, RoundingMode.HALF_UP)) != 0) {
            return false;
        }
        // 核对渠道结算金额
        if (fqReconciliation.getChannelSettlementAmount().compareTo(channelReconciliation.getChannelSettlementAmount().setScale(2, RoundingMode.HALF_UP)) != 0) {
            return false;
        }
        // 核对客栈结算金额
//        if(financeParentOrder.getInnSettlementAmount().compareTo(channelReconciliation.getInnSettlementAmount().setScale(2, RoundingMode.HALF_UP)) != 0) {
//            return false;
//        }
        return true;
    }

    @Override
    public boolean checkInnId(Integer innId, String appKey) {
        List<Map<String, Object>> innAppKey = financeOrderDao.getInnAppKey(innId, appKey);
        if (CollectionsUtil.isNotEmpty(innAppKey)) {
            Map<String, Object> stringObjectMap = innAppKey.get(0);
            if (stringObjectMap != null) {
                Object id = stringObjectMap.get("id");
                if (id != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 从消息中心获取WxOpenid
     */
    @Override
    public void synchronizationInnInfo(FinanceInnSettlementInfo financeInnSettlementInfo) {
        try {
            FinanceInnSettlementInfo innInfo = financeHelper.getInnInfo(financeInnSettlementInfo.getId());
            if (innInfo != null) {
                BeanUtil.copy(innInfo, financeInnSettlementInfo);
                financeInnSettlementInfo.setDateUpdated(new Date());
                financeInnSettlementInfoDao.save(financeInnSettlementInfo);
            }
        } catch (Exception e) {
            LOGGER.error("同步客栈[" + financeInnSettlementInfo.getId() + "]结算信息失败，原因：" + e);
        }
    }

    @Override
    public List<FinanceInnSettlementInfo> getAllInnSettlementInfo() {
        return financeInnSettlementInfoDao.getAll();
    }

    @Override
    public void sendInnBill(String settlementTime) {
        //查询数据库,该账期账单是否已经发送
        FinanceAccountPeriod financeAccountPeriod = financeAccountPeriodDao.findFinanceAccountPeriodWithSettlementTime(settlementTime);
        if (financeAccountPeriod == null) {
            throw new RuntimeException("【" + settlementTime + "】的账期不存在");
        }
        if (financeAccountPeriod.getSendBillStatus()) {
            throw new RuntimeException("【" + settlementTime + "】账期的账单已被发送，请勿重复发送操作");
        }
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        StringBuilder stringBuilder = new StringBuilder("发送：" + settlementTime + "客栈结算账单");
        financeOperationLog.setOperateContent(stringBuilder.toString());
        financeOperationLog.setOperateType("3");
        financeOperationLog.setOperateUser(getCurrentUser());
        financeOperationLog.setSettlementTime(settlementTime);
        financeOperationLogDao.save(financeOperationLog);
        //发送账单
        financeInnSettlementDao.updateFinanceInnSettlementBillStatus(settlementTime);
        LOGGER.info("更改【" + settlementTime + "】客栈结算对象的结算状态为true");
        // 查询本月需要发送账单的客栈结算对象集合
        List<FinanceInnSettlement> financeInnSettlements = financeInnSettlementDao.selectFinanceInnSettlementBySettlementTime(settlementTime, false);
        if (CollectionsUtil.isEmpty(financeInnSettlements)) {
            LOGGER.error("账期【" + settlementTime + "】,目标客栈集合为空，账单发送失败");
            return;
        }
        for (FinanceInnSettlement financeInnSettlement : financeInnSettlements) {
            // 发送微信账单推送消息
            sendBillMessage(financeInnSettlement);
            //发送短信账单推送信息
            try {
                sendBillSms(financeInnSettlement);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            //更新账期表发送账单状态
            financeAccountPeriodDao.updateSendBillStatus(settlementTime);
        } catch (Exception e) {
            throw new RuntimeException("按账期发送账单-更新账期表出现异常");
        }
    }

    /**
     * 封装相关微信账单推送信息并发送
     *
     * @param financeInnSettlement
     */
    public void sendBillMessage(FinanceInnSettlement financeInnSettlement) {
        String settlementTime = financeInnSettlement.getSettlementTime();
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
        if (StringUtils.isBlank(settlementTime)) {
            LOGGER.error(financeInnSettlementInfo.getInnName() + "结算月份为空");
            return;
        }
        try {
            MessageManageServiceImpl messageManageService = new MessageManageServiceImpl();
            PendingNotify notify = new PendingNotify();
            String id = financeInnSettlementInfo.getWxOpenId();
            if (StringUtils.isNotBlank(id) && !id.equals("null")) {
                List<String> openid = new ArrayList<>();
                openid.add(id);
                notify.setReceivers(openid);
                notify.setTip(financeInnSettlementInfo.getInnName() + "客栈您好，您有一笔待确认账单，为不影响正常结款，请将您的银行卡账号在系统中填写完整（已填写完整，请忽略）。");
                notify.setPendingTask("代销账单确认");
                notify.setNotifyType("紧急重要");
                notify.setNotifyTime(settlementTime + "离店订单");
                notify.setDescription("订单量：" + financeInnSettlement.getTotalOrder() + "\n" + "间夜量：" + financeInnSettlement.getRoomNights() + "\n" + "结算金额：" + financeInnSettlement.getAfterPaymentAmount() + "\n" +
                        "请在24小时内确认账单，超过确认时间系统默认您已接受，账单确认后将为您安排结算。财务咨询电话：028-85961162；QQ：4000230190；咨询时间（工作日）：9:00-18:00。");

                LOGGER.info(financeInnSettlementInfo.getInnName() + "准备向" + id + "发送微信推送");
                try {
                    messageManageService.sendMessage(MessageBuilder.buildPendingNotifyWechatMessage(notify));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            LOGGER.error(financeInnSettlementInfo.getInnName() + settlementTime + "微信推送失败");
        }
    }

    //推送错误消息
    @Override
    public void sendErrorMessage(Integer innId, String settlementTime) {
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlementInfoDao.financeInnSettlementInfoWithId(innId);
        sendErrorSMSMessage(financeInnSettlementInfo, settlementTime);
        sendErrorWxMessage(financeInnSettlementInfo, settlementTime);
    }


    //结算错误微信推送
    public void sendErrorWxMessage(FinanceInnSettlementInfo financeInnSettlementInfo, String settlementTime) {
        try {
            String wxOpenId = financeInnSettlementInfo.getWxOpenId();
            List<String> receivers = new ArrayList<>();
            if (!StringUtil.isNotNull(wxOpenId)) {
                LOGGER.info(financeInnSettlementInfo.getInnName() + "微信账号为空");
                return;
            }
            receivers.add(wxOpenId);
            LOGGER.info("准备向" + financeInnSettlementInfo.getInnName() + "发送结算错误微信消息");
            new MessageManageServiceImpl().sendMessage(MessageBuilder.buildErrorNotifyWechatMessage(financeInnSettlementInfo.getBankName() + "您好，您的银行卡存在错误", "由于银行卡信息存在错误，账期" + settlementTime + "款项被银行退回", "请仔细核对银行卡户名，账号，支行等信息，并联系签约经理修改，修改正确后为您重新安排打款", "番茄代销", receivers));

        } catch (Exception e) {
            LOGGER.error(financeInnSettlementInfo.getInnName() + settlementTime + "错误结算微信推送失败");
            e.printStackTrace();
        }
    }

    //结算错误短信推送
    public void sendErrorSMSMessage(FinanceInnSettlementInfo financeInnSettlementInfo, String settlementTime) {
        try {
            String contact1 = financeInnSettlementInfo.getContact1();
            String contact2 = financeInnSettlementInfo.getContact2();
            List<String> receivers = new ArrayList<>();
            if (StringUtils.isNotBlank(contact1)) {
                receivers.add(contact1);
            }
            if (StringUtils.isNotBlank(contact2)) {
                receivers.add(contact2);
            }

            if (CollectionsUtil.isEmpty(receivers)) {
                LOGGER.info(financeInnSettlementInfo.getInnName() + "电话联系方式为空");
                return;
            }
            StringBuffer msm = new StringBuffer();
            msm.append(financeInnSettlementInfo.getInnName() + "您好，账期");
            msm.append(settlementTime + "款项被银行退回，请仔细核对银行卡户名，账号，支行等信息，并联系签约经理修改，修改正确后为您重新安排打款");
            LOGGER.info("准备向" + financeInnSettlementInfo.getInnName() + "发送错误结算短信通知推送");
            new MessageManageServiceImpl().sendMessage(MessageBuilder.buildSmsMessage(receivers, SmsChannel.SEND_TYPE_VIP, msm.toString()));
        } catch (Exception e) {
            LOGGER.error(financeInnSettlementInfo.getInnName() + settlementTime + "错误结算短信推送失败");
            e.printStackTrace();
        }
    }


    /**
     * 发送通知短信
     *
     * @param financeInnSettlement
     */
    public void sendBillSms(FinanceInnSettlement financeInnSettlement) {
        String settlementTime = financeInnSettlement.getSettlementTime();
        LOGGER.info(settlementTime + "进入短信推送");
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
        if (StringUtils.isBlank(settlementTime)) {
            LOGGER.error(financeInnSettlementInfo.getInnName() + "结算月份为空");
            return;
        }
        try {
            MessageManageServiceImpl messageManageService = new MessageManageServiceImpl();
            String contact1 = financeInnSettlementInfo.getContact1();
            String contact2 = financeInnSettlementInfo.getContact2();
            List<String> contacts = new ArrayList<>();
            if (StringUtils.isNotBlank(contact1) && !contact1.equals("null")) {
                contacts.add(contact1);
            }
            if (StringUtils.isNotBlank(contact2) && !contact2.equals("null")) {
                contacts.add(contact2);
            }
            LOGGER.info("准备拼接短信内容共有" + contacts.size() + "个号码,号码1为" + contact1);
            if (CollectionsUtil.isNotEmpty(contacts)) {
                StringBuffer msm = new StringBuffer();
                msm.append(financeInnSettlementInfo.getInnName() + "您好，");
                msm.append("您的账单已出，");
                msm.append("账期：" + settlementTime);
                msm.append("收入金额：" + financeInnSettlement.getAfterPaymentAmount() + "元。");
                msm.append("请于24小时内在番茄系统中的财务——代销对账内确认账单。财务咨询电话：028-85961162，咨询时间（工作日）：9:00-18:00。");
                //发送短信
                LOGGER.info("准备向" + financeInnSettlementInfo.getInnName() + financeInnSettlementInfo.getContact1() + "发送短信通知推送");
                messageManageService.sendMessage(MessageBuilder.buildSmsMessage(contacts, SmsChannel.SEND_TYPE_VIP, msm.toString()));
                LOGGER.info("消息中心方法调用完成");
            }

        } catch (Exception e) {
            LOGGER.error(financeInnSettlementInfo.getInnName() + settlementTime + "短信推送失败");
        }
    }

    @Override
    public void batchSettlement(String settlementTime) {
        //查询数据库,该账期账单是否已经结算
        FinanceAccountPeriod financeAccountPeriod = financeAccountPeriodDao.findFinanceAccountPeriodWithSettlementTime(settlementTime);
        if (financeAccountPeriod == null) {
            throw new RuntimeException("【" + settlementTime + "】的账期不存在");
        }

        // 查询该结算月份,是否全部客栈（除已标注）已经结算
        List<FinanceInnSettlement> financeInnSettlementsList = financeInnSettlementDao.selectUnSettlementInn(settlementTime, "0", false);
        if (CollectionsUtil.isEmpty(financeInnSettlementsList)) {
            throw new RuntimeException("【" + settlementTime + "】的客栈已经全部结算");
        }
        // 一键结算该月份的的全部客栈
        financeInnSettlementDao.batchUpdateInnSettlement(settlementTime);
        financeOrderDao.updateOrderWithBalance(settlementTime);
        // 记录操作日志
        FinanceOperationLog financeOperationLog = getBatchSettlementLog(settlementTime);
        financeOperationLogDao.save(financeOperationLog);

        // 查询该结算月份,是否全部客栈（除已标注）已经结算
        if (CollectionsUtil.isEmpty(financeInnSettlementsList)) {
            throw new RuntimeException("【" + settlementTime + "】的客栈已经全部结算");
        }
        for (FinanceInnSettlement financeInnSettlement : financeInnSettlementsList) {
            //发送微信结算通知
            sendSettlementMessage(financeInnSettlement);
            //发送短信结算通知
            sendSettlementSms(financeInnSettlement);
        }
        try {
            //更新账期表结算状态字段
            financeAccountPeriodDao.updateSettlementStatus(settlementTime);
        } catch (Exception e) {
            throw new RuntimeException("按账期一键结算-更新账期表出现异常");
        }
    }

    /**
     * 发送结算短信
     *
     * @param financeInnSettlement
     */
    public void sendSettlementSms(FinanceInnSettlement financeInnSettlement) {
        String settlementTime = financeInnSettlement.getSettlementTime();
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
        if (StringUtils.isBlank(settlementTime)) {
            LOGGER.error(financeInnSettlementInfo.getInnName() + "结算月份为空");
            return;
        }
        try {
            MessageManageServiceImpl messageManageService = new MessageManageServiceImpl();
            String contact1 = financeInnSettlementInfo.getContact1();
            String contact2 = financeInnSettlementInfo.getContact2();
            List<String> contacts = new ArrayList<>();

            if (StringUtils.isNotBlank(contact1) && !contact1.equals("null")) {
                contacts.add(contact1);
            }
            if (StringUtils.isNotBlank(contact2) && !contact2.equals("null")) {
                contacts.add(contact2);
            }
            if (CollectionsUtil.isNotEmpty(contacts)) {
                String month = financeInnSettlement.getSettlementTime();
                StringBuffer msm = new StringBuffer();
                msm.append(financeInnSettlementInfo.getInnName() + "您好，");
                msm.append("番茄已将代销账款由财付通转至您的银行账户中。");
                msm.append("账期:" + settlementTime);
                msm.append("总额：" + financeInnSettlement.getAfterPaymentAmount() + "元");
                msm.append("具体到账时间，以银行为准，约有2~3天银行处理期!");
                //发送短信
                LOGGER.error("准备向" + financeInnSettlementInfo.getInnName() + "发送短信结算通知");
                messageManageService.sendMessage(MessageBuilder.buildSmsMessage(contacts, SmsChannel.SEND_TYPE_VIP, msm.toString()));
            }
        } catch (Exception e) {
            LOGGER.error(financeInnSettlementInfo.getInnName() + settlementTime + "短信结算通知发送失败");
        }
    }

    /**
     * 封装相关微信结算通知信息并发送
     *
     * @param financeInnSettlement
     */
    private void sendSettlementMessage(FinanceInnSettlement financeInnSettlement) {
        String settlementTime = financeInnSettlement.getSettlementTime();
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
        try {
            MessageManageServiceImpl messageManageService = new MessageManageServiceImpl();
            BalanceModel model = new BalanceModel();
            if (StringUtils.isBlank(settlementTime)) {
                LOGGER.error(financeInnSettlementInfo.getInnName() + "结算月份为空");
                return;
            }
            String id = financeInnSettlementInfo.getWxOpenId();
            if (StringUtils.isNotBlank(id) && !id.equals("null")) {
                List<String> openid = new ArrayList<>();
                openid.add(id);
                model.setTip(financeInnSettlementInfo.getInnName() + "您好，番茄已将代销账款由财付通转入至您的银行账户中。具体到账时间，以银行为准，约有2~3天银行处理期。");
                model.setTimeSlot(settlementTime + "离店,");
                model.setTotalMoney(financeInnSettlement.getAfterPaymentAmount() + "元,");
                model.setDescription("请注意查收!");
                model.setReceivers(openid);
                WeChatMessage message = MessageBuilder.buildBalanceWechatMessage(model);
                LOGGER.info(financeInnSettlementInfo.getInnName() + "准备向" + id + "发送微信推送");
                messageManageService.sendMessage(message);
            }
        } catch (Exception e) {
            LOGGER.error(financeInnSettlementInfo.getInnName() + settlementTime + "结算推送发送失败");
        }
    }

    /**
     * 构建一键结算的操作日志
     *
     * @param settlementTime 结算月份
     * @return
     */
    private FinanceOperationLog getBatchSettlementLog(String settlementTime) {
        String operateContent = "一键结算:";
        // 查询已标记、未结算的客栈
        List<FinanceInnSettlement> financeInnSettlementsList = financeInnSettlementDao.selectUnSettlementInn(settlementTime, "0", true);
        if (CollectionsUtil.isNotEmpty(financeInnSettlementsList)) {
            operateContent += "【" + settlementTime + "】除了：";
            for (FinanceInnSettlement financeInnSettlement : financeInnSettlementsList) {
                FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
                operateContent += financeInnSettlementInfo.getInnName();
                operateContent += "，共计:" + financeInnSettlement.getTotalOrder() + "个订单,";
                operateContent += financeInnSettlement.getInnSettlementAmount() + "元;";
            }
            operateContent += "以外，其他客栈全部结算完成!";
        } else {
            operateContent += "【" + settlementTime + "】全部客栈结算完成!";
        }
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setOperateContent(operateContent);
        financeOperationLog.setOperateType("4");
        financeOperationLog.setOperateUser(getCurrentUser());
        financeOperationLog.setSettlementTime(settlementTime);
        return financeOperationLog;
    }

    @Override
    public Page<FinanceOperationLog> findFinanceOperationLogList(Page<FinanceOperationLog> page, String keyWord, String settlementTime, String startDate, String endDate, String operateType) {
        return financeOperationLogDao.findFinanceOperationLogList(page, keyWord, settlementTime, startDate, endDate, operateType);
    }

    /**
     * 根据上传的Excel输入流读取渠道对账的订单信息
     *
     * @param inputStream
     * @return
     */
    private List<ChannelReconciliation> getOrderFromExcel(InputStream inputStream) throws IOException, InvalidFormatException, ParseException {
        List<ChannelReconciliation> list = null;
        // 根据文件流构建工作表对象
        Workbook workbook = WorkbookFactory.create(inputStream);
        // 获取第一个sheet
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet != null && sheet.getLastRowNum() > 0) {
            list = new ArrayList<>();
            // 遍历行，第一行是标题，从第二行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                try {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        // 获得订单号
                        String channelOrderNo = ExcelReaderUtils.getCellFormatValue(row.getCell(0));
                        if (StringUtils.isNotBlank(channelOrderNo)) {
                            ChannelReconciliation channelReconciliation = new ChannelReconciliation();
                            channelReconciliation.setChannelOrderNo(channelOrderNo);
                            channelReconciliation.setInnName(ExcelReaderUtils.getCellFormatValue(row.getCell(1)));
//                        channelReconciliation.setChannelRoomTypeName(ExcelReaderUtils.getCellFormatValue(row.getCell(2)));
//                        channelReconciliation.setContact(ExcelReaderUtils.getCellFormatValue(row.getCell(3)));
                            channelReconciliation.setUserName(ExcelReaderUtils.getCellFormatValue(row.getCell(2)));
//                        channelReconciliation.setCheckInAt(DateUtils.parseDate(ExcelReaderUtils.getCellFormatValue(row.getCell(5)), "yyyy-MM-dd"));
//                        channelReconciliation.setCheckOutAt(DateUtils.parseDate(ExcelReaderUtils.getCellFormatValue(row.getCell(6)), "yyyy-MM-dd"));
//                        channelReconciliation.setRoomTypeNums(Integer.parseInt(ExcelReaderUtils.getCellFormatValue(row.getCell(7))));
//                        channelReconciliation.setNights(Integer.parseInt(ExcelReaderUtils.getCellFormatValue(row.getCell(8))));
                            String priceStrategyStr = ExcelReaderUtils.getCellFormatValue(row.getCell(3));
                            Short priceStrategy = 1;
                            if ("卖价".equals(priceStrategyStr)) {
                                priceStrategy = 2;
                            }
                            channelReconciliation.setPriceStrategy(priceStrategy);
                            channelReconciliation.setTotalAmount(new BigDecimal(ExcelReaderUtils.getCellFormatValue(row.getCell(4))));
                            channelReconciliation.setChannelSettlementAmount(new BigDecimal(ExcelReaderUtils.getCellFormatValue(row.getCell(5))));
//                        channelReconciliation.setInnSettlementAmount(new BigDecimal(ExcelReaderUtils.getCellFormatValue(row.getCell(6))));
//                        Short costType = 1;
//                        String costTypeStr = ExcelReaderUtils.getCellFormatValue(row.getCell(7));
//                        if ("违约金".equals(costTypeStr)) {
//                            costType = 2;
//                        }
//                        channelReconciliation.setCostType(costType);
                            list.add(channelReconciliation);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("对账表格的第" + (i + 1) + "行，数据解析出错，请仔细检查数据格式");
                }
            }
        }
        return list;
    }

    /**
     * 将从Excel中读取的渠道对账单，转换成Map
     *
     * @param channelId          渠道ID
     * @param reconciliationList 渠道对账单集合
     * @return
     */
    private Map<String, ChannelReconciliation> getChannelOrderMap(Integer channelId, List<ChannelReconciliation> reconciliationList) {
        Map<String, ChannelReconciliation> orderMap = null;
        if (CollectionsUtil.isNotEmpty(reconciliationList)) {
            orderMap = new HashMap<>();
            for (ChannelReconciliation channelReconciliation : reconciliationList) {
                String channelOrderNo = channelReconciliation.getChannelOrderNo();
                orderMap.put(channelId + channelOrderNo, channelReconciliation);
            }
        }
        return orderMap;
    }

    /**
     * 获取指定渠道，指定账期的番茄账单集合
     *
     * @param channelId      渠道ID
     * @param settlementTime 账期
     * @return
     */
    private List<FinanceParentOrder> getFqOrderList(Integer channelId, String settlementTime) {
        return financeOrderDao.findFinanceParentOrderByChannelId(channelId.shortValue(), settlementTime);
    }

    /**
     * 根据渠道ID和结算月份查询订单集合
     * 使用“渠道ID+订单号”作为key，订单对象最为value
     *
     * @param channelId 渠道ID
     * @param orderList 番茄账单集合
     * @return
     */
    private Map<String, FinanceParentOrder> getFqOrderMap(Integer channelId, List<FinanceParentOrder> orderList) {
        Map<String, FinanceParentOrder> orderMap = null;
        if (CollectionsUtil.isNotEmpty(orderList)) {
            orderMap = new HashMap<>();
            for (FinanceParentOrder financeParentOrder : orderList) {
                String channelOrderNo = financeParentOrder.getChannelOrderNo();
                orderMap.put(channelId + channelOrderNo, financeParentOrder);
            }
        }
        return orderMap;
    }

    /**
     * 检测渠道对账单的Excel名称是否合法
     *
     * @param fileName
     * @return
     */
    private boolean checkFileName(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            // 校验Excel文件名
            Pattern pattern = Pattern.compile(".+\\.(xls|xlsx)");
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根绝订单集合构造渠道订单详情的Excel Sheet对象
     *
     * @param financeParentOrderList
     * @return
     */
    private ExcelSheetBean buildChannelExcelSheetBean(List<FinanceParentOrder> financeParentOrderList) {
        ExcelSheetBean excelSheetBean = new ExcelSheetBean();
        if (!CollectionsUtil.isEmpty(financeParentOrderList)) {
            excelSheetBean.setSheetName(financeParentOrderList.get(0).getChannelName());
        } else {
            excelSheetBean.setSheetName(System.currentTimeMillis() + "");
        }
        excelSheetBean.setClassName("com.project.entity.finance.FinanceParentOrder");
        excelSheetBean.setList(financeParentOrderList);
        excelSheetBean.setPropertySequence(new String[]{"innerOrderMode", "channelOrderNo", "innName", "userName", "contact", "channelRoomTypeName", "checkDate", "auditStatusStr", "channelSettlementAmount"});
        excelSheetBean.setTableHeader(new String[]{"订单模式", "订单号", "客栈名称", "预定人", "手机号码", "房型", "入住—退房日期", "核单", "渠道商结算金额"});
        return excelSheetBean;
    }

    /**
     * 获取当前登录用户名
     *
     * @return
     */
    private String getCurrentUser() {
        return SpringSecurityUtil.getCurrentUser().getUsername();
    }

    @Override
    public void updateChannelSettlementIncome(Integer id, BigDecimal incomeAmount, String remarks) {
        String remarksStr = "";
        if (remarks != null) {
            Pattern p = Pattern.compile("\n|\r");
            Matcher m = p.matcher(remarks);
            remarksStr = m.replaceAll("");
        }
        financeChannelSettlementDao.updateIncomeAmount(id, incomeAmount, remarksStr);
    }

    @Override
    public Map<String, Object> findChannelSettlementIncomeAmount(String settlementTime) {
        if (StringUtils.isNotBlank(settlementTime)) {
            List<Map<String, Object>> financeChannelSettlementCount = financeChannelSettlementDao.selectChannelSettlementIncomeAmount(settlementTime);
            if (!CollectionsUtil.isEmpty(financeChannelSettlementCount)) {
                return financeChannelSettlementCount.get(0);
            }
        }
        return null;
    }

    @Override
    public File[] getFileList(HttpServletRequest request) {
        String realPath = request.getSession().getServletContext().getRealPath("/") + "download";
        File file = new File(realPath);
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return new Date(file2.lastModified()).compareTo(new Date(file1.lastModified()));
            }
        });
        return files;
    }

    @Override
    public void removeFile(HttpServletRequest request, String fileName) {
        String filePath = request.getSession().getServletContext().getRealPath("/") + "download" + "/" + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("您慢了一步，文件已经被其他同学删掉了");
        }
        if (!file.isFile()) {
            throw new RuntimeException("您要删除的不是一个文件哦");
        }
        file.delete();
    }

    @Override
    public List<FinanceChannelSettlement> getFinanceChannelSettlementsWithSettlement(String settlementTime) {
        return financeChannelSettlementDao.selectFinanceChannelSettlement(settlementTime);
    }

    @Override
    public Page<FinanceParentOrder> findSpecialInnNormalOrder(Page<FinanceParentOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo) {
        return financeOrderDao.findSpecialInnNormalOrder(page, innId, channelId, settlementTime, channelOrderNo);
    }

    @Override
    public List<Map<String, Object>> selectSpecialInnNormalOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo) {
        return financeOrderDao.selectSpecialInnNormalOrderCount(innId, channelId, settlementTime, channelOrderNo);
    }


    @Override
    public Page<FinanceSpecialOrder> findFinanceSpecialOrder(Page<FinanceSpecialOrder> page, Integer innId, Integer channelId, String settlementTime, String orderNo, String status, String contactsStatus) {
        return financeSpecialOrderDao.findFinanceSpecialOrder(page, innId, channelId, settlementTime, orderNo, status, contactsStatus);
    }

    @Override
    public List<Map<String, Object>> selectSpecialInnRecoveryOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo) {
        return financeOrderDao.selectSpecialInnRecoveryOrderCount(innId, channelId, settlementTime, channelOrderNo);
    }

    @Override
    public List<Map<String, Object>> selectSpecialInnRefundOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String contactsStatus) {
        return financeOrderDao.selectSpecialInnRefundOrderCount(innId, channelId, settlementTime, channelOrderNo, contactsStatus);
    }

    @Override
    public List<Map<String, Object>> selectSpecialInnReplenishmentOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo) {
        return financeOrderDao.selectSpecialInnReplenishmentOrderCount(innId, channelId, settlementTime, channelOrderNo);
    }

    @Override
    public List<Map<String, Object>> selectUnbalanceFinanceInnSettlementCount(String settlementTime, String status) {
        return financeInnSettlementDao.selectUnbalanceFinanceInnSettlementCount(settlementTime, status);
    }

    @Override
    public FinanceInnSettlement findFinanceInnSettlement(Integer id, String settlementTime) {
        return financeInnSettlementDao.findFinanceInnSettlement(id, settlementTime);
    }

    @Override
    public Page<FinanceInnSettlement> findArrearFinanceInnSettlement(String settlementTime, Page<FinanceInnSettlement> page, String arrearsStatus, String innName) {
        return financeInnSettlementDao.findArrearFinanceInnSettlement(settlementTime, page, arrearsStatus, innName);
    }

    @Override
    public Page<FinanceInnSettlement> findTotalArrearFinanceInnSettlement(String settlementTime, Page<FinanceInnSettlement> page, String arrearsStatus, String innName) {
        return financeInnSettlementDao.findTotalArrearFinanceInnSettlement(settlementTime, page, arrearsStatus, innName);
    }

    @Override
    public Map<String, Object> selectArrearFinanceInnSettlement(String settlementTime, String arrearsStatus) {
        return financeInnSettlementDao.selectArrearFinanceInnSettlement(settlementTime, arrearsStatus);
    }

    @Override
    public Map<String, Object> selectTotalArrearFinanceInnSettlement(String settlementTime, String arrearsStatus) {
        return financeInnSettlementDao.selectTotalArrearFinanceInnSettlement(settlementTime, arrearsStatus);
    }

    @Override
    public void cleanSpecialOrder(String settlementTime) {
        List<Map<String, Object>> mapList = financeOrderDao.getNormalOrderId(settlementTime);
        String id;
        List<String> list = new ArrayList<>();
        if (CollectionsUtil.isNotEmpty(mapList)) {
            for (Map<String, Object> map : mapList) {
                if (map.size() != 0) {
                    id = (String) map.get("id");
                    if (null != id) {
                        list.add(id);
                    }
                }
            }
        }
        if (CollectionsUtil.isNotEmpty(list)) {
            for (String s : list) {
                financeSpecialOrderDao.delete(s);
            }
        }
    }

}
