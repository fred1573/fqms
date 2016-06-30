package com.project.service.region;

import com.project.dao.region.RegionDao;
import com.project.entity.inn.InnRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author frd
 */
@Service("regionService")
@Transactional
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionDao regionDao;

    @Override
    public InnRegion get(Integer id) {
        return regionDao.get(id);
    }
}
