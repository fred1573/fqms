package com.project.service.roomStatus;

import com.alibaba.fastjson.JSONObject;
import com.project.bean.vo.RoomStatusVo;
import com.project.common.Constants;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.inn.InnDao;
import com.project.entity.finance.FinanceOperationLog;
import com.project.utils.CollectionsUtil;
import com.project.utils.FinanceHelper;
import com.tomato.mq.client.support.MQClientBuilder;
import com.tomato.mq.support.core.SysMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sam on 2016/4/15.
 */
@Service("roomStatusService")
@Transactional
public class RoomStatusServiceImpl implements RoomStatusService {
    @Resource
    private InnDao innDao;
    @Resource
    private FinanceOperationLogDao financeOperationLogDao;
    @Resource
    private FinanceHelper financeHelper;

    @Override
    public List<RoomStatusVo> findRoomStatusByUserCode(String userCode) {
        List<RoomStatusVo> list = null;
        if(StringUtils.isNotBlank(userCode)) {
            List<Map<String, Object>> listMap = innDao.selectInnAdminByUserCode(userCode);
            if(CollectionsUtil.isNotEmpty(listMap)) {
                list = new ArrayList<>();
                for(Map<String, Object> map : listMap) {
                    Integer innId = Integer.parseInt(String.valueOf(map.get("inn_id")));
                    String innName = String.valueOf(map.get("inn_name"));
                    String userCodeOld = String.valueOf(map.get("user_code"));
                    Integer adminType = Integer.parseInt(String.valueOf(map.get("admin_type")));
                    String adminTypeStr = String.valueOf(map.get("admin_type_str"));
                    RoomStatusVo roomStatusVo = new RoomStatusVo(innId, innName, userCodeOld, adminType, adminTypeStr);
                    list.add(roomStatusVo);
                }
            }
        }
        return list;
    }

    @Override
    public void modifyRoomStatus(Integer innId, Integer adminType, String innName) {
        // 根据客栈ID，更新全部房态切换状态
        innDao.updateInnAdminType(innId, adminType);
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setOperateUser(financeHelper.getCurrentUser());
        financeOperationLog.setOperateType("202");
        financeOperationLog.setOperateObject(innName);
        String adminTypeStr = "PMS";
        if(adminType == 2) {
            adminTypeStr = "EBK";
        }
        financeOperationLog.setOperateContent("将房态切换为" + adminTypeStr);
        // 记录操作日志
        financeOperationLogDao.save(financeOperationLog);
        Map<String, Object> map = new HashMap<>();
        map.put("innId", innId);
        map.put("adminType", adminType);
        // 向消息中心放事件
        MQClientBuilder.build().send(new SysMessage(Constants.MQ_PROJECT_IDENTIFICATION, Constants.MQ_EVENT_ROOM_STATUS_SWITCH, new JSONObject(map).toJSONString()));
    }
}
