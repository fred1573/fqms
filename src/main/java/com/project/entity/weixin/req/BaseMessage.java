package com.project.entity.weixin.req;


public class BaseMessage {
	
	/** 开发者微信号  */
	private String ToUserName;
	
	/** 发送方帐号（一个OpenID）  */
	private String FromUserName;
	
	/** 消息创建时间 （整型）  */
	private Long CreateTime;
	
	/**
	 * 消息类型
	 * text:文本信息
	 * image:图片链接
	 * voice:语音
	 * video:视频
	 * location:地理位置
	 * link:链接
	 */
	private String MsgType;
	
	/** 消息id，64位整型  */
	private Long MsgId;

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public Long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public Long getMsgId() {
		return MsgId;
	}

	public void setMsgId(Long msgId) {
		MsgId = msgId;
	}
	
	
	
}
