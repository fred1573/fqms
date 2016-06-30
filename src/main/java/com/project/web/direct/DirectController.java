package com.project.web.direct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.direct.DirectOrderForm;
import com.project.bean.direct.DirectOrderVo;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.utils.HttpUtil;
import com.project.utils.PageUtil;
import com.project.utils.SystemConfig;
import com.project.utils.encode.PassWordUtil;
import com.project.utils.time.DateUtil;
import com.project.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * 直连订单请求处理类
 * Created by sam on 2015/07/28.
 */
@Controller
@RequestMapping(value = "/direct/order")
public class DirectController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DirectController.class);

    private static final String CURRENT_PAGE = "direct";

    private static final int PAGE_SIZE = 20;

    @RequestMapping
    public String list(Model model, DirectOrderForm directOrderForm) {
        if(StringUtils.isBlank(directOrderForm.getStartDate())) {
            directOrderForm.setStartDate(getCurrentDate());
        }
        if(StringUtils.isBlank(directOrderForm.getEndDate())) {
            directOrderForm.setEndDate(getCurrentDate());
        }
        String searchTimeTyep = directOrderForm.getSearchTimeTyep();
        if(StringUtils.isBlank(searchTimeTyep)) {
            directOrderForm.setSearchTimeTyep("CREATE");
        }
        String resultOta = getOrderOta();
        Map<String, String> otaMap = new HashMap<>();
        if (StringUtils.isNotBlank(resultOta)) {
            JSONObject jsonObject = JSON.parseObject(resultOta);
            String status = jsonObject.getString("status");
            if ("200".equals(status)) {
                JSONArray data = jsonObject.getJSONArray("rows");
                if (!CollectionUtils.isEmpty(data)) {
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject jsonObject1 = JSON.parseObject(data.get(i).toString());
                        String otaId = jsonObject1.getString("otaId");
                        String name = jsonObject1.getString("name");
                        otaMap.put(otaId, name);
                    }
                }
            }
        }

        String result = getOrdersInfo(directOrderForm);
        List<DirectOrderVo> orderList = new ArrayList<>();
        // 构造分页对象
        PageUtil pageUtil = null;
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if ("200".equals(status)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (!CollectionUtils.isEmpty(rows)) {
                    for (int i = 0; i < rows.size(); i++) {
                        String order = rows.get(i).toString();
                        orderList.add(JSONObject.parseObject(order, DirectOrderVo.class));
                    }
                }
                String totalResult = jsonObject.getString("result");
                if(StringUtils.isNotBlank(totalResult)) {
                    JSONObject totalObject = JSON.parseObject(totalResult);
                    String orderNums = totalObject.getString("orderNums");
                    pageUtil = new PageUtil(PAGE_SIZE, Integer.parseInt(orderNums), directOrderForm.getPage());
                }
            }
        }
        model.addAttribute("otaMap", otaMap);
        model.addAttribute("pageUtil", pageUtil);
        model.addAttribute("orderList", orderList);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("directOrderForm", directOrderForm);
        return "/direct/list";
    }

    public static void main(String[] args) {
        DirectOrderForm directOrderForm = new DirectOrderForm();
        directOrderForm.setChannelId("102");
        directOrderForm.setPage(1);
        directOrderForm.setInnName("天字一号房");
        directOrderForm.setStartDate("2014-07-29");
        directOrderForm.setEndDate("2015-07-29");
        directOrderForm.setOrderStatus("1");
        directOrderForm.setSearchTimeTyep("CREATE");
        String result = new DirectController().getOrdersInfo(directOrderForm);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if ("200".equals(status)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                System.out.println("rows:" + rows);
                if (!CollectionUtils.isEmpty(rows)) {
                    for (int i = 0; i < rows.size(); i++) {
                        String order = rows.get(i).toString();
                        DirectOrderVo directOrderVo = JSONObject.parseObject(order, DirectOrderVo.class);
                        System.out.println(directOrderVo);
                    }
                }
            }
        }
    }

    /**
     * 用于请求OMS获取渠道名称和ID的接口
     *
     * @return 接口响应的json字符串
     */
    private String getOrderOta() {
        // 获取时间戳
        long timestamp = System.currentTimeMillis();
        // 获取签名
        String signature = PassWordUtil.getDirectSignature(timestamp);
        StringBuilder stringBuilder = new StringBuilder();
        // 拼接接口请求域名、端口号
        stringBuilder.append(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        // 拼接服务名称
        stringBuilder.append(ApiURL.OMS_QUERY_ORDER_OTA);
        stringBuilder.append("?signature=").append(signature);
        stringBuilder.append("&timestamp=").append(timestamp);
        stringBuilder.append("&otaId=").append(Constants.OMS_PROXY_PID);
        return new HttpUtil().get(stringBuilder.toString());
    }

    /**
     * 请求OMS接口，获取订单信息
     *
     * @param directOrderForm
     * @return
     */
    private String getOrdersInfo(DirectOrderForm directOrderForm) {
        // 获取时间戳
        long timestamp = System.currentTimeMillis();
        // 获取签名
        String signature = PassWordUtil.getDirectSignature(timestamp);
        StringBuilder url = new StringBuilder();
        // 拼接接口请求域名、端口号
        url.append(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        // 拼接服务名称
        url.append(ApiURL.OMS_QUERY_ORDER);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("signature",signature);
        paramMap.put("timestamp",timestamp);
        paramMap.put("otaId",Constants.OMS_PROXY_PID);
        paramMap.put("page",directOrderForm.getPage());
        paramMap.put("innName",directOrderForm.getInnName());
        paramMap.put("orderType", directOrderForm.getSearchTimeTyep());
        paramMap.put("channelId", directOrderForm.getChannelId());
        paramMap.put("status", directOrderForm.getOrderStatus());
        paramMap.put("startDate", directOrderForm.getStartDate());
        paramMap.put("endDate", directOrderForm.getEndDate());
        return new HttpUtil().httpPost(url.toString(), paramMap);
    }

    /**
     * 获取当前时间的字符串格式
     * @return
     */
    private String getCurrentDate() {
        return DateUtil.format(new Date());
    }

}
