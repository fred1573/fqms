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

import com.google.common.collect.Maps;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.entity.inn.Inn;
import com.project.service.inn.InnManager;
import com.project.utils.CacheUtil;
import com.project.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 客栈审批和用户重置密码
 * @author cyc
 *
 */
@Controller
@RequestMapping(value = "/inn")
public class InnController extends BaseController {
	@Autowired
	private InnManager innManager;
	
	//结算信息
	@RequestMapping(value = "info")
	public String infoPage(Model model, HttpServletRequest request) {
		model.addAttribute("currentPage", "bankInfo");
		return "/inn/bankInfo";
	}

	@RequestMapping(value = "/updateBrand",method = RequestMethod.POST)
	@ResponseBody
	public AjaxResult updateBrand(Integer innId,boolean brand){
		innManager.updateInnBrand(innId,brand);
		return new AjaxResult(Constants.HTTP_OK,"");
	}
	
	@RequestMapping(value = "/searchInnInfo")
	@ResponseBody
	public AjaxResult searchInnInfo(boolean isFilt, HttpServletRequest request, Page<Inn> page){
		Map<String, Object> result = Maps.newConcurrentMap();
		String keyWord = request.getParameter("keyWord");
		page = innManager.getPage(page, keyWord, isFilt);
		result.put("page", page);
		return new AjaxResult(Constants.HTTP_OK, result, "");
	}
	
	@RequestMapping(value = "/updateBankInfo")
	@ResponseBody
	public AjaxResult updateBankInfo(Inn inn){
		Map<String, Object> result = Maps.newConcurrentMap();
		innManager.saveOrUpdate(inn);
		//更新PMS缓存
		CacheUtil.clearPmsCache(Constants.CACHE_INN_INFO, "updateGeneralCache/", inn.getId());
		return new AjaxResult(Constants.HTTP_OK, result, "");
	}
	
	@RequestMapping(value = "/getProvinceCity")
	@ResponseBody
	public AjaxResult getProvinceCity(Integer type){
		Map<String, Object> result = Maps.newConcurrentMap();
		type = (type == null)?1:type;
		switch (type) {
		case 1:
			result.put("province", CacheUtil.getProvinces());
			result.put("city", CacheUtil.getCities());
			break;
		case 2:
			result.put("province", CacheUtil.getProvinces());
			break;
		case 3:
			result.put("city", CacheUtil.getCities());
			break;
		default:
			result.put("province", CacheUtil.getProvinces());
			result.put("city", CacheUtil.getCities());
			break;
		}
		return new AjaxResult(Constants.HTTP_OK, result, "");
	}
}
