package com.project.web.roomStatus;

import com.project.bean.vo.AjaxBase;
import com.project.bean.vo.RoomStatusVo;
import com.project.common.Constants;
import com.project.service.roomStatus.RoomStatusService;
import com.project.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 房态切换控制器对象
 * Created by sam on 2016/4/15.
 */
@Controller
@RequestMapping(value = "/roomStatus/")
public class RoomStatusController  extends BaseController {
    private static final String CURRENT_PAGE = "statusSwitch";
    @Resource
    private RoomStatusService roomStatusService;

    /**
     * 根据PMS注册账号，查询客栈房态切换对象
     * @param model
     * @param userCode PMS注册账号
     * @return
     */
    @RequestMapping("list")
    public String orderList(Model model, String userCode) {
        List<RoomStatusVo> roomStatusVoList = roomStatusService.findRoomStatusByUserCode(userCode);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("list", roomStatusVoList);
        model.addAttribute("userCode", userCode);
        return "/roomStatus/list";
    }

    /**
     * 根据PMS客栈ID和指定的房态，修改客栈房态切换
     * @param adminType 房态类型
     * @param innId PMS客栈ID
     * @param innName PMS客栈名称
     * @return
     */
    @RequestMapping("modify")
    @ResponseBody
    public AjaxBase modify(Integer adminType, Integer innId, String innName) {
        try {
            roomStatusService.modifyRoomStatus(innId, adminType, innName);
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "修改失败：" + e);
        }
        return new AjaxBase(Constants.HTTP_OK, "修改成功");
    }

}
