package com.project.web.area;

import com.project.entity.area.Area;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Administrator on 2015/6/19.
 */
public class ApiArea {

    private Integer id;
    private String name;

    public ApiArea(Area area) {
        try {
            BeanUtils.copyProperties(this, area);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
