package com.project.utils;

import java.util.List;



import com.project.bean.Item;
import com.project.bean.vo.CityVo;
import com.project.cache.abstractCache.IDataCache;
import com.project.cache.manager.LoadCacheDataManager;
import com.project.common.Constants;
import com.project.core.utils.spring.SpringContextHolder;
import com.project.dao.inn.InnAdminDao;
import com.project.entity.wg.WgOtaInfo;
import com.project.service.wg.WgOtaInfoManager;
import com.project.utils.http.HttpClientUtil;



public class CacheUtil {
	
	
	@SuppressWarnings("unchecked")
	public static List<Item> getCacheByTypeCode(String type){
		IDataCache dataCache = LoadCacheDataManager.get("SysConstantCache");
		List<Item> list = (List<Item>) dataCache.get(type);
		return list;
	}
	
	public static String getDictionaryName(String type, Integer typeId){
		List<Item> list = getCacheByTypeCode(type);
		String tmpId = String.valueOf(typeId);
		for (Item item : list) {
			if(item.getValue().equals(tmpId)){
				return item.getName();
			}
		}
		return "怎么没找到";
	}
	
	@SuppressWarnings("unchecked")
	public static List<WgOtaInfo> getWgOtaInfos(){
		List<WgOtaInfo> list = null;
		IDataCache dataCache = LoadCacheDataManager.get("SysConstantCache");
		if(dataCache.get("otaInfos") == null){
			WgOtaInfoManager wgOtaInfoManager = SpringContextHolder.getBean("wgOtaInfoManager");
			list = wgOtaInfoManager.getAll();
			dataCache.put("otaInfos", list);
		}else{
			list = (List<WgOtaInfo>) dataCache.get("otaInfos");
		}
		return list;
	}
	
	/**
	 * 获取省列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<CityVo> getProvinces(){
		List<CityVo> list = null;
		IDataCache dataCache = LoadCacheDataManager.get(Constants.FQMS_CACHE_SYS);
		if(dataCache.get(Constants.FQMS_CACHE_PROVINCE) == null){
			InnAdminDao innAdminDao = SpringContextHolder.getBean("innAdminDao");
			String sql = "select distinct r.province_name as name,r.province_code as code from tomato_inn_region r ";
			list = (List<CityVo>) innAdminDao.findWithNoIdSql(CityVo.class, sql);
			dataCache.put(Constants.FQMS_CACHE_PROVINCE, list);
		}else{
			list = (List<CityVo>) dataCache.get(Constants.FQMS_CACHE_PROVINCE);
		}
		return list;
	}
	
	/**
	 * 获取市列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<CityVo> getCities(){
		List<CityVo> list = null;
		IDataCache dataCache = LoadCacheDataManager.get(Constants.FQMS_CACHE_SYS);
		if(dataCache.get(Constants.FQMS_CACHE_CITY) == null){
			InnAdminDao innAdminDao = SpringContextHolder.getBean("innAdminDao");
			String sql = "select distinct r.city_name as name,r.city_code as code from tomato_inn_region r ";
			list = (List<CityVo>) innAdminDao.findWithNoIdSql(CityVo.class, sql);
			dataCache.put(Constants.FQMS_CACHE_CITY, list);
		}else{
			list = (List<CityVo>) dataCache.get(Constants.FQMS_CACHE_CITY);
		}
		return list;
	}

	/**
	 * 清除PMS对应的缓存
	 * @param code
	 * @param innId
	 */
	public static void clearPmsCache(String code, String uri, Integer innId) {
		String url = SystemConfig.PMS_URL + uri +code+innId;
		HttpClientUtil.getResponseInfoByGet(Constants.HTTP_GET_TYPE_STRING, url);
	}
	
}
