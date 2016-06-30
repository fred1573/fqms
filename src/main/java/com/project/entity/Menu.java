package com.project.entity;

import com.project.entity.account.Authority;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单
 * @author yuneng.huang on 2016/5/23.
 */
public class Menu extends IdEntity{

    private String name;
    private String code;

    private List<Authority> authorityList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Authority> getAuthorityList() {
        return authorityList;
    }

    public void setAuthorityList(List<Authority> authorityList) {
        this.authorityList = authorityList;
    }
}
