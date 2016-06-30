package com.project.service.weixin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;





import com.google.common.collect.Lists;
import com.project.common.Constants;
import com.project.entity.weixin.resp.TextMsg;
import com.project.utils.MessageUtil;
import com.project.utils.ResourceBundleUtil;
import com.project.utils.http.HttpClientUtil;

@Component
@Transactional
public class WeixinService {
	
	/** 
     * 处理微信发来的请求 
     *  
     * @param request 
     * @return 
     */  
    public String processRequest(HttpServletRequest request) {  
        String respMessage = null;  
        try {  
            // 默认返回的文本消息内容  
            String respContent = "";  
            List<NameValuePair> formParams = Lists.newArrayList();
            
            // xml请求解析  
            Map<String, String> requestMap = MessageUtil.parseXml(request);  
  
            // 发送方帐号（open_id）  
            String fromUserName = requestMap.get("FromUserName");  
            // 公众帐号  
            String toUserName = requestMap.get("ToUserName");  
            // 消息类型  
            String msgType = requestMap.get("MsgType");  
            String MsgId = requestMap.get("MsgId");
            String CreateTime = requestMap.get("CreateTime");
            formParams.add(new BasicNameValuePair("fromUserName", fromUserName));
            formParams.add(new BasicNameValuePair("toUserName", toUserName));
            formParams.add(new BasicNameValuePair("msgType", msgType));
            formParams.add(new BasicNameValuePair("MsgId", MsgId));
            formParams.add(new BasicNameValuePair("CreateTime", CreateTime));
  
            // 回复文本消息  
            TextMsg textMessage = new TextMsg();  
            textMessage.setToUserName(fromUserName);  
            textMessage.setFromUserName(toUserName);  
            textMessage.setCreateTime(new Date().getTime());  
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);  
            textMessage.setFuncFlag(0);  
  
            // 文本消息  
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {  
            	String content = requestMap.get("Content");  
            	StringBuilder sb = new StringBuilder();
            	sb.append("MsgId:").append(MsgId).append("\n");
            	sb.append("fromUserName:").append(fromUserName).append("\n");
            	sb.append("toUserName:").append(toUserName).append("\n");
            	sb.append("msgType:").append(msgType).append("\n");
            	sb.append("CreateTime:").append(CreateTime).append("\n");
            	sb.append("content:").append(content).append("\n");
//                respContent = sb.toString();  
                // 文本消息内容
                formParams.add(new BasicNameValuePair("Content", content));
                //转发给api
                HttpClientUtil.getResponseInfoByPost(Constants.HTTP_GET_TYPE_STRING,
                		Constants.SYS_WEIXIN_MSG_URL, formParams);
            }  
            // 图片消息  
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {  
                respContent = "您发送的是图片消息！";  
                // 文本消息内容
                formParams.add(new BasicNameValuePair("Content", "您收到了多媒体信息！请到公众账号查看!"));
                //转发给api
                HttpClientUtil.getResponseInfoByPost(Constants.HTTP_GET_TYPE_STRING,
                		Constants.SYS_WEIXIN_MSG_URL, formParams);
            }  
            // 地理位置消息  
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {  
                respContent = "您发送的是地理位置消息！";
                formParams.add(new BasicNameValuePair("Content", "您收到了多媒体信息！请到公众账号查看!"));
                //转发给api
                HttpClientUtil.getResponseInfoByPost(Constants.HTTP_GET_TYPE_STRING,
                		Constants.SYS_WEIXIN_MSG_URL, formParams);
            }  
            // 链接消息  
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {  
                respContent = "您发送的是链接消息！";
                formParams.add(new BasicNameValuePair("Content", "您收到了多媒体信息！请到公众账号查看!"));
                //转发给api
                HttpClientUtil.getResponseInfoByPost(Constants.HTTP_GET_TYPE_STRING,
                		Constants.SYS_WEIXIN_MSG_URL, formParams);
            }  
            // 音频消息  
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {  
                respContent = "您发送的是音频消息！";
                formParams.add(new BasicNameValuePair("Content", "您收到了多媒体信息！请到公众账号查看!"));
                //转发给api
                HttpClientUtil.getResponseInfoByPost(Constants.HTTP_GET_TYPE_STRING,
                		Constants.SYS_WEIXIN_MSG_URL, formParams);
            }  
            // 事件推送  
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {  
                // 事件类型  
                String eventType = requestMap.get("Event");  
                // 订阅  
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {  
                    respContent = "谢谢您的关注！";  
                }  
                // 取消订阅  
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {  
                    //  取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                }  
                // 自定义菜单点击事件  
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {  
                    //  自定义菜单权没有开放，暂不处理该类消息
                }  
            }  
  
            textMessage.setContent(respContent);  
            respMessage = MessageUtil.textMessageToXml(textMessage);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return respMessage;  
    }  
}  

