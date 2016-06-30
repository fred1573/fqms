package com.project.service.direct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.proxysale.CancelOrderLogDao;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.proxysale.CancelOrderLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2016/4/28.
 */
@Service
@Transactional
public class CancelOrderLogServiceImpl implements CancelOrderLogService {

    @Resource
    private CancelOrderLogDao cancelOrderLogDao;
    @Resource
    private FinanceOperationLogDao financeOperationLogDao;
    @Override
    public void save(String remark, String channelNo) {
        CancelOrderLog cancelOrderLog = new CancelOrderLog();
        if (StringUtils.isBlank(channelNo)) {
            throw new RuntimeException("数据错误");
        }
        if (StringUtils.isBlank(channelNo)) {
            throw new RuntimeException("渠道订单号为空");
        }
        cancelOrderLog.setChannelOrderNo(channelNo);
        cancelOrderLog.setRemark(remark);
        cancelOrderLog.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(date);
        cancelOrderLog.setOperateTime(time);
        cancelOrderLogDao.save(cancelOrderLog);
    }

    @Override
    public CancelOrderLog findCancelOrderLogWithChannelNo(String channelNo) {
        return cancelOrderLogDao.findCancelLogWithChannelNo(channelNo);
    }
    @Override
    public void SaveOperateLog(String remark,String channelNo){
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setOperateTime(new Date());
        financeOperationLog.setOperateType("10");
        financeOperationLog.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
        financeOperationLog.setOperateContent("取消订单"+channelNo+"备注："+remark);
        financeOperationLogDao.save(financeOperationLog);

    }

}
