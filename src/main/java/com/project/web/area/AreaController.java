package com.project.web.area;

import com.project.bean.vo.AjaxBase;
import com.project.common.Constants;
import com.project.entity.area.Area;
import com.project.service.area.AreaService;
import com.project.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2015/6/19.
 */
@Controller
@RequestMapping(value = "/area", method = RequestMethod.GET)
public class AreaController extends BaseController{

    @Autowired
    private AreaService areaService;

    @RequestMapping("/{id}/children")
    @ResponseBody
    public AjaxArea children(@PathVariable("id")Integer id, Model model){
        Area area;
        try {
            area = areaService.get(id);
        }catch (NullPointerException e){
            return new AjaxArea(Constants.HTTP_500, "区域不存在,id="+id);
        }
        List<ApiArea> apiAreas = new ArrayList<>(area.getChildren().size());
        for (Area child : area.getChildren()) {
            apiAreas.add(new ApiArea(child));
        }
        return new AjaxArea(Constants.HTTP_OK, apiAreas);
    }

    private class AjaxArea extends AjaxBase {

        List<ApiArea> areas;

        public AjaxArea(int status, String message) {
            super(status, message);
        }

        public AjaxArea(int status, List<ApiArea> apiAreas) {
            super(status);
            areas = apiAreas;
        }

        public List<ApiArea> getAreas() {
            return areas;
        }

        public void setAreas(List<ApiArea> areas) {
            this.areas = areas;
        }
    }
}
