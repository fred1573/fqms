package com.project.service.finance;

import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.account.HibernateUserDao;
import com.project.dao.finance.FinanceAccountPeriodDao;
import com.project.dao.finance.FinanceInnSettlementDao;
import com.project.dao.finance.FinanceInnSettlementInfoDao;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.finance.FinanceOrderDao;
import com.project.entity.account.User;
import com.project.entity.finance.FinanceAccountPeriod;
import com.project.entity.finance.FinanceInnSettlementInfo;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.finance.FinanceParentOrder;
import com.project.service.account.AccountService;
import com.project.utils.CollectionsUtil;
import com.project.utils.FinanceHelper;
import com.project.utils.time.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sam on 2015/12/30.
 */
@Service("periodService")
@Transactional
public class PeriodServiceImpl implements PeriodService {
    @Autowired
    private AccountService accountService;
    @Resource
    private FinanceHelper financeHelper;
    @Resource
    private FinanceOperationLogDao financeOperationLogDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodServiceImpl.class);
    @Resource
    private FinanceInnSettlementDao financeInnSettlementDao;
    @Resource
    private FinanceAccountPeriodDao financeAccountPeriodDao;
    @Resource
    private FinanceOrderService financeOrderService;
    @Resource
    private FinanceInnSettlementInfoDao financeInnSettlementInfoDao;
    @Autowired
    private HibernateUserDao hibernateUserDao;
    @Resource
    private FinanceOrderDao financeOrderDao;

    @Override
    public List<FinanceAccountPeriod> findAllFinanceAccountPeriod() {
        return financeAccountPeriodDao.selectAllFinanceAccountPeriod();
    }

    @Override
    public Page<FinanceAccountPeriod> selectProxysalePeriod(Page<FinanceAccountPeriod> page) {
        return financeAccountPeriodDao.selectPeriodListByPage(page);
    }

    /**
     * 添加结算账期,
     *
     * @param beginDate
     * @param endDate
     */
    private void savePeriod(String beginDate, String endDate) {
        FinanceAccountPeriod financeAccountPeriod = new FinanceAccountPeriod();
        String settlement = beginDate + "至" + endDate;
        User user = hibernateUserDao.findUserByUserCode(SpringSecurityUtil.getCurrentUserName());
        financeAccountPeriod.setCreator(user);
        financeAccountPeriod.setModifior(user);
        financeAccountPeriod.setSettlementTime(settlement);
        // 默认状态为锁定不能修改
        financeAccountPeriod.setAccountStatus("1");
        financeAccountPeriodDao.save(financeAccountPeriod);
    }

    /**
     * 添加操作记录
     *
     * @param beginDate
     * @param endDate
     */
    private void saveFinanceOperationLog(String beginDate, String endDate, Integer sum) {
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setOperateTime(new Date());
        financeOperationLog.setOperateType("9");
        financeOperationLog.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
        financeOperationLog.setOperateContent("创建结算账期： " + beginDate + "至" + endDate + ",共计" + sum + "个订单");
        financeOperationLog.setSettlementTime(beginDate + "至" + endDate);
        financeOperationLogDao.save(financeOperationLog);

    }

    /**
     * 按账期抓取订单任务
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public void updateFinanceOrderByPeriod(String beginDate, String endDate) {
        String settlement = beginDate + "至" + endDate;
        LOGGER.info("开始创建账期：" + settlement);
        //查询数据库判断账期是否已经存在
        FinanceAccountPeriod financeAccountPeriod1 = financeAccountPeriodDao.findFinanceAccountPeriodWithSettlementTime(settlement);
        if (financeAccountPeriod1 != null) {
            throw new RuntimeException("该账期订单已更新，不能重复更新");
        }
        List<FinanceParentOrder> financeParentOrderList = financeHelper.getCheckOutForPeriodFinanceOrderFromOMS(beginDate, endDate);
        LOGGER.info("完成按账期从OMS抓取订单操作");
        if(CollectionsUtil.isNotEmpty(financeParentOrderList)) {
            LOGGER.info("本次同步账单数量：" + financeParentOrderList.size());
        }
        if (!CollectionUtils.isEmpty(financeParentOrderList)) {
            List<String> orderIdList = new ArrayList<>();
            for (FinanceParentOrder financeParentOrder : financeParentOrderList) {
                orderIdList.add(financeParentOrder.getId());
            }
            // 批量处理订单
            List<List<String>> lists = CollectionsUtil.splitList(orderIdList, 500);
            for(List<String> orders : lists) {
                // 更新订单的核单状态
                financeOrderDao.updateFinanceParentOrderSettlementTime(CollectionsUtil.convertToDBString(orders),settlement);
            }
        }
        LOGGER.info("完成更新账单操作");
        //操作日志
        saveFinanceOperationLog(beginDate, endDate, financeParentOrderList.size());
        LOGGER.info("完成保存操作日志");
        //保存账期
        savePeriod(beginDate, endDate);
        LOGGER.info("完成创建账期操作");
    }

    @Override
    public void updateFinanceOrder(String beginDate, String endDate) {
        String settlement = beginDate + "至" + endDate;
        List<FinanceParentOrder> financeParentOrderList = financeHelper.getCheckOutForPeriodFinanceOrderFromOMS(beginDate, endDate);
        if (!CollectionUtils.isEmpty(financeParentOrderList)) {
            for (FinanceParentOrder financeParentOrder : financeParentOrderList) {
                financeParentOrder.setSettlementTime(settlement);
                financeOrderService.createFinanceOrder(financeParentOrder);
            }
        }
    }

    /**
     * 获取最近账期
     *
     * @return
     */
    @Override
    public String getRecentlySettlementTime() {
        return financeAccountPeriodDao.selectRecentlyPeriod();
    }

    /**
     * 获取最后操作时间
     *
     * @return
     */
    public Date getOperateTime(String settlementTime) {
        FinanceOperationLog financeOperationLog = financeOperationLogDao.findFinanceOperationLogWithSettlementTime(settlementTime, "3");
        if (financeOperationLog != null) {
            return financeOperationLog.getOperateTime();
        }
        return null;
    }

    /**
     * 按账期更新客栈确认状态
     *
     * @param recentlySettlementTime
     */
    @Override
    public void updateConfirmStatusWithSettlementTime(String recentlySettlementTime) {
        financeInnSettlementDao.updateConfirmStatusWithSettlementTime(recentlySettlementTime);
    }

    /**
     * 系统自动更新确认状态
     */
    public void autoUpdateConfirmStatus() {
        String recentlySettlementTime = getRecentlySettlementTime();
        if (StringUtils.isBlank(recentlySettlementTime)) {
            LOGGER.info("账期异常，系统自动确认账单定时任务异常终止");
            return;
        }
        List<Map<String, Object>> maps = financeInnSettlementDao.selectUnConfirmInnCount(recentlySettlementTime);
        if (CollectionsUtil.isEmpty(maps)) {
            LOGGER.info("没有未结算客栈，系统自动确认账单定时任务异常终止");
            return;
        }
        Map<String, Object> stringObjectMap = maps.get(0);
        if (stringObjectMap != null) {
            int total = Integer.parseInt(String.valueOf(stringObjectMap.get("total")));
            if (total == 0) {
                LOGGER.info("没有未结算客栈，系统自动确认账单定时任务异常终止");
                return;
            }
            // 获取最后结算操作时间
            Date operateTime = getOperateTime(recentlySettlementTime);
            if (operateTime != null) {
                // 计算发送账单的小时数
                int hours = DateUtil.getDifferHour(operateTime, new Date());
                if (hours > Constants.CONFIRM_HOURS) {
                    //自动确认客栈状态
                    updateConfirmStatusWithSettlementTime(recentlySettlementTime);
                    LOGGER.info("系统自动确认接受账单" + recentlySettlementTime + "定时任务结束");
                }
            }
        }
    }

    @Override
    public void updateInnInfo() {
        List<Map<String, Object>> dataMapList = financeInnSettlementDao.getInnIdList();
        if (CollectionsUtil.isNotEmpty(dataMapList)) {
            for (Map<String, Object> dataMap : dataMapList) {
                Integer innId = Integer.parseInt(String.valueOf(dataMap.get("id")));
                FinanceInnSettlementInfo financeInnSettlementInfo = financeHelper.getInnInfo(innId);
                try {
                    if (financeInnSettlementInfo != null) {
                        financeInnSettlementInfoDao.save(financeInnSettlementInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
