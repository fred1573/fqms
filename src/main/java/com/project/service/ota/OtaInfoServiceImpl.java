package com.project.service.ota;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.dao.ota.OtaInfoDao;
import com.project.entity.ota.OtaInfo;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Administrator on 2015/6/25.
 */
@Service("otaInfoService")
public class OtaInfoServiceImpl implements OtaInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtaInfoServiceImpl.class);

    @Autowired
    private OtaInfoDao otaInfoDao;

    @Override
    public Page<OtaInfo> list(Page<OtaInfo> page) {
        return otaInfoDao.list(page);
    }

    @Override
    public OtaInfo getByOtaId(Integer otaId) {
        if(otaId != null){
            return getOtaInfo(new HttpUtil().get(getUrl(otaId)));
        }
        return null;
    }

    @Override
    public List<OtaInfo> list() {
        return getOtaInfos(new HttpUtil().get(getUrl(null)));
    }

    private List<OtaInfo> getOtaInfos(String jsonStr) {

        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (jsonObject.getInteger("status") == Constants.HTTP_OK){
                JSONArray jsonOtaInfos = jsonObject.getJSONArray("otaInfo");
                if(jsonOtaInfos != null && jsonOtaInfos.size() > 0){
                    List<OtaInfo> otaInfos = new ArrayList<>();
                    for (int i = 0; i < jsonOtaInfos.size(); i++) {
                        JSONObject jsonOtaInfo = (JSONObject) jsonOtaInfos.get(i);
                        OtaInfo otaInfo = JSON.parseObject(jsonOtaInfo.toJSONString(), OtaInfo.class);
                        otaInfos.add(otaInfo);
                    }
                    return otaInfos;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private OtaInfo getOtaInfo(String jsonStr) {

        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (jsonObject.getInteger("status") == Constants.HTTP_OK){
                JSONArray jsonOtaInfos = jsonObject.getJSONArray("otaInfo");
                if(jsonOtaInfos != null && jsonOtaInfos.size() > 0){
                    for (int i = 0; i < jsonOtaInfos.size(); i++) {
                        JSONObject jsonOtaInfo = (JSONObject) jsonOtaInfos.get(i);
                        OtaInfo otaInfo = JSON.parseObject(jsonOtaInfo.toJSONString(), OtaInfo.class);
                        return otaInfo;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getUrl(Integer otaId) {
        Map<String, String> params = new HashMap<>();
        params.put("otaPid", Constants.OMS_PROXY_PID.toString());
        if(otaId != null){
            params.put("otaId", otaId.toString());
        }
        return new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL),
                ApiURL.OMS_OTA_INFO,
                params);
    }

}
