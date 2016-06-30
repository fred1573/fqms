package com.project.web.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.entity.Menu;
import com.project.entity.account.Role;
import com.project.entity.account.User;
import com.project.service.account.AccountService;
import com.project.service.account.MenuService;
import com.project.service.account.RoleService;
import com.project.web.BaseController;
import com.project.web.view.TreeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门（角色）管理
 *
 * @author mowei
 */
@Controller
@RequestMapping(value = "/role")
public class RoleController extends BaseController{

	@Autowired
	private AccountService accountService;
	@Resource
	private RoleService roleService;
	@Resource
	private TreeBuilder treeBuilder;
	@Resource
	private MenuService menuService;

	@ModelAttribute
	public void setContext(Model model) {
		model.addAttribute("currentUser", getCurrentUser());
	}

	@RequestMapping("/index")
	public String tree(Model model,Long id,@RequestParam(defaultValue = "1")int currentPage) {
		Role rootRole = getCurrentUser().getRootRole();
		if (id == null) {
			id = rootRole.getId();
		}
		Role role = roleService.findById(id);
		model.addAttribute("roleTreeJson",treeBuilder.build(role).toJsonString());
		model.addAttribute("role", role);
		PageBounds pageBounds = new PageBounds(currentPage, 10);
		PageList<User> pageList = accountService.findByPage(pageBounds, null, id);
		model.addAttribute("pageList", pageList);
		model.addAttribute("paginator", pageList.getPaginator());
		List<Menu> menuList = menuService.findAll();
		model.addAttribute("menuList", menuList);
		model.addAttribute("currentPage", "role");
		return "/account/permission";
	}

	@RequestMapping("/getDescendant")
	@ResponseBody
	public AjaxResult getDescendant() {
		User currentUser = getCurrentUser();
		Role rootRole = currentUser.getRootRole();
		List<Role> descendant = roleService.findDescendant(rootRole.getId());
		return new AjaxResult(Constants.HTTP_OK, descendant);
	}

	@RequestMapping("/detail")
	@ResponseBody
	public AjaxResult detail(@RequestParam(required = true)Long id,@RequestParam(defaultValue = "1")int currentPage) {
		JSONObject jsonObject = new JSONObject();
		Role role=roleService.findById(id);
		jsonObject.put("role", role);
		PageBounds pageBounds = new PageBounds(currentPage, 10);
		PageList<User> pageList = accountService.findByPage(pageBounds, null, id);
		jsonObject.put("pageList", pageList);
		jsonObject.put("paginator", pageList.getPaginator());
		return new AjaxResult(Constants.HTTP_OK, jsonObject);
	}

	@RequestMapping("/search")
	@ResponseBody
	public AjaxResult search(@RequestParam(required = true) Long id, String likeName, @RequestParam(defaultValue ="1" ) int currentPage) {
		JSONObject jsonObject = new JSONObject();
		PageBounds pageBounds = new PageBounds(currentPage, 10);
		PageList<User> pageList = accountService.findByPage(pageBounds, likeName, id);
		jsonObject.put("pageList", pageList);
		jsonObject.put("paginator", pageList.getPaginator());
		return new AjaxResult(Constants.HTTP_OK, jsonObject);
	}


	@RequestMapping("/save")
	@ResponseBody
	public AjaxResult save(String data) {
		Role role = JSON.parseObject(data, Role.class);
		roleService.save(role);
		return new AjaxResult(Constants.HTTP_OK,null);
	}

	@RequestMapping("/updateAuthority")
	@ResponseBody
	public AjaxResult updateAuthority(@RequestParam(required = true)Long roleId,@RequestParam(required = true)String authorityIds) {
		String[] authorityIdArray = StringUtils.commaDelimitedListToStringArray(authorityIds);
		roleService.updateAuthority(roleId, CollectionUtils.arrayToList(authorityIdArray));
		return new AjaxResult(Constants.HTTP_OK,null);
	}

	@RequestMapping("/update")
	@ResponseBody
	public AjaxResult update(String data) {
		Role role = JSON.parseObject(data, Role.class);
		roleService.update(role);
		return new AjaxResult(Constants.HTTP_OK,null);
	}

	@RequestMapping("/updateUser")
	@ResponseBody
	public AjaxResult updateUser(User user,Long roleId) {
		List<Role> roles = new ArrayList<>();
		Role role = new Role();
		role.setId(roleId);
		roles.add(role);
		user.setRoleList(roles);
		accountService.update(user);
		return new AjaxResult(Constants.HTTP_OK,null);
	}

	@RequestMapping("/saveUser")
	@ResponseBody
	public AjaxResult saveUser(User user,Long roleId) {
		List<Role> roles = new ArrayList<>();
		Role role = new Role();
		role.setId(roleId);
		roles.add(role);
		user.setRoleList(roles);
		accountService.save(user);
		return new AjaxResult(Constants.HTTP_OK,null);
	}


	@RequestMapping("/delete")
	@ResponseBody
	public AjaxResult delete(Role role) {
		roleService.delete(role);
		return new AjaxResult(Constants.HTTP_OK,null);
	}

	@RequestMapping("/deleteUser")
	@ResponseBody
	public AjaxResult deleteUser(User user) {
		accountService.remove(user);
		return new AjaxResult(Constants.HTTP_OK,null);
	}

	@RequestMapping("/disable")
	@ResponseBody
	public AjaxResult disable(Role role) {
		roleService.disable(role);
		return new AjaxResult(Constants.HTTP_OK,null);
	}

	@RequestMapping("/disableUser")
	@ResponseBody
	public AjaxResult disableUser(User user) {
		accountService.disable(user);
		return new AjaxResult(Constants.HTTP_OK,null);
	}


	@RequestMapping("/enabled")
	@ResponseBody
	public AjaxResult enabled(Role role) {
		roleService.enabled(role);
		return new AjaxResult(Constants.HTTP_OK,null);
	}

	@RequestMapping("/enabledUser")
	@ResponseBody
	public AjaxResult enabledUser(User user) {
		accountService.enabled(user);
		return new AjaxResult(Constants.HTTP_OK,null);
	}


}
