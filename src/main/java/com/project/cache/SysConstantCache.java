package com.project.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.project.bean.Item;
import com.project.cache.abstractCache.AbstractEhCache;
import com.project.entity.common.SysDictionary;
import com.project.service.common.SysDictionaryManager;

/**
 * 字典缓存
 * @author mowei
 *
 */
public class SysConstantCache extends AbstractEhCache {
	
	@Autowired
	private SysDictionaryManager sysDictionaryManager;
	
	/**
	 * 根据缓存name获取相应的cache配置，如果获取失败，使用默认sysCache；如果不写构造方法，也使用默认的sysCache
	 */
	public SysConstantCache(){
		cache = cacheManager.getCache("sysConstantCache")==null?cacheManager.getCache("sysCache"):cacheManager.getCache("sysConstantCache");
	}

	public void loadCache() {
//		List<SysDictionary> list = sysDictionaryManager.findByPropertyOrderby("status", Status.ENABLED, "conseq");
		List<SysDictionary> list = sysDictionaryManager.getAllSysDictionary();
		for (Object obj : list) {
			SysDictionary sysDictionary = (SysDictionary) obj;
			List<Item> tmp = (List<Item>) get(sysDictionary.getContyp());
			if (tmp == null) {
				tmp = new ArrayList<Item>();
			}
			Item item = new Item();
			item.setName(sysDictionary.getConnam());
			item.setValue(sysDictionary.getConval());
			item.setStatus(sysDictionary.getStatus());
			tmp.add(item);
			put(sysDictionary.getContyp(), tmp);
		}
	}

}
