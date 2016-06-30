package com.project.service.finance;

import com.alibaba.fastjson.JSONObject;
import com.project.core.orm.Page;
import com.project.dao.finance.FinanceArrearInnDao;
import com.project.dao.finance.FinanceInnSettlementDao;
import com.project.entity.finance.FinanceArrearInn;
import com.project.entity.finance.FinanceInnSettlement;
import com.project.utils.CollectionsUtil;
import com.project.utils.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by admin on 2016/3/9.
 */
@Service("financeArrearInnService")
@Transactional
public class FinanceArrearInnServiceImpl implements FinanceArrearInnService {
    @Resource
    private FinanceArrearInnDao financeArrearInnDao;
    @Resource
    private FinanceInnSettlementDao financeInnSettlementDao;


    @Override
    public void FinanceLevelArrears(String jsonData) {

            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String paymentRemark = jsonObject.getString("paymentRemark");
            String settlementTime = jsonObject.getString("settlementTime");
            if (StringUtils.isBlank(settlementTime)) {
                throw new RuntimeException("账期不能为空");
            }
            Integer innId = jsonObject.getInteger("id");
            if (innId == null) {
                throw new RuntimeException("客栈ID不能为空");
            }
            List<FinanceInnSettlement> financeInnSettlementList = financeInnSettlementDao.findFinanceInnSettlementWithArrears(innId, settlementTime);
            if (CollectionsUtil.isEmpty(financeInnSettlementList)) {
                throw new RuntimeException("未找到平账客栈信息");
            }
            FinanceInnSettlement financeInnSettlement = financeInnSettlementList.get(0);
            BigDecimal arrearRemaining = financeInnSettlement.getArrearsRemaining();
            BigDecimal remaining = jsonObject.getBigDecimal("remaining");
            if (arrearRemaining == null) {
                throw new RuntimeException("往期挂账金额为空");
            }
            if (remaining == null) {
                throw new RuntimeException("平账金额为空");
            }
            List<FinanceInnSettlement> financeInnSettlementNextArrears = financeInnSettlementDao.findFinanceInnSettlementNextArrears(innId, settlementTime);
            if (CollectionsUtil.isNotEmpty(financeInnSettlementNextArrears)) {
                for (FinanceInnSettlement f : financeInnSettlementNextArrears) {
                    if (f.getIsArrears().equals(FinanceInnSettlement.PARTIAL_ARREARS_TAG) || f.getIsArrears().equals(FinanceInnSettlement.LEVEL_ARREARS_TAG)) {
                        throw new RuntimeException("本客栈后期存在平账，不能在本账期平账");
                    }
                    f.setArrearsRemaining(NumberUtil.wrapNull(f.getArrearsRemaining()).subtract(remaining));
                    f.setArrearsPast(NumberUtil.wrapNull(f.getArrearsPast()).subtract(remaining));
                    financeInnSettlementDao.save(f);
                }
            }

            if (arrearRemaining.compareTo(remaining) == 0) {
                financeInnSettlementDao.FinanceLevelArrears(innId, settlementTime);
                //保存挂账处理记录
                List<FinanceArrearInn> list = financeArrearInnDao.findFinanceArrearInn(settlementTime, innId);
                FinanceArrearInn financeArrearInn;
                if (CollectionsUtil.isNotEmpty(list)) {
                    financeArrearInn = list.get(0);
                } else {
                    financeArrearInn = new FinanceArrearInn();
                }
                financeArrearInn.setArrearRemaining(BigDecimal.ZERO);
                financeArrearInn.setArrearPast(remaining);
                financeArrearInn.setSettlementTime(settlementTime);
                financeArrearInn.setRemark(paymentRemark);
                financeArrearInn.setInnId(innId);
                financeArrearInn.setManualLevel(true);
                financeArrearInnDao.save(financeArrearInn);
                //更新客栈信息
                financeInnSettlement.setArrearsPast(financeInnSettlement.getArrearsRemaining());
                financeInnSettlement.setArrearsRemaining(BigDecimal.ZERO);
                financeInnSettlement.setAfterPaymentAmount(BigDecimal.ZERO);
                financeInnSettlement.setArrearsRemaining(BigDecimal.ZERO);
                financeInnSettlement.setIsArrears("1");
                financeInnSettlement.setSettlementStatus("1");
                financeInnSettlementDao.save(financeInnSettlement);
            } else {
                throw new RuntimeException("平账金额不等于剩余挂账金额");
            }
        }


    @Override
    public List<FinanceInnSettlement> findPastArrears(Integer innId, String settlementTime) {
        List<FinanceArrearInn> financeArrearInnList = financeArrearInnDao.findFinanceArrearInn(innId, settlementTime);
        List<String> list = new ArrayList<>();
        if (CollectionsUtil.isNotEmpty(financeArrearInnList)) {
            for (FinanceArrearInn f : financeArrearInnList) {
                list.add(f.getSettlementTime());
            }
            if (CollectionsUtil.isNotEmpty(list)) {
                List<FinanceInnSettlement> financeInnSettlementList = new ArrayList<>();
                for (String s : list) {
                    FinanceInnSettlement financeInnSettlement = financeInnSettlementDao.findFinanceInnSettlement(innId, s);
                    if (financeInnSettlement != null) {
                        financeInnSettlementList.add(financeInnSettlement);
                    }
                }
                if (CollectionsUtil.isNotEmpty(financeInnSettlementList)) {
                    return financeInnSettlementList;
                }
            }
        }
        return null;
    }


    @Override
    public void deleteFinanceArrearsInn(String settlementTime) {
        financeArrearInnDao.deleteFinanceArrearsInn(settlementTime);
    }


    @Override
    public Page<FinanceInnSettlement> getTotalArrearsPage(Page<FinanceInnSettlement> page, String settlementTime, String arrearsStatus, String innName) {
        List<FinanceInnSettlement> totalArrearsWithSettlementTime = financeInnSettlementDao.findTotalArrearsWithSettlementTime(settlementTime, arrearsStatus, innName);
        Map<Integer, FinanceInnSettlement> map = new LinkedHashMap<>();
        if (CollectionsUtil.isNotEmpty(totalArrearsWithSettlementTime)) {
            for (FinanceInnSettlement financeInnSettlement : totalArrearsWithSettlementTime) {
                Integer id = financeInnSettlement.getFinanceInnSettlementInfo().getId();
                if (map.get(id) == null) {
                    map.put(id, financeInnSettlement);
                }
            }
            List<FinanceInnSettlement> list = getArrearsInnList(map);
            page = getArrearsInnPage(list, page);
        }
        return page;
    }

    /**
     * 从map封装finganceSettlement到list
     *
     * @param map
     * @return
     */
    public List<FinanceInnSettlement> getArrearsInnList(Map<Integer, FinanceInnSettlement> map) {
        List<FinanceInnSettlement> list = new ArrayList<>();
        Iterator<Map.Entry<Integer, FinanceInnSettlement>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, FinanceInnSettlement> next = iterator.next();
            FinanceInnSettlement value = next.getValue();
            list.add(value);
        }
        return list;
    }

    /**
     * 从list封装financeSettlement到page
     *
     * @param list
     * @param page
     * @return
     */
    public Page<FinanceInnSettlement> getArrearsInnPage(List<FinanceInnSettlement> list, Page<FinanceInnSettlement> page) {
        page.setTotalCount(list.size());
        int pageSize = page.getPageSize();
        List<FinanceInnSettlement> pageList = new ArrayList<>();
        if (pageSize >= list.size()) {
            page.setResult(list);
        } else {
            int pageNo = page.getPageNo();
            int j = pageNo * pageSize - 1;
            if (pageNo * pageSize > list.size()) {
                j = list.size() - 1;
            }
            for (int i = (pageNo - 1) * pageSize; i <= j; i++) {
                FinanceInnSettlement financeInnSettlement = list.get(i);
                pageList.add(financeInnSettlement);
            }
            page.setResult(pageList);
        }
        return page;
    }
}
