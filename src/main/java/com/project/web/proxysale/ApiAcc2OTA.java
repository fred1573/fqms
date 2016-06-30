package com.project.web.proxysale;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/29.
 */
public class ApiAcc2OTA {

    List<Integer> otas = new ArrayList<>();

    Integer accId;

    public List<Integer> getOtas() {
        return otas;
    }

    public void setOtas(List<Integer> otas) {
        this.otas = otas;
    }

    public Integer getAccId() {
        return accId;
    }

    public void setAccId(Integer accId) {
        this.accId = accId;
    }
}
