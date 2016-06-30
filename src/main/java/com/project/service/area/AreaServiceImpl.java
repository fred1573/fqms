package com.project.service.area;

import com.project.dao.area.AreaDao;
import com.project.entity.area.Area;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by Administrator on 2015/6/8.
 */
@Service("areaService")
@Transactional
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaDao areaDao;

    @Override
    public Area get(Integer id) {
        return areaDao.get(id);
    }

    @Override
    public Set<Area> getCollection(String[] idArr) {
        if(idArr.length <= 0){
            return null;
        }
        Set<Area> results = new HashSet<>();
        for (String id : idArr) {
            results.add(get(Integer.parseInt(id)));
        }
        return results;
    }

    @Override
    public Area getCityByCode(String code) {
        return areaDao.getCityByCode(code);
    }
}
