package com.project.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import com.project.cache.abstractCache.IDataCache;
import com.project.cache.manager.LoadCacheDataManager;
import com.project.entity.account.Authority;

public class CustomerSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	private static Logger logger = LoggerFactory.getLogger(CustomerSecurityMetadataSource.class);
	
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		// object 是一个URL，被用户请求的url。
		Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
		String url = ((FilterInvocation) object).getRequestUrl();
		logger.info("---------访问URL为:"+url);
        int m = url.indexOf("?");
        if (m != -1)
            url = url.substring(1, m);
        else
        	url = url.substring(1);
        IDataCache dataCache = LoadCacheDataManager.get("SysUrlToAuthorityCache");
        if(dataCache != null){
        	@SuppressWarnings("unchecked")
			List<Authority> as = (List<Authority>) dataCache.get(url);
            if(as!=null && as.size()>0){
//            	logger.info("---------该URL所拥有的权限列表为:" + as.toString());
            	for(Authority a :as){
                	if(StringUtils.isNotBlank(a.getSysAuthorityName())){
                        ConfigAttribute ca = new SecurityConfig(a.getPrefixedName());
                        atts.add(ca);
                    }
                }
            	return atts;
            }
        }
        return null;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}
	
	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}
	
}

