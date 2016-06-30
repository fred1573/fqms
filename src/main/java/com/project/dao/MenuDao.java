package com.project.dao;

import com.project.entity.Menu;

import java.util.List;

/**
 * @author yuneng.huang on 2016/5/23.
 */
public interface MenuDao {

      List<Menu> selectAll();
}
