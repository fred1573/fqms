package com.project.service.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.proxysale.PricePatternDao;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.proxysale.ProxyInn;
import com.project.entity.proxysale.ProxyRoomType;
import com.project.entity.proxysale.ProxyRoomTypeVo;
import com.project.utils.CollectionsUtil;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by admin on 2016/5/10.
 */
@Service
@Transactional
public class ProxyInnRoomTypeServiceImpl implements ProxyInnRoomTypeService {

    @Resource
    private FinanceOperationLogDao financeOperationLogDao;
    @Resource
    private ProxyInnService proxyInnService;
    @Resource
    private PricePatternDao pricePatternDao;

    /**
     * 保存下架房型操作日志
     */
    @Override
    public void saveOperateLog(String data) {
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        JSONObject jsonObject = JSONObject.parseObject(data);
        String innName = jsonObject.getString("innName");
        financeOperationLog.setOperateObject(innName);
        financeOperationLog.setOperateType("116");
        JSONArray base = jsonObject.getJSONArray("baseName");
        JSONArray sale = jsonObject.getJSONArray("saleName");
        StringBuilder stringBuilder = new StringBuilder("本次下架房型【");
        if (CollectionsUtil.isNotEmpty(sale)) {
            stringBuilder.append("代销房型：");
            for (int i = 0; i < sale.size(); i++) {
                stringBuilder.append(sale.get(i).toString() + ",");
            }
        }
        if (CollectionsUtil.isNotEmpty(base)) {
            stringBuilder.append("活动房型：");
            for (int i = 0; i < base.size(); i++) {
                stringBuilder.append(base.get(i).toString() + ",");
            }
        }
        stringBuilder.append("】");
        int index = stringBuilder.toString().lastIndexOf(',');
        stringBuilder.deleteCharAt(index);
        financeOperationLog.setOperateContent(stringBuilder.toString());
        financeOperationLog.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
        financeOperationLogDao.save(financeOperationLog);
    }


    /**
     * 从OMS获取房型
     *
     * @return
     */
    @Override
    public JSONObject getRoomTypeFromOMS(Integer innId) {
        StringBuilder url = new StringBuilder();
        url.append(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        Map<String, Object> paramMap = new HashMap<>();
        url.append(ApiURL.OMS_INN_ROOM_TYPE);

        paramMap.put("otaId", Constants.OMS_PROXY_PID + ",106");
        paramMap.put("innId", innId);
        String result = new HttpUtil().httpPost(url.toString(), paramMap);
        if (StringUtils.isBlank(result)) {
            throw new RuntimeException("OMS返回数据为空");
        }
        JSONObject jsonObject = JSON.parseObject(result);
        String status = jsonObject.getString("status");
        if (!"200".equals(status)) {
            throw new RuntimeException("OMS接口请求失败，原因：" + jsonObject.getString("message"));
        } else {
            return jsonObject;
        }
    }

    /**
     * 解析封装房型
     *
     * @param jsonObject
     * @return
     */
    @Override
    public Map<String, List<ProxyRoomType>> packRoomType(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            Map<String, List<ProxyRoomType>> map = new HashMap<>();
            List<ProxyRoomType> saleType = new ArrayList<>();
            List<ProxyRoomType> baseType = new ArrayList<>();
            ProxyRoomType proxyRoomType = new ProxyRoomType();
            String object;
            if (CollectionsUtil.isNotEmpty(jsonArray)) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    object = jsonArray.get(i).toString();
                    proxyRoomType = JSONObject.parseObject(object, ProxyRoomType.class);
                    if (null != proxyRoomType && proxyRoomType.getOtaId() == 102) {
                        if (proxyRoomType.getStrategyType() == 1) {
                            baseType.add(proxyRoomType);
                        } else {
                            saleType.add(proxyRoomType);

                        }
                    }
                }
            }
            map.put("base", baseType);
            map.put("sale", saleType);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void downRoomType(Map<Integer, String> map) {
        if (map.size() > 0) {
            Iterator<Map.Entry<Integer, String>> entrys = map.entrySet().iterator();
            while (entrys.hasNext()) {
                Map.Entry<Integer, String> entry = entrys.next();
                Integer accountId = entry.getKey();
                String roomType = entry.getValue();
                downRoomType(accountId, roomType);
            }
        } else {
            throw new RuntimeException("选择的下架房型为空");
        }
    }

    @Override
    public void downRoomType(Integer accountId, String roomTypeId) {

        StringBuilder url = new StringBuilder();
        url.append(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        Map<String, Object> paramMap = new HashMap<>();
        url.append(ApiURL.DOWN_ROOM_TYPE);

        paramMap.put("accountId", accountId);
        paramMap.put("roomTypeIds", roomTypeId);
        String result = new HttpUtil().httpPost(url.toString(), paramMap);
        if (StringUtils.isBlank(result)) {
            throw new RuntimeException("OMS返回数据为空");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String status = jsonObject.getString("status");
        if (!status.equals("200")) {
            throw new RuntimeException("OMS接口请求失败，原因：" + jsonObject.getString("message"));
        }
    }

    @Override
    public Map<Integer, Integer> getSaleAccountId() {
        Map<Integer, Integer> map = new HashMap<>();
        List<Map<String, Object>> accountId = pricePatternDao.findAccountId();
        Integer innId;
        Integer account;
        if (CollectionsUtil.isNotEmpty(accountId)) {
            for (Map<String, Object> m : accountId) {
                innId = (Integer) m.get("innid");
                account = (Integer) m.get("accountid");
                map.put(innId, account);
            }
            return map;
        }
        return null;
    }

    /**
     * 封装accountId
     *
     * @param list
     * @param map
     * @return
     */
    public List<ProxyInn> packAccountId(List<ProxyInn> list, Map<Integer, Integer> map) {
        if (CollectionsUtil.isNotEmpty(list) && map.size() > 0) {
            List<ProxyInn> newList = new ArrayList<>();
            for (ProxyInn proxyInn : list) {
                proxyInn.setAccountId(map.get(proxyInn.getId()));
                newList.add(proxyInn);
            }
            return newList;
        }
        return list;
    }

    /**
     * 解析房型json数据
     *
     * @param json
     * @return
     */
    public Map<Integer, String> parseRoomType(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        List<ProxyRoomTypeVo> sale = new ArrayList<>();
        List<ProxyRoomTypeVo> base = new ArrayList<>();
        JSONArray saleA = jsonObject.getJSONArray("sale");
        if (CollectionsUtil.isNotEmpty(saleA)) {
            for (int i = 0; i < saleA.size(); i++) {
                String s = saleA.get(i).toString();
                ProxyRoomTypeVo proxyRoomTypeVo = JSONObject.parseObject(s, ProxyRoomTypeVo.class);
                sale.add(proxyRoomTypeVo);
            }
        }
        JSONArray baseA = jsonObject.getJSONArray("base");
        if (CollectionsUtil.isNotEmpty(baseA)) {
            for (int i = 0; i < baseA.size(); i++) {
                String s = baseA.get(i).toString();
                ProxyRoomTypeVo proxyRoomTypeVo = JSONObject.parseObject(s, ProxyRoomTypeVo.class);
                base.add(proxyRoomTypeVo);
            }
        }
        Map<Integer, String> map = new HashMap<>();

        //封装卖价房型
        if (CollectionsUtil.isNotEmpty(sale)) {
            String accountId = sale.get(0).getAccountId();
            StringBuilder stringBuilder = new StringBuilder();

            for (ProxyRoomTypeVo proxyRoomTypeVo : sale) {
                stringBuilder.append(proxyRoomTypeVo.getOmsRoomTypeId() + ",");
            }
            int index = stringBuilder.toString().lastIndexOf(',');
            stringBuilder.deleteCharAt(index);
            map.put(Integer.parseInt(accountId), stringBuilder.toString());
        }
        //封装底价房型
        if (CollectionsUtil.isNotEmpty(base)) {
            String accountId = base.get(0).getAccountId();
            StringBuilder stringBuilder = new StringBuilder();
            for (ProxyRoomTypeVo proxyRoomTypeVo : base) {
                stringBuilder.append(proxyRoomTypeVo.getOmsRoomTypeId() + ",");
            }
            int index = stringBuilder.toString().lastIndexOf(',');
            stringBuilder.deleteCharAt(index);
            map.put(Integer.parseInt(accountId), stringBuilder.toString());
        }

        return map;
    }

}
