/**
* @Title: HotelReviewController.java
* @Package com.project.web.audit
* @Description: 
* @author Administrator
* @date 2014年3月27日 上午11:25:44
*/

/**
 * 
 */
package com.project.web.inn;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.entity.inn.Inn;
import com.project.entity.inn.InnTask;
import com.project.service.inn.InnTaskManager;
import com.project.service.weixin.WeixinService;
import com.project.utils.SignUtil;
import com.project.web.BaseController;

/**
 * 客栈每日任务
 * @author X
 *
 */
@Controller
@RequestMapping(value = "/innTask")
public class InnTaskController extends BaseController {
	@Autowired
	private InnTaskManager innTaskManager;
	@Autowired
	private WeixinService weixinService;
	
	//完成每日任务
	@RequestMapping(value = "finish")
	@ResponseBody
	public AjaxResult index(HttpServletRequest request,HttpServletResponse response) {
		InnTask task = new InnTask();
		Inn inn = new Inn();
		task.setFuncItemType(Integer.parseInt(request.getParameter("funcItemType")));
		inn.setId(Integer.parseInt(request.getParameter("inn.id")));
		task.setInn(inn);
		task.setRecordedAt(new Date());
		innTaskManager.save(task);
		return new AjaxResult(Constants.HTTP_OK,"");
	}
	
	@RequestMapping(value = "receiveWeixin", method = RequestMethod.GET)
	@ResponseBody
	public void receiveWeixin(Model model, HttpServletRequest request
			, HttpServletResponse response){
		//权限验证
		// 微信加密签名  
        String signature = request.getParameter("signature");  
        // 时间戳  
        String timestamp = request.getParameter("timestamp");  
        // 随机数  
        String nonce = request.getParameter("nonce");  
        // 随机字符串  
        String echostr = request.getParameter("echostr");  
  
        PrintWriter out = null;
		try {
			out = response.getWriter();
			if (SignUtil.checkSignature(signature, timestamp, nonce)) {  
				out.print(echostr);  
			}  
		} catch (IOException e) {
			
		} finally{
			out.close();  
		}
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
        out = null;  
	}
	
	@RequestMapping(value = "receiveWeixin", method = RequestMethod.POST)
	@ResponseBody
	public void receiveWeixinPost(Model model, HttpServletRequest request
			, HttpServletResponse response){
		//获取微信转发的信息  然后转发给api
		PrintWriter out = null;
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");  
			// 接收消息、处理消息  
	        String respMessage = weixinService.processRequest(request);
	        // 响应消息  
	        out = response.getWriter();  
	        if(StringUtils.isNoneBlank(respMessage)){
	        	out.print(respMessage);  
	        }
		} catch (Exception e) {
		} finally{
			out.close();  
		}
	}
	
}
