package com.project.bean;

import com.project.bean.vo.AjaxBase;

import java.util.List;
import java.util.Map;

/**
 * PMS活动请求接口返回数据类
 * Created by admin on 2016/5/23.
 */
public class AjaxOperateActivity extends AjaxBase {
    private List<Map<String, Object>> result;
    private Integer pageNum;
    private Integer totalRecords;

    public AjaxOperateActivity(int status, String message, List<Map<String, Object>> result, Integer PageNum, Integer totalRecords) {
        super(status, message);
        this.pageNum = PageNum;
        this.result = result;
        this.totalRecords = totalRecords;
    }

    public AjaxOperateActivity(int status, String message) {
        super(status, message);
    }

    public AjaxOperateActivity(int status) {
        super(status);
    }


    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        pageNum = pageNum;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

}
