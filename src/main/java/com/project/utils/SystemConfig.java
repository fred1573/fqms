package com.project.utils;

import com.project.common.ApiURL;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2015/6/12.
 */
public class SystemConfig {

    public static final String XZ_URL = "xz.url";
    public static final String OMS_URL = "oms.url";
    public static final String CRM_URL = "crm.url";
    public static final String FTP_URL = "ftp.url";
    public static final String PMS_URL = "pms.url";
    public static final String TOMS_URL = "toms.url";
    public static final String IMG_URL = "img.url";



    public static final Map<String, String> PROPERTIES = new HashMap<>();

    static {
        try {
            String profiles = System.getProperty("spring.profiles.active");
            if (profiles == null) {
                profiles = "production";
            }
            Properties p = new Properties();
            String path = "/${profiles}/service_interface_url.properties";
            path = path.replace("${profiles}", profiles);
            p.load(SystemConfig.class.getResourceAsStream(path));
            PROPERTIES.put(XZ_URL, p.get(XZ_URL).toString());
            PROPERTIES.put(OMS_URL, p.get(OMS_URL).toString());
            PROPERTIES.put(CRM_URL, p.get(CRM_URL).toString());
            PROPERTIES.put(FTP_URL, p.get(FTP_URL).toString());
            PROPERTIES.put(PMS_URL, p.get(PMS_URL).toString());
            PROPERTIES.put(TOMS_URL, p.get(TOMS_URL).toString());
            PROPERTIES.put(IMG_URL, p.get(IMG_URL).toString());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        String url = SystemConfig.PROPERTIES.get(SystemConfig.PMS_URL) + ApiURL.PMS_INN_INFO + "124";
        System.out.println(url);
    }
}
