package com.project.service.proxysale;

import com.project.dao.proxysale.OrderComplaintProcessLogDao;
import com.project.dao.proxysale.ProxySaleOrderComplaintDao;
import com.project.entity.account.User;
import com.project.entity.proxysale.OrderComplaintProcessLog;
import com.project.entity.proxysale.ProxySaleOrderComplaint;
import com.project.enumeration.ProxySaleOrderComplaintStatus;
import com.project.enumeration.ProxySaleOrderComplaintType;
import com.project.service.CurrentUserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuneng.huang on 2016/6/13.
 */
@Service
@Transactional("mybatisTransactionManager")
public class OrderComplaintProcessLogServiceImpl implements OrderComplaintProcessLogService {

    @Resource
    private OrderComplaintProcessLogDao orderComplaintProcessLogDao;
    @Resource
    private ProxySaleOrderComplaintDao proxySaleOrderComplaintDao;
    @Resource
    private CurrentUserHolder currentUserHolder;

    @Override
    public void save(OrderComplaintProcessLog processLog) {
        Assert.notNull(processLog);
        Long orderComplaintId = processLog.getOrderComplaintId();
        Assert.notNull(orderComplaintId);
        String note = processLog.getNote();
        if ((!StringUtils.hasText(note)) && note.length() > 80) {
            throw new RuntimeException("最多输入80个字字符");
        }
        ProxySaleOrderComplaint complaint = proxySaleOrderComplaintDao.selectById(orderComplaintId);
        if (complaint == null) {
            throw new RuntimeException("未找到投诉记录id:"+orderComplaintId);
        }
        if (ProxySaleOrderComplaintStatus.FINISH.equals(complaint.getComplaintStatus())) {
            if (!StringUtils.hasText(note)) {
                throw new RuntimeException("完成的投诉跟进记录必填");
            }
            ProxySaleOrderComplaintType complaintType = processLog.getComplaintType();
            if (complaintType == null) {
                throw  new RuntimeException("投诉类型不能为空");
            }
            if (complaint.getComplaintType()!=null&&!complaintType.equals(complaint.getComplaintType())) {
                throw  new RuntimeException("处理完成后类型不可再修改");
            }
        }else if (processLog.getComplaintType() == null && !(StringUtils.hasText(note))) {
            throw new RuntimeException("必须填其中任意一个内容");
        }
        User user = currentUserHolder.getUser();
        processLog.setProcessUserId(user.getId());
        processLog.setProcessUserName(user.getSysUserCode());
        orderComplaintProcessLogDao.insert(processLog);
    }



    @Override
    public List<OrderComplaintProcessLog> findByOrderComplaintId(Long complaintId) {
        return orderComplaintProcessLogDao.selectByOrderComplaintId(complaintId);
    }

    @Override
    public List<OrderComplaintProcessLog> findByOrderNo(String orderNo) {
        return orderComplaintProcessLogDao.selectByOrderNo(orderNo);
    }
}
