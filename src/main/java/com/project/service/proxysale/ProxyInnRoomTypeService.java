package com.project.service.proxysale;

import com.alibaba.fastjson.JSONObject;
import com.project.entity.proxysale.ProxyInn;
import com.project.entity.proxysale.ProxyRoomType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/5/10.
 */
@Service
@Transactional
public interface ProxyInnRoomTypeService {
    /**
     * 获取所有代销房型
     *
     * @param innId
     * @return
     */
    JSONObject getRoomTypeFromOMS(Integer innId);

    /**
     * \
     * 封装底价卖家房型
     *
     * @param jsonObject
     * @return
     */
    Map<String, List<ProxyRoomType>> packRoomType(JSONObject jsonObject);

    /**
     * 下架房型
     *
     * @param accountId
     * @param roomTypeId
     */
    void downRoomType(Integer accountId, String roomTypeId);

    /**
     * 获取卖价accountId
     *
     * @return
     */
    Map<Integer, Integer> getSaleAccountId();

    /**
     * 封装accountId
     *
     * @param list
     * @param map
     * @return
     */
    List<ProxyInn> packAccountId(List<ProxyInn> list, Map<Integer, Integer> map);

    /**
     * 解析房型ID
     *
     * @param json
     * @return
     */
    Map<Integer, String> parseRoomType(String json);

    /**
     * 下架房型
     *
     * @param map
     */
    void downRoomType(Map<Integer, String> map);

    /**
     * 记录下架房型日志
     * @param data
     */
    void saveOperateLog( String data);
}
