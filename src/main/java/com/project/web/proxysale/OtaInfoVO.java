package com.project.web.proxysale;

import com.project.entity.ota.OtaInfo;

/**
 * @author Hunhun
 *         2015-09-17 16:25
 */
public class OtaInfoVO {

    private OtaInfo otaInfo;
    //是否可关联
    private boolean canRelate = false;
    //是否已选择关联
    private boolean selected = false;

    public OtaInfo getOtaInfo() {
        return otaInfo;
    }

    public void setOtaInfo(OtaInfo otaInfo) {
        this.otaInfo = otaInfo;
    }

    public boolean isCanRelate() {
        return canRelate;
    }

    public void setCanRelate(boolean canRelate) {
        this.canRelate = canRelate;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
