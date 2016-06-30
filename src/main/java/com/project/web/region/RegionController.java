/**
* @Title: RegionController.java
* @Package com.project.web.region
* @Description: 
* @author Administrator
* @date 2014骞�鏈�1鏃�涓嬪崍5:57:27
*/

/**
 * 
 */
package com.project.web.region;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.entity.area.InAreaPage;
import com.project.entity.inn.InnAdmin;
import com.project.service.region.RegionManager;
import com.project.web.BaseController;

/**
 * 地区后台
 * @author cyc
 *
 */
@Controller
@RequestMapping(value = "/region")
public class RegionController extends BaseController  {

	@Autowired
	private RegionManager regionManager;
	
	//查询分页
	@RequestMapping(value = "welcome")
	public String list(Model model ,HttpServletRequest request,@ModelAttribute("page") Page<InAreaPage> page)  {	
		initPage(page,15);
		String pageNo = request.getParameter("pageNo");
		if(pageNo!=null){
			page.setPageNo(Integer.parseInt(pageNo));
		}
		String condition = request.getParameter("condition");
		String status = request.getParameter("status");
		if(status==null) {
			status = "周庄";
		}
		String useStatus = request.getParameter("useS");
		if(useStatus==null||useStatus.equals("使用状况")){
			useStatus="";
		}
		page = regionManager.selectList(page,condition,status,useStatus);
		model.addAttribute(page);
		model.addAttribute("currentPage", "region");
		model.addAttribute("condition",condition);
		model.addAttribute("useS",useStatus);
		return "/region/regionBack";
	}
	
	//查询统计数据
	@RequestMapping(value = "count")
	@ResponseBody
	public AjaxResult select(HttpServletRequest request)  {
		String status = request.getParameter("status");
		int id = regionManager.selectId(status);
		String totalCount = regionManager.selectTotalCount(id)+"";
		int count =Integer.parseInt(regionManager.selectCount(id)+"");
		return new AjaxResult(count,totalCount);
	}
	
	//添加客栈
	@RequestMapping(value = "add")
	@ResponseBody
	public AjaxResult addInn(HttpServletRequest request)  {
		String status = request.getParameter("status");
		String phone = request.getParameter("phone");
		InnAdmin admin = regionManager.selectInnId(phone);
		int innId = 0;
		if(admin==null || Constants.MEMBER_STATE_AUDITED != admin.getStatus()) {
			return new AjaxResult(Constants.HTTP_400,admin);
		}else {
			innId = admin.getInn().getId();
		}
			int i=0;
			i = regionManager.selectPhone(innId,status);
			if(i==1) {	
				try  {
					regionManager.addInn(innId, status);
				} catch (Exception e)  {
					logger.error(e.getMessage(), e);
					return new AjaxResult(Constants.HTTP_500,"");
				}
				return new AjaxResult(Constants.HTTP_OK,"");
			}
			else {
				return new AjaxResult(Constants.HTTP_401,"");
			}
	}
	
	// 删除客栈
	@RequestMapping(value = "delete")
	@ResponseBody
	public AjaxResult delete(HttpServletRequest request)  {	
		String id = request.getParameter("id");
		String status = request.getParameter("status");
		regionManager.delete(id,status);	
		return new AjaxResult(Constants.HTTP_400,"");
	}
	
}
