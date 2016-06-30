package com.project.service.area;

import com.project.entity.area.Area;

import java.util.Set;

/**
 *
 * Created by Administrator on 2015/6/8.
 */
public interface AreaService {

    Area get(Integer id);

    Set<Area> getCollection(String[] idArr);

    Area getCityByCode(String code);
}
