package com.project.service.ota;

import com.project.core.orm.Page;
import com.project.entity.ota.OtaInfo;

import java.util.List;

/**
 * tomato_oms_ota_info  pid=112
 * Created by Administrator on 2015/6/25.
 */
public interface OtaInfoService {

    Page<OtaInfo> list(Page<OtaInfo> page);

    OtaInfo getByOtaId(Integer otaId);

    List<OtaInfo> list();
}
