package com.project.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;



import com.project.bean.MsgSendStatusInfo;
import com.project.common.Constants;
import com.project.utils.http.HttpClientUtil;



/**
 * Created with IntelliJ IDEA.
 * User: Dong Wang
 * Date: 13-9-5
 * Time: 下午3:38
 */
public class ShortMessageUtil {
    private static final String SMS_URL = "http://pi.noc.cn/SendSMS.aspx";
    public static final int MESSAGE_TYPE_SINGLE = 1; //单条短信，不超过70字符
    public static final int MESSAGE_TYPE_LONGMSG_NOPAGE = 2;//长短信，自动切割多条发送，无页码
    public static final int MESSAGE_TYPE_LONGMSG_PAGE = 3;//长短信，自动切割多条发送，含页码
    public static final int MESSAGE_TYPE_LONGMSG = 5;//长短信，不切割，不超过500字符,默认值

    private static final String ACCOUNT = "100606";
    private static final String PASSWORD = "fqll888";

    private static final String AUTOGRAPH = "【番茄系统】";

    /**
     * @param tel         手机号码，多个请用","隔开
     * @param content     短信内容
     * @param messageType 短信类别
     */
    public static MsgSendStatusInfo sendShortMessage(String tel, String content, int messageType) {
    	MsgSendStatusInfo info = new MsgSendStatusInfo();
        content += AUTOGRAPH;
//        content = clearContent(content);
        String url = SMS_URL + "?Msisdn=" + tel + "&SMSContent=" + content + "&MSGType=" + messageType + "&LongCode=" + "&ECECCID=" + ACCOUNT + "&Password=" + PASSWORD;
        try {
        	String a = HttpClientUtil.getResponseInfoByGet(Constants.HTTP_GET_TYPE_STRING, url);
        	String[] result = a.split("\\|");
            info.code = result[0];
            info.info = result[2];
            if("1".equals(result[0])){
            	info.status = true;
            	return info;
            }else{
            	info.status = false;
            	info.info = replaceErrorMsg(info.code, info.info);
            	return info;
            }
        } catch (Exception e) {
            return info;
        }
    }

    private static Set<String> loadSensitiveWords() {
        Set<String> result = new HashSet<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ShortMessageUtil.class.getResourceAsStream("/sensitivewords.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                result.add(line);
            }
            br.close();
        } catch (IOException e) {
        }
        return result;
    }

    private static String clearContent(String content) {
        Set<String> limits = loadSensitiveWords();
        for (String limit : limits) {
            try {
                content = content.replace(limit, "***");
            } catch (Exception e) {
                continue;
            }
        }

        try {
            content = URLEncoder.encode(content, "UTF-8");
        } catch (Exception e) {
        }
        return content.trim();
    }
    
    private static String replaceErrorMsg(String code, String info){
    	String msg = info;
    	switch(code){
    	case "3013":
    		msg = "该手机用户把短信通道号码列入了黑名单，无法向其发送短信。";
    		break;
    	}
    	return msg;
    }
    
}
