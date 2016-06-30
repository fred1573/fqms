package com.project.service.proxysale;

import com.project.dao.proxysale.ProxyInnOnoffDao;
import com.project.entity.proxysale.ProxyInnOnoff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Date;

/**
 * @author Administrator
 *         2015-10-15 16:26
 */
@Service
@Transactional
public class ProxyInnOnoffServiceImpl implements ProxyInnOnoffService {

    @Autowired
    private ProxyInnOnoffDao proxyInnOnoffDao;

    @Override
    public void save(ProxyInnOnoff innOnoff) {
        proxyInnOnoffDao.save(innOnoff);
    }

    @Override
    public Long count(Integer proxyInnId, Short pattern) {
        return proxyInnOnoffDao.countForLongWithSql("select * from tomato_proxysale_inn_onoff ioo where ioo.proxy_inn=? and ioo.pattern=?", proxyInnId, pattern);
    }

    @Override
    public Long count(Integer proxyInnId, Short pattern, Date from, Date to) {
        return null;
    }

	@Override
	public ProxyInnOnoff getLastRow(Integer innerId) {
		return proxyInnOnoffDao.selectLastRow(innerId);
	}
}
