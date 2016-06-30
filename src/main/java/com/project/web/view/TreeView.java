package com.project.web.view;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @author yuneng.huang on 2016/5/19.
 */
public class TreeView {

    private String id;
    private String name;
    private List<TreeView> children;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TreeView> getChildren() {
        return children;
    }

    public void setChildren(List<TreeView> children) {
        this.children = children;
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
