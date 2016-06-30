package com.project.service.roomStatus;

import com.project.bean.vo.RoomStatusVo;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 房态切换管理业务逻辑处理接口
 * Created by sam on 2016/4/15.
 */
@Component
@Transactional
public interface RoomStatusService {
    /**
     * 根据PMS注册账号，查询客栈房态切换对象
     * @param userCode PMS注册账号
     * @return
     */
    List<RoomStatusVo> findRoomStatusByUserCode(String userCode);

    /**
     * 修改指定客栈的房态状态
     * @param innId PMS客栈ID
     * @param adminType 当前房态
     * @param innName PMS客栈名称
     */
    void modifyRoomStatus(Integer innId, Integer adminType, String innName);
}
