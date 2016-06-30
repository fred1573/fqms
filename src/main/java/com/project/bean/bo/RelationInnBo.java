package com.project.bean.bo;

/**
 * @author Administrator
 *         2015-11-20 17:31
 */
public class RelationInnBo {

    private Integer inn_id;
    private Short pattern;
    private Integer outer_id;

    public RelationInnBo() {
    }

    public RelationInnBo(Integer inn_id, Short pattern, Integer outer_id) {
        this.inn_id = inn_id;
        this.pattern = pattern;
        this.outer_id = outer_id;
    }

    public Integer getInn_id() {
        return inn_id;
    }

    public void setInn_id(Integer inn_id) {
        this.inn_id = inn_id;
    }

    public Short getPattern() {
        return pattern;
    }

    public void setPattern(Short pattern) {
        this.pattern = pattern;
    }

    public Integer getOuter_id() {
        return outer_id;
    }

    public void setOuter_id(Integer outer_id) {
        this.outer_id = outer_id;
    }
}
