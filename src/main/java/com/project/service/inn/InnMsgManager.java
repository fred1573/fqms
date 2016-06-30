/**
* @Title: HotelReviewManager.java
* @Package com.project.service.audit
* @Description: 
* @author Administrator
* @date 2014年3月27日 上午11:57:56
*/

/**
 * 
 */
package com.project.service.inn;

import java.util.Date;
import java.util.List;




import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.project.bean.MsgSendStatusInfo;
import com.project.common.Constants;
import com.project.dao.inn.InnAdminDao;
import com.project.entity.inn.Inn;
import com.project.entity.inn.InnAdmin;
import com.project.entity.inn.InnMsgLog;
import com.project.entity.inn.InnMsgTemplate;
import com.project.entity.inn.MsgAuto;
import com.project.utils.CommonValidateUtil;
import com.project.utils.ShortMessageUtil;

/**
 * @author X
 *
 */
@Component
@Transactional
public class InnMsgManager {

	@Autowired
	private InnAdminDao innAdminDao;
	
	/**
	 * 发送指定模板的多人短信
	 * 
	 * @param innId
	 * @param type
	 * @param statusId
	 * @param mobile
	 * @param name
	 * @param autos
	 * @return
	 */
	public JSONObject sendMsgsIntellectMany(int innId, int type, int statusId, String mobile, String name, List<MsgAuto> autos) {
		JSONObject result = new JSONObject();
		InnAdmin admin = innAdminDao.findById(innId);
		String innMobile = admin.getMobile();
		List<MsgAuto> autoList = autos;
		if (autoList != null && autoList.size() == 0) {
			result.put(Constants.STATUS, Constants.HTTP_400);
			result.put(Constants.ERRORS, "<p>未设置相关的智能短信发送模板！</p>");
			result.put("noneed", "true");
			return result;
		}
		String[] mobiles = null;
		String[] names = null;
		StringBuilder sb = new StringBuilder("");
		if (StringUtils.isNotBlank(name)) {
			names = name.split(",");
		}
		if (StringUtils.isNotBlank(mobile)) {
			mobiles = mobile.split(",");
			for (int i = 0; i < mobiles.length; i++) {
				int tag;
				if (CommonValidateUtil.checkStr(mobiles[i].trim(), Constants.MOBILE, false) && !mobiles[i].equals("null")) {
					tag = sendMsgsIntellectOnMobile(innId, type, mobiles[i], statusId, autoList, innMobile);
					switch (tag) {
					case Constants.MSG_SEND_OK:
						String customer = "null".equals(names[i]) ? "某客人" : names[i];
						sb.append("<p>向" + customer + " " + mobiles[i] + "发送短信成功</p>");
						break;
					case Constants.MSG_SEND_NO_NUMS:
//						customer = "null".equals(names[i]) ? "某客人" : names[i];
//						sb.append("<p>向" + customer + "发送短信失败：免费短信每月最多发送" + MsgSendCheckUtil.DEFAULT_SEND_NUMS_PER_MONTH + "条哦，亲！</p>");
						break;
					case Constants.MSG_SEND_ERORR:
						customer = "null".equals(names[i]) ? "某客人" : names[i];
						sb.append("<p>向" + customer + "短信通道报错，请致电4000-230-190</p>");
						break;
					}
				} else {
					String customer = "null".equals(names[i]) ? "某客人" : names[i];
					sb.append("<p>向" + customer + "发送短信失败：手机号为空或者非法</p>");
				}
			}
		} else {
			result.put(Constants.STATUS, Constants.HTTP_400);
			result.put(Constants.ERRORS, "<p>智能短信发送失败，没有可用手机号</p>");
			return result;
		}
		result.put(Constants.STATUS, Constants.HTTP_OK);
		result.put(Constants.ERRORS, sb.toString());
		sb = null;
		return result;
	}
	
	/**
	 * 智能发送短信 手机端用(直接在任务调度时定时)
	 * 
	 * @param innId
	 *            客栈ID
	 * @param type
	 *            短信记录所关联的订单类型，2：预定 3：入住 4：退房
	 * @param mobile
	 *            将要发送短信的目标手机号
	 * @param statusId
	 *            短信记录所关联的订单ID
	 * @param autoList
	 *            将要发送的智能模板列表
	 * @return
	 * @author X
	 * @time 2013-11-27
	 */
	public int sendMsgsIntellectOnMobile(int innId, int type, String mobile, int statusId, List<MsgAuto> autoList, String sendFrom) {
		boolean sendFlag = false;
		boolean sendNumsFlag = false;
		sendNumsFlag = true;
		if (sendNumsFlag) {
			for (MsgAuto msgAuto : autoList) {
				MsgSendStatusInfo info = new MsgSendStatusInfo();
				final InnMsgLog msgLog = new InnMsgLog();
				InnMsgTemplate msgTemplate = msgAuto.getInnMsgTemplate();
				msgLog.setFlag(Constants.MSGLOG_FLAG_DOWN);
				msgLog.setSendUserCode(sendFrom);
				msgLog.setSendTime(new Date());
				msgLog.setStatusId(statusId);
				Inn inn = new Inn();
				inn.setId(innId);
				msgLog.setInn(inn);
				msgLog.setMobile(mobile);
				msgLog.setOrderType(type);
				msgLog.setTitle(msgTemplate.getMsgTitle());
				msgLog.setContent(msgTemplate.getMsgContent());
				msgLog.setSendNum(msgTemplate.getMsgContent().length() / 64 + 1);
				msgLog.setSendType(3);
				info = ShortMessageUtil.sendShortMessage(msgLog.getMobile(), msgLog.getContent(), ShortMessageUtil.MESSAGE_TYPE_LONGMSG_PAGE);
				msgLog.setStatus(Constants.MSG_SEND_YES);
				if (info.status) {
					msgLog.setPaidNum(0);
//					msgLog.save();
					sendFlag = true;
				}
			}
		} else {
			return Constants.MSG_SEND_NO_NUMS;
		}
		if (sendFlag) {
			return Constants.MSG_SEND_OK;
		} else {
			return Constants.MSG_SEND_ERORR;
		}
	}
	
	public void sendMsg(String content, String toMobile, String title){
		List<MsgAuto> autos = Lists.newArrayList();
		MsgAuto auto = new MsgAuto();
		InnMsgTemplate tem = new InnMsgTemplate();
		tem.setMsgContent(content);
		tem.setMsgTitle(title);
		auto.setInnMsgTemplate(tem);
		autos.add(auto);
		String mobile = toMobile;
		String name = "客栈主";
		sendMsgsIntellectMany(Constants.SYS_RESOURCE_SUPER_INN
				, 0, 0, mobile, name, autos);
	}
	
	public String appendMsg4Reg(InnAdmin admin){
		StringBuilder sb = new StringBuilder();
		sb.append("Hi,");
		sb.append(admin.getInn().getName()).append("，恭喜您，您的注册信息已审核通过!您的登录账号为：");
		sb.append(admin.getMobile()).append(",").append("赶紧登陆完善您的客栈信息吧，登陆地址：http://www.fanqiele.com/");
		String msg = sb.toString();
		sb = null;
		return msg;
	}
	
}
