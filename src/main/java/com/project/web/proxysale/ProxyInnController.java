package com.project.web.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.vo.AjaxBase;
import com.project.bean.vo.AjaxResult;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.entity.area.Area;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.ota.OtaInfo;
import com.project.entity.proxysale.*;
import com.project.service.ota.OtaInfoService;
import com.project.service.proxysale.*;
import com.project.utils.CollectionsUtil;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import com.project.utils.encode.MD5;
import com.project.web.BaseController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/6/11.
 */
@Controller
@RequestMapping("/proxysale/inn")
public class ProxyInnController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ProxyInnController.class);
    //每页最大记录数
    private static final int PAGE_SIZE = 15;
    //left.jsp-->当前页面
    private static final String CURRENT_PAGE = "proxysale";
    public static final int EXPORT_SIZE = 60;
    public static final int PAGE_NO_MAX = 4;

    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private OtaInfoService otaInfoService;
    @Autowired
    private ChannelInnService channelInnService;
    @Autowired
    private ProxyInnOnoffService proxyInnOnoffService;
    @Autowired
    private ProxyAuditService proxyAuditService;
    @Autowired
    private OnOffRoomService onOffRoomService;
    @Autowired
    private ProxyInnRoomTypeService proxyInnRoomTypeService;

    /**
     * 代销客栈下架
     *
     * @param proxyInnId   代销客栈ID
     * @param pricePattern 价格策略
     * @param reason       下架原因
     * @return
     */
    @RequestMapping("offShelf")
    @ResponseBody
    public AjaxResult offShelf(Integer proxyInnId, Short pricePattern, String reason) {
        try {
            proxyInnService.offShelfProxyInn(proxyInnId, pricePattern, reason);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "success");
    }

    /**
     * 根据客栈ID和价格策略查询客栈的抽佣比例
     *
     * @param proxyInnId   代销客栈ID
     * @param pricePattern 价格策略
     * @return
     */
    @RequestMapping("getProxyInnPricePattern")
    @ResponseBody
    public AjaxResult getProxyInnPricePattern(Integer proxyInnId, Short pricePattern, Integer innId) {
        try {
            return new AjaxResult(Constants.HTTP_OK, proxyInnService.getProxyInnPricePatternByProxyInn(proxyInnId, pricePattern, innId), "success");
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }

    /**
     * 根据代销客栈ID和价格策略，查询客栈和渠道的关联关系
     *
     * @param proxyInnId   代销客栈ID
     * @param innId        客栈ID
     * @param pricePattern 价格策略
     * @return
     */
    @RequestMapping("/getChannelByType")
    @ResponseBody
    public AjaxResult getChannelByType(Integer proxyInnId, Float pricePattern, Integer innId) {
        try {
            return new AjaxResult(Constants.HTTP_OK, proxyInnService.getProxyInnChannel(proxyInnId, pricePattern, innId), "success");
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }



    /**
     * 根据区域ID和价格策略，查询全部渠道
     *
     * @param areaId       区域ID
     * @param pricePattern 价格策略
     * @return
     */
    @RequestMapping("/getChannelByArea")
    @ResponseBody
    public AjaxResult getChannelByArea(Integer areaId, Short pricePattern) {
        try {
            return new AjaxResult(Constants.HTTP_OK, proxyInnService.getChannelByArea(areaId, pricePattern), "success");
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }

    /**
     * 区域批量上架客栈指定价格策略
     *
     * @param jsonData 参数的json字符串
     * @return
     */
    @RequestMapping("/batchOnShelfByArea")
    @ResponseBody
    public AjaxResult batchOnShelfByArea(String jsonData) {
        try {
            proxyInnService.batchOnShelfByArea(jsonData, true);
            return new AjaxResult(Constants.HTTP_OK, "success");
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }

    /**
     * 区域批量下架客栈指定价格策略
     *
     * @param jsonData 参数的json字符串
     * @return
     */
    @RequestMapping("/batchOffShelfByArea")
    @ResponseBody
    public AjaxResult batchOffShelfByArea(String jsonData) {
        try {
            proxyInnService.batchOnShelfByArea(jsonData, false);
            return new AjaxResult(Constants.HTTP_OK, "success");
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }

    /**
     * 设置渠道、普通代销上架、精品代销上架
     *
     * @param jsonData 提交数据的字符串数组
     * @return
     */
    @RequestMapping("/modifyProxyInnChannel")
    @ResponseBody
    public AjaxResult modifyProxyInnChannel(String jsonData) {
        try {
            proxyInnService.modifyProxyInnChannel(jsonData);
            return new AjaxResult(Constants.HTTP_OK, "success");
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }

    @RequestMapping("/{id}/modify")
    @ResponseBody
    public AjaxResult modify(@PathVariable("id") Integer id,
                             @RequestParam("baseArr") String[] baseArr,
                             @RequestParam("saleArr") String[] saleArr) {
        ProxyInn proxyInn = proxyInnService.get(id);
        List<ProxysaleChannel> pcs = new ArrayList<>();
        Short baseStrategy = 1;
        Short saleStrategy = 2;
        if (null != baseArr && baseArr.length > 0) {
            for (String key : baseArr) {
                ProxysaleChannel ps = new ProxysaleChannel();
                ps.setStrategy(baseStrategy);
                ps.setChannel(this.channelService.get(Integer.parseInt(key)));
                ps.setValid(true);
                ps.setCreateTime(new Date());
                ps.setOperator(proxyInnService.getCurrentUser());
                pcs.add(ps);
            }
        }
        if (null != saleArr && saleArr.length > 0) {
            for (String key : saleArr) {
                ProxysaleChannel ps = new ProxysaleChannel();
                ps.setStrategy(saleStrategy);
                ps.setValid(true);
                ps.setCreateTime(new Date());
                ps.setChannel(this.channelService.get(Integer.parseInt(key)));
                ps.setOperator(proxyInnService.getCurrentUser());
                pcs.add(ps);
            }
        }
        proxyInnService.modifyBackend(proxyInn, pcs);
        return new AjaxResult(Constants.HTTP_OK, "");
    }

    //    @Log(module = "代销客栈", descr = "获取代销客栈列表")
    @RequestMapping
    public String list(Model model, @ModelAttribute Page<ProxyInn> page,
                       @RequestParam(value = "innName", required = false) String innName,
                       @RequestParam(value = "pricePattern", required = false) Integer pricePattern,
                       @RequestParam(value = "status", required = false) Integer status,
                       @RequestParam(value = "areaName", required = false) String areaName) {

        long currentTimeMillis = System.currentTimeMillis();
        initPage(page, PAGE_SIZE);
        Integer areaId = null;
        Area area = null;
        if (StringUtils.isNotBlank(areaName)) {
            area = proxyInnService.getAreaByAreaName(areaName);
            if (area != null) {
                areaId = area.getId();
            }
        }
        Map<Integer, Integer> saleAccountId = proxyInnRoomTypeService.getSaleAccountId();
        page = proxyInnService.find(page, areaId, innName, pricePattern, status);
        List<ProxyInn> innList = (List<ProxyInn>) page.getResult();
        page.setResult(proxyInnRoomTypeService.packAccountId(innList, saleAccountId));
        if (!CollectionsUtil.isEmpty(page.getResult())) {
            model.addAttribute("area", area);
        }
        model.addAttribute(page);
        model.addAttribute("innName", innName);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("summary", proxyInnService.getProxyInnSummary(areaId));
        model.addAttribute("toadyUp", proxyInnService.getUpAndDownSummary(areaId, null, true));
        model.addAttribute("currentBtn", "inn");
        model.addAttribute("status", status);
        model.addAttribute("innName", innName);
        logger.info("客栈管理列表页本次执行时间：" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
        return "proxysale/inn/list";
    }


    /**
     * 根据客栈ID获取客栈的关房记录
     *
     * @param innId
     * @return
     */
    @RequestMapping("/getInnCloseInfo")
    @ResponseBody
    public AjaxResult getInnCloseInfo(Integer innId) {
        return onOffRoomService.getInnCloseInfo(innId);
    }

    /**
     * 区域关房操作
     *
     * @param closeInfo
     * @return
     */
    @RequestMapping("/areaOff")
    @ResponseBody
    public AjaxResult areaOff(String closeInfo) {
//        return new AjaxResult(200, "关房操作成功");
        return onOffRoomService.areaOff(closeInfo);
    }

    /**
     * 客栈关房
     *
     * @return
     */
    @RequestMapping("/innOff")
    @ResponseBody
    public AjaxResult innOff(String closeInfo) {
        return onOffRoomService.innOff(closeInfo);
    }

    /**
     * 批量开房
     *
     * @param closeBeginDate
     * @param closeEndDate
     * @return
     */
    @RequestMapping("/batchOpenRoom")
    @ResponseBody
    public AjaxResult innOff(String closeBeginDate, String closeEndDate) {
        return onOffRoomService.batchOpenRoom(closeBeginDate, closeEndDate);
    }


    @RequestMapping("/{id}/channels")
    @ResponseBody
    public AjaxRelationChannel getRelationChannels(@PathVariable("id") Integer id) {
        List<OtaInfoVO> otaInfoVOs = new ArrayList<>(5);
        List<OtaInfo> otaInfos = otaInfoService.list();
        if (CollectionUtils.isEmpty(otaInfos)) {
            return new AjaxRelationChannel(Constants.HTTP_OK, otaInfoVOs);
        }
        List<Channel> channelsWithoutPersistence = proxyInnService.getRelationChannels(id);
        for (OtaInfo otaInfo : otaInfos) {
            OtaInfoVO vo = new OtaInfoVO();
            vo.setOtaInfo(otaInfo);
            vo.setSelected(channelInnService.isExist(otaInfo.getOtaId(), id));
            for (Channel channel : channelsWithoutPersistence) {
                if (otaInfo.getOtaId().equals(channel.getId())) {
                    vo.setCanRelate(true);
                    break;
                }
            }
            otaInfoVOs.add(vo);
        }
        return new AjaxRelationChannel(Constants.HTTP_OK, otaInfoVOs);
    }

    @RequestMapping(value = "/{id}/detail", method = RequestMethod.GET)
    public String detail(Model model, @PathVariable("id") Integer id) {
        ProxyInn proxyInn = proxyInnService.get(id);
        model.addAttribute("proxyInn", proxyInn);
        model.addAttribute("summary", proxyInnService.getUpAndDownSummary(null, id, false));
        try {
            model.addAttribute("contact", proxyInnService.getSignManager(proxyInn.getInn()));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("contact", "");
        }
        model.addAttribute("offInfo", proxyInnOnoffService.getLastRow(id));
        System.out.println(proxyAuditService.hasContractssChecked(proxyInn.getInn()));
        model.addAttribute("hasPass", proxyAuditService.hasContractssChecked(proxyInn.getInn()));
        return "proxysale/inn/detail";
    }

    /**
     * 获取房型价格信息
     */
    @RequestMapping(value = "/{accountId}/inninfo", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult innInfo(@PathVariable("accountId") Integer outerId,
                              @RequestParam(value = "pageNo", required = false) Integer pageNo) {
        String innInfo;
        try {
            pageNo = (pageNo == null || pageNo < 1 || pageNo > PAGE_NO_MAX) ? 1 : pageNo;
            innInfo = getInnInfo(outerId, pageNo);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "获取客栈详情信息失败");
        }
        JSONObject jsonObject = JSON.parseObject(innInfo);
        if (jsonObject.getInteger("status") == 200) {
            removeInfoUnused(jsonObject);
            jsonObject.put("pageNo", pageNo);
            return new AjaxResult(Constants.HTTP_OK, jsonObject);
        }
        return new AjaxResult(Constants.HTTP_500, jsonObject.getString("message"));
    }


    private void removeInfoUnused(JSONObject jsonObject) {
        JSONArray list = jsonObject.getJSONArray("list");
        for (int i = 0; i < list.size(); i++) {
            JSONObject json = (JSONObject) list.get(i);
            json.remove("roomArea");
            json.remove("bedWid");
            json.remove("facilitiesMap");
            json.remove("recommend");
            json.remove("imgList");
            json.remove("innId");
            json.remove("bedLen");
            json.remove("floorNum");
            json.remove("bedNum");
            json.remove("roomTypeId");
            json.remove("roomInfo");
            list.set(i, json);
        }
        jsonObject.remove("page");
    }

    private String getInnInfo(Integer outerId, Integer pageNo) {
        long ts = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DAY_OF_YEAR, pageNo == null ? 0 : (pageNo - 1) * PAGE_SIZE);  //导出数据时导出未来60天的信息，不做分页，见exportXXX()
        String from = dateFormat.format(calendar.getTime());
        //因接口时间参数为闭区间，故要减1
        calendar.add(Calendar.DAY_OF_YEAR, pageNo == null ? EXPORT_SIZE - 1 : PAGE_SIZE - 1);  //导出数据时导出未来60天的信息，不做分页，见exportXXX()
        String to = dateFormat.format(calendar.getTime());

        String sig = MD5.getOMSSignature(ts);
        Map<String, String> params = new HashMap<>();
        params.put("accountId", outerId.toString());
        params.put("otaId", Constants.OMS_PROXY_PID.toString());
        params.put("from", from);
        params.put("to", to);
        params.put("signature", sig);
        params.put("timestamp", "" + ts);
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL), ApiURL.OMS_ROOM_TYPE, params);
        String result = new HttpUtil().get(url);
        return result;
    }

    @RequestMapping(value = "/{id}/otalink/modify", method = RequestMethod.POST)
    @ResponseBody
    public AjaxBase updateOtaLink(@PathVariable("id") Integer id, @RequestParam("otaLink") String otaLink) {
        try {
            proxyInnService.updateOTALink(id, otaLink);
            return new AjaxBase(Constants.HTTP_OK, "");
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, e.getMessage());
        }
    }

    /**
     * 导出已上架卖价客栈的房型房量
     */
    @RequestMapping("/export/sale/{type}")
    public void exportSale(HttpServletRequest req, HttpServletResponse response, @PathVariable("type") Integer type) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        String fileName = System.currentTimeMillis() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);

        try {
            List<ProxyInn> list = proxyInnService.findByStatus(1);

            OutputStream out = response.getOutputStream();
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            wb.setCompressTempFiles(true); //使用gzip压缩,减小空间占用
            Sheet sh = wb.createSheet("卖价房型房量");

            //设置星期
            Row rowHeader = sh.createRow(0);
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            for (int i = 0; i < EXPORT_SIZE; i++) {
                Cell cellHeader = rowHeader.createCell(i + 2);
                cellHeader.setCellValue("星期" + getDayOfWeek(dayOfWeek + i));
            }

            if (CollectionUtils.isNotEmpty(list)) {
                int rowCount = 1;
                //迭代客栈列表
                for (ProxyInn proxyInn : list) {
                    Integer outerId = null;
                    if (type == 1) {
                        outerId = proxyInn.getSaleOuterId();
                    } else {
                        outerId = proxyInn.getBaseOuterId();
                    }
                    if (outerId == null) {
                        continue;
                    }
                    String innInfo = getInnInfo(outerId, null);
                    JSONObject jsonObject = JSON.parseObject(innInfo);
                    if (jsonObject.getInteger("status").equals(400)) {
                        continue;
                    }
                    JSONArray jsonArray = jsonObject.getJSONArray("list");//房型列表
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Row row = sh.createRow(rowCount++);
                        Cell cellInnName = row.createCell(0, Cell.CELL_TYPE_STRING);//设置客栈名称
                        cellInnName.setCellValue(proxyInn.getInnName());
                        JSONObject json = (JSONObject) jsonArray.get(i);
                        String roomTypeName = json.getString("roomTypeName");
                        Cell cellRoomTypeName = row.createCell(1, Cell.CELL_TYPE_STRING);
                        cellRoomTypeName.setCellValue(roomTypeName);
                        JSONArray jsonArrDetails = json.getJSONArray("roomDetail");//价格列表
                        Cell cellRoomPrice;
                        for (int j = 0; j < jsonArrDetails.size(); j++) {
                            JSONObject roomDetail = (JSONObject) jsonArrDetails.get(j);
                            BigDecimal roomPrice = roomDetail.getBigDecimal("roomPrice");
                            cellRoomPrice = row.createCell(j + 2, Cell.CELL_TYPE_NUMERIC);
                            cellRoomPrice.setCellValue(roomPrice.doubleValue());
                        }
                    }
                    sh.createRow(rowCount++);
                }
            }

            wb.write(out);
            out.close();
            // dispose of temporary files backing this workbook on disk
            wb.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getDayOfWeek(int dayOfWeek) {
        int result = (dayOfWeek - 1) % 7;
        return result == 0 ? 7 : result;
    }

    /**
     * 导出已上架底价客栈的房型房量
     */
    @RequestMapping("/export/base")
    public void exportBase() {

    }

    @RequestMapping(value = "del")
    @ResponseBody
    public AjaxResult delete(@RequestParam("id") Integer id, @RequestParam("reason") String reason) {
        try {
            proxyInnService.delete(id, reason);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "");
    }

    @RequestMapping(value = "/update/inn/percentage")
    @ResponseBody
    public AjaxResult updateInnPercentage(Integer id, Float percentage) {
        try {
            proxyInnService.updateInnerPercentage(id, percentage);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "");
    }

    @RequestMapping(value = "/del_list")
    public String delRecord(Model model, @ModelAttribute Page<ProxyInnDelLog> page) {
        page.setPageSize(PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        if (!page.isOrderBySetted()) {
            page.setOrderBy("delTime");
            page.setOrder(Page.DESC);
        }
        page = proxyInnService.findDelList(page);
        model.addAttribute(page);
        model.addAttribute("currentBtn", "delList");
        return "proxysale/inn/del_list";
    }

    private class AjaxRelationChannel extends AjaxBase {
        private List<OtaInfoVO> otaInfoVOs;

        public AjaxRelationChannel(int status, String message) {
            super(status, message);
        }

        public AjaxRelationChannel(int status, List<OtaInfoVO> otaInfoVOs) {
            super(status);
            this.otaInfoVOs = otaInfoVOs;
        }

        public List<OtaInfoVO> getOtaInfoVOs() {
            return otaInfoVOs;
        }

        public void setOtaInfoVOs(List<OtaInfoVO> otaInfoVOs) {
            this.otaInfoVOs = otaInfoVOs;
        }

    }

    /**
     * 跳转到查看代销操作记录页面
     *
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(value = "/operateList")
    public String getOperateList(Model model, Page<FinanceOperationLog> page, String innName, String settlementTime, String startDate, String endDate, String operateType) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "operate");
        page = proxyInnService.findProxySaleOperationLogList(page, innName, startDate, endDate, operateType);
        model.addAttribute(page);
        model.addAttribute("innName", innName);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("operateType", operateType);
        return "proxysale/operate/operate_list";
    }

    /**
     * 获取所有代销房型
     *
     * @param innId
     * @return
     */
    @RequestMapping("/roomType")
    @ResponseBody
    public AjaxResult getRoomType(@RequestParam(value = "innId", required = true) Integer innId) {
        Map<String, List<ProxyRoomType>> map;
        try {
            JSONObject jsonObject = proxyInnRoomTypeService.getRoomTypeFromOMS(innId);
            map = proxyInnRoomTypeService.packRoomType(jsonObject);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, map);
    }


    /**
     * 下架房型
     *
     * @return
     */

    @RequestMapping("/down/roomType")
    @ResponseBody
    public AjaxResult downRoomType(String data) {
        try {
            Map<Integer, String> map = proxyInnRoomTypeService.parseRoomType(data);
            //下架房型
            proxyInnRoomTypeService.downRoomType(map);
            //记录日志
            proxyInnRoomTypeService.saveOperateLog(data);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "下架成功");
    }


}
