package com.project.service.operation;

import com.alibaba.fastjson.JSONObject;
import com.project.common.ApiURL;
import com.project.core.orm.Page;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.finance.FinanceInnSettlementInfoDao;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.operation.OperationActivityDao;
import com.project.dao.proxysale.ProxyInnDao;
import com.project.entity.finance.FinanceInnSettlementInfo;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.operation.OperationActivity;
import com.project.utils.*;
import com.project.utils.time.DateUtil;
import com.tomasky.msp.client.service.impl.MessageManageServiceImpl;
import com.tomasky.msp.client.support.MessageBuilder;
import com.tomasky.msp.enumeration.SmsChannel;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 2016/5/17.
 */
@Service("operationActivityService")
@Transactional
public class OperationActivityServiceImpl implements OperationActivityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationActivityServiceImpl.class);
    @Resource
    private OperationActivityDao operationActivityDao;
    @Resource
    private FinanceOperationLogDao financeOperationLogDao;
    @Resource
    private ProxyInnDao proxyInnDao;
    @Resource
    private FinanceInnSettlementInfoDao financeInnSettlementInfoDao;
    @Resource
    private FinanceHelper financeHelper;

    @Override
    public void save(OperationActivity operationActivity, MultipartFile file, String operate, Integer id, HttpSession session, String publishTime) {
        String fileName = file.getOriginalFilename();

        String ftpFileName = ApiURL.IMG_FTP_FOLDER + "/" + String.valueOf(System.currentTimeMillis()) + "/" + fileName;
        String imgUrl;
        String url;
        if (file.getSize() > 0) {
            imgUrl = uploadImg(ftpFileName, file);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(imgUrl);
            JSONObject object = (JSONObject) jsonObject.get("data");
            url = (String) object.get("downloadUrl");
        } else {
            if (operate.equals("edit")) {
                url = operationActivity.getCoverPicture();
            } else {
                url = "";
            }
        }

        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
        financeOperationLog.setOperateType("117");
        financeOperationLog.setOperateObject(operationActivity.getActivityName());
        financeOperationLog.setOperateTime(new Date());
        operationActivity.setCoverPicture(url);

        if (null == operationActivity.getRecommend()) {
            operationActivity.setRecommend(false);
        }
        operationActivity.setStatus("1");
        if (null != operate && operate.equals("add")) {
            financeOperationLog.setOperateContent("添加活动");
            operationActivity.setPublishTime(new Date());
            operationActivityDao.save(operationActivity);
        }
        if (null != operate && operate.equals("edit")) {
            financeOperationLog.setOperateContent("修改活动");
            operationActivity.setPublishTime(DateUtil.parse(publishTime, "yyyy-MM-dd HH:mm:ss"));
            operationActivity.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
            operationActivityDao.updateOperationActivity(operationActivity);
        }
        financeOperationLogDao.save(financeOperationLog);
    }

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    public String uploadImg(String ftpFileName, MultipartFile file) {
        ftpFileName = handerName(ftpFileName);
        try {
            CommonsMultipartFile cf = (CommonsMultipartFile) file;
            DiskFileItem fi = (DiskFileItem) cf.getFileItem();
            File f = fi.getStoreLocation();
            StringBody stringBody = new StringBody(ftpFileName);
            FileBody fileBody = new FileBody(f);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            HttpEntity entity = builder.addPart("imgFile", fileBody).addPart("fileId", stringBody).build();
            return request(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<OperationActivity> getActivities(Page<OperationActivity> page, String activityName) {

        return operationActivityDao.getActivities(page, activityName);
    }

    @Override
    public List<OperationActivity> findActivities() {
        return operationActivityDao.findAllActivities();
    }

    @Override
    public List<Map<String, Object>> findStatus(Integer innId) {
        return operationActivityDao.findStatus(innId);
    }

    @Override
    public List<Map<String, Object>> packActivity(Integer innId, Integer pageNo) {
        try {
            if (null == pageNo) {
                pageNo = 1;
            }
            if (null == innId) {
                throw new RuntimeException("客栈Id传入为空");
            }
            Page<OperationActivity> page = new Page<>();
            page.setPageNo(pageNo);
            page.setPageSize(10);
            page = getActivities(page, null);

            List<Map<String, Object>> listMaps = new ArrayList<>();
            List<OperationActivity> result = (List<OperationActivity>) page.getResult();
            if (CollectionsUtil.isNotEmpty(result)) {
                List<Map<String, Object>> status = findStatus(innId);
                if (CollectionsUtil.isNotEmpty(status)) {
                    Map<Integer, List<String>> listMap = new LinkedHashMap<>();
                    //封装状态码到Map
                    for (Map<String, Object> map1 : status) {
                        List<String> list = new ArrayList<>();
                        Integer activityId = (Integer) map1.get("activity");
                        String entryStatus = (String) map1.get("status");

                        list.add(entryStatus);
                        listMap.put(activityId, list);
                    }
                    //遍历活动对象，封装接口响应数据
                    for (OperationActivity operationActivity : result) {
                        Integer id = operationActivity.getId();
                        List<String> list = listMap.get(id);
                        Map<String, Object> map = new HashMap<>();
                        if (CollectionsUtil.isNotEmpty(list)) {
                            map.put("enterStatus", list.get(0));

                        } else {
                            map.put("enterStatus", 4);

                        }
                        map.put("activityId", id);
                        map.put("activityName", operationActivity.getActivityName());
                        map.put("activityTime", operationActivity.getStartTime() + " ~ " + operationActivity.getEndTime());
                        map.put("content", operationActivity.getContent());
                        map.put("days", getDateLine(operationActivity.getDateLine()));
                        map.put("recommend", operationActivity.getRecommend());
                        map.put("imgUrl", operationActivity.getCoverPicture());
                        map.put("activityStatus", operationActivity.getStatus());
                        map.put("requireStr", operationActivity.getRequire());
                        listMaps.add(map);
                    }
                } else {
                    for (OperationActivity operationActivity : result) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("activityId", operationActivity.getId());
                        map.put("activityName", operationActivity.getActivityName());
                        map.put("activityTime", operationActivity.getStartTime() + " ~ " + operationActivity.getEndTime());
                        map.put("content", operationActivity.getContent());
                        map.put("days", getDateLine(operationActivity.getDateLine()));
                        map.put("enterStatus", 4);
                        map.put("imgUrl", operationActivity.getCoverPicture());
                        map.put("recommend", operationActivity.getRecommend());
                        map.put("activityStatus", operationActivity.getStatus());
                        map.put("requireStr", operationActivity.getRequire());
                        listMaps.add(map);
                    }
                }
            } else {
                listMaps = null;
            }

            return listMaps;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void finishActivity(Integer id) {
        operationActivityDao.updateActivity(id);
        /*operationActivityDao.updateActivityInn(id);*/
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setOperateContent("结束活动");
        financeOperationLog.setOperateType("117");
        financeOperationLog.setOperateObject(operationActivityDao.get(id).getActivityName());
        financeOperationLog.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
        financeOperationLog.setOperateTime(new Date());
        financeOperationLogDao.save(financeOperationLog);
    }

    @Override
    public void finishActivityJob(Integer id) {
        operationActivityDao.updateActivity(id);
    }

    /**
     * 计算截止日期
     *
     * @param date
     * @return
     */
    public Long getDateLine(String date) {
        Date date1 = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtil.isNotNull(date)) {
                Date date2 = simpleDateFormat.parse(date);
                Long day;
                if ((date2.getTime() - date1.getTime()) <= 0) {
                    day = 0L;
                    return day;
                }
                day = (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
                if (((date2.getTime() - date1.getTime()) % (24 * 60 * 60 * 1000)) > 0) {
                    day += 1;
                }
                return day;
            }

        } catch (Exception e) {
            throw new RuntimeException("截止日期转换错误");
        }
        return null;
    }

    @Override
    public Page<Map<String, Object>> getInnWithActivity(Page<Map<String, Object>> page, Integer activityId, String innName) {
        return operationActivityDao.getInnWithActivity(page, activityId, innName);
    }

    @Override
    public List<Map<String, Object>> getInnWithActivity(Integer activityId) {
        return operationActivityDao.getInnWithActivity(activityId);
    }

    @Override
    public void updateInnStatus(Integer activityId, Integer innId, String status) {
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setOperateObject("PMS客栈ID:" + innId);
        financeOperationLog.setOperateTime(new Date());
        financeOperationLog.setOperateType("117");
        financeOperationLog.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlementInfoDao.financeInnSettlementInfoWithId(innId);
        if (null == financeInnSettlementInfo) {
            financeInnSettlementInfo = financeHelper.getInnInfo(innId);
        }
        String innName = financeInnSettlementInfo.getInnName();
        String activityName = operationActivityDao.get(activityId).getActivityName();
        List<String> receivers = new ArrayList<>();
        List<String> contacts = new ArrayList<>();
        String wxOpenId = financeInnSettlementInfo.getWxOpenId();
        String contact1 = financeInnSettlementInfo.getContact1();
        String contact2 = financeInnSettlementInfo.getContact2();
        if (StringUtil.isNotNull(wxOpenId)) {
            receivers.add(wxOpenId);
        }
        if (StringUtil.isNotNull(contact1)) {
            contacts.add(contact1);
        }
        if (StringUtil.isNotNull(contact2)) {
            contacts.add(contact2);
        }

        if (null == activityId || null == innId) {
            throw new RuntimeException("未传入客栈或者活动唯一标示");
        }
        if (null != status && status.equals("2")) {
            financeOperationLog.setOperateContent("同意客栈参加活动" + activityName);
            operationActivityDao.updateInnStatus(activityId, innId, "2");
            if (CollectionsUtil.isNotEmpty(receivers)) {
                LOGGER.info("同意请求微信准备推送");
                try {
                    sendWeChat(innName, activityName, "审核通过", receivers);
                } catch (Exception e) {
                    LOGGER.error("同意请求微信推送失败");
                    e.printStackTrace();
                }
            }
            if (CollectionsUtil.isNotEmpty(contacts)) {
                LOGGER.info("同意请求短信准备推送");
                try {
                    sendSms(innName, activityName, "已成功报名", contacts);
                } catch (Exception e) {
                    LOGGER.error("同意请求短信推送失败");
                    e.printStackTrace();
                }
            }
        }

        if (null != status && status.equals("3")) {
            financeOperationLog.setOperateContent("拒绝客栈参加活动" + activityName);
            operationActivityDao.updateInnStatus(activityId, innId, "3");
            if (CollectionsUtil.isNotEmpty(receivers)) {
                LOGGER.info("拒绝请求微信准备推送");
                try {
                    sendWeChat(innName, activityName, "未通过", receivers);
                } catch (Exception e) {
                    LOGGER.error("拒绝请求微信推送失败");
                    e.printStackTrace();
                }
            }
            if (CollectionsUtil.isNotEmpty(contacts)) {
                LOGGER.info("拒绝请求短信准备推送");
                try {
                    sendSms(innName, activityName, "未能成功报名", contacts);
                } catch (Exception e) {
                    LOGGER.error("拒绝请求短信推送失败");
                    e.printStackTrace();
                }
            }
        }
        financeOperationLogDao.save(financeOperationLog);
    }

    @Override
    public void updateInnStatusAll(Integer activityId) {


        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setOperateContent("一键同意活动" + operationActivityDao.get(activityId).getActivityName());
        financeOperationLog.setOperateObject(operationActivityDao.get(activityId).getActivityName());
        financeOperationLog.setOperateType("117");
        financeOperationLog.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
        financeOperationLog.setOperateTime(new Date());
        //记录操作日志
        financeOperationLogDao.save(financeOperationLog);
        List<Map<String, Object>> innIdsWithActivity = new ArrayList<>();
        innIdsWithActivity = operationActivityDao.findInnIdsWithAvtivity(activityId);

        //短信微信推送
        if (CollectionsUtil.isNotEmpty(innIdsWithActivity)) {

            for (Map<String, Object> map : innIdsWithActivity) {
                Integer innId = (Integer) map.get("id");

                FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlementInfoDao.financeInnSettlementInfoWithId(innId);
                if (null == financeInnSettlementInfo) {
                    financeInnSettlementInfo = financeHelper.getInnInfo(innId);
                }
                String innName = financeInnSettlementInfo.getInnName();
                String activityName = operationActivityDao.get(activityId).getActivityName();
                List<String> receivers = new ArrayList<>();
                List<String> contacts = new ArrayList<>();
                String wxOpenId = financeInnSettlementInfo.getWxOpenId();
                String contact1 = financeInnSettlementInfo.getContact1();
                String contact2 = financeInnSettlementInfo.getContact2();

                if (StringUtil.isNotNull(wxOpenId)) {
                    receivers.add(wxOpenId);
                }
                if (StringUtil.isNotNull(contact1)) {
                    contacts.add(contact1);
                }
                if (StringUtil.isNotNull(contact2)) {
                    contacts.add(contact2);
                }
                if (CollectionsUtil.isNotEmpty(receivers)) {
                    LOGGER.info("一键同意微信准备推送");
                    try {
                        sendWeChat(innName, activityName, "审核通过", receivers);
                    } catch (Exception e) {
                        LOGGER.error(innName + "一键同意微信推送失败");
                        e.printStackTrace();
                    }
                }
                if (CollectionsUtil.isNotEmpty(contacts)) {
                    LOGGER.info("一键同意短信准备推送");

                    try {
                        sendSms(innName, activityName, "已成功报名", contacts);
                    } catch (Exception e) {
                        LOGGER.error(innName + "一键同意短信推送失败");
                        e.printStackTrace();
                    }
                }
            }
            operationActivityDao.updateInnStatusAll(activityId);
        } else {
            throw new RuntimeException("没有可以同意的客栈");
        }
    }

    @Override
    public Map<String, Object> statisticInn(Integer activityId) {

        return operationActivityDao.statisticInn(activityId);
    }

    public void exportExecl(Integer activityId, HttpServletRequest request) {
        List<Map<String, Object>> innWithActivity = operationActivityDao.getInnWithActivity(activityId);
        if (CollectionsUtil.isEmpty(innWithActivity)) {
            throw new RuntimeException("没有获取到此活动的参与客栈信息");
        }
        OperationActivity operationActivity = operationActivityDao.get(activityId);
        String name = operationActivity.getActivityName();
        if (null != name && name.length() > 10) {
            name = name.substring(0, 10);
        }
        FileOutputStream fileOutputStream = null;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            fillData(workbook, normalCellStyle, boldCellStyle, innWithActivity);
            String fileName = name + ".xls";
            String realPath = request.getSession().getServletContext().getRealPath("/") + "download";
            File file = new File(realPath + "/" + fileName);
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("导出活动客栈表失败");
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                throw new RuntimeException("表格导出时出错!", e);
            }
        }
    }


    /**
     * 填充execl数据
     *
     * @param workbook
     * @param normalCellStyle
     * @param boldCellStyle
     */
    public void fillData(HSSFWorkbook workbook, HSSFCellStyle normalCellStyle, HSSFCellStyle boldCellStyle, List<Map<String, Object>> innWithActivity) {
        HSSFSheet totalSheet = workbook.createSheet("活动客栈列表");
        totalSheet.autoSizeColumn(8, true);
        totalSheet.setDefaultColumnWidth(18);
        buildSheetTitle(totalSheet);
        ExcelUtil.renderedExcel(totalSheet, boldCellStyle, 0);
        buildSheetData(totalSheet, innWithActivity);
        ExcelUtil.renderedExcel(totalSheet, normalCellStyle, 1);
    }

    /**
     * 构建表头
     *
     * @param totalSheet
     */
    public void buildSheetTitle(HSSFSheet totalSheet) {
        HSSFRow row = totalSheet.createRow(0);
        HSSFCellUtil.createCell(row, 0, "区域");
        HSSFCellUtil.createCell(row, 1, "目的地");
        HSSFCellUtil.createCell(row, 2, "客栈名称");
        HSSFCellUtil.createCell(row, 3, "pms客栈id");
        HSSFCellUtil.createCell(row, 4, "状态");

    }

    /**
     * 填充execl数据
     *
     * @param totalSheet
     * @param innWithActivity
     */
    public void buildSheetData(HSSFSheet totalSheet, List<Map<String, Object>> innWithActivity) {
        int i = 1;
        for (Map<String, Object> map : innWithActivity) {
            HSSFRow row = totalSheet.createRow(i);
            HSSFCellUtil.createCell(row, 0, (String) map.get("area"));
            HSSFCellUtil.createCell(row, 1, (String) map.get("region"));
            HSSFCellUtil.createCell(row, 2, (String) map.get("innname"));
            HSSFCellUtil.createCell(row, 3, map.get("pmsid") + "");
            String status = (String) map.get("status");
            if (null != status) {
                if (status.equals("1")) {
                    HSSFCellUtil.createCell(row, 4, "未审核");
                }
                if (status.equals("2")) {
                    HSSFCellUtil.createCell(row, 4, "通过");
                }
                if (status.equals("3")) {
                    HSSFCellUtil.createCell(row, 4, "拒绝");
                }
            }
            i++;
        }
    }

    private String request(HttpEntity entity) {
        String url = SystemConfig.PROPERTIES.get(SystemConfig.IMG_URL) + ApiURL.IMG_UPLOAD;
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpclient = httpClientBuilder.build();
        HttpPost post = new HttpPost(url);
        String result = null;
        try {
            post.setEntity(entity);
            HttpResponse response = httpclient.execute(post);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                HttpEntity entitys = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entitys);
                    LOGGER.info("请求响应：" + result);
                    return result;
                } else {
                    LOGGER.error("响应出错，返回数据为NULL");
                    throw new RuntimeException("响应出错，返回数据为NULL");
                }
            } else {
                LOGGER.error("响应出错" + response.getStatusLine().getStatusCode());
                throw new RuntimeException("响应出错" + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.error("上传请求出错", e);
            throw new RuntimeException("上传请求出错" + e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                new RuntimeException(e.getMessage());
            }
        }
    }

    private static String handerName(String fileName) {
        if (fileName.startsWith("/")) {
            fileName = fileName.replaceFirst("/", "");
        }
        return fileName;
    }

    @Override
    public void applicationActivity(Integer activityId, Integer innId) {
        Map<String, Object> activityInn = operationActivityDao.getActivityInn(activityId, innId);
        if (null == activityInn || activityInn.size() == 0) {
            operationActivityDao.saveActivityInn(activityId, innId);
        } else {
            operationActivityDao.updateActivityInn(activityId, innId);
        }
    }

    @Override
    public List<Integer> getPageNum() {
        Map<String, Object> totalRecords = operationActivityDao.getTotalRecords();
        List<Integer> list = new ArrayList<>();
        BigInteger bigInteger = (BigInteger) totalRecords.get("count");
        if (null == bigInteger) {
            bigInteger = new BigInteger("0");
        }
        int i = bigInteger.intValue() % 10;
        if (i > 0) {
            list.add((bigInteger.intValue() / 10) + 1);
        } else {
            list.add(bigInteger.intValue() / 10);
        }
        list.add(bigInteger.intValue());
        return list;
    }

    /**
     * 发送审核结果微信
     *
     * @param innName
     * @param activityName
     * @param auditResult
     * @param receivers
     */
    private void sendWeChat(String innName, String activityName, String auditResult, List<String> receivers) {
        MessageManageServiceImpl messageManageService = new MessageManageServiceImpl();
        messageManageService.sendMessage(new MessageBuilder().buildactivityNotifyWechatMessage("活动审核结果通知", activityName, auditResult, innName + "客栈," + "您申请的" + activityName + "活动已审核。", receivers));
    }

    /**
     * 发送审核短信
     *
     * @param innName
     * @param auditResult
     */
    private void sendSms(String innName, String activityName, String auditResult, List<String> contacts) {
        String message = innName + "客栈," + "您申请的" + activityName + "活动" + auditResult + "。";
        MessageManageServiceImpl messageManageService = new MessageManageServiceImpl();
        messageManageService.sendMessage(MessageBuilder.buildSmsMessage(contacts, SmsChannel.SEND_TYPE_VIP, message));
    }

    @Override
    public OperationActivity getActivityById(Integer id) {
        return operationActivityDao.get(id);
    }
}
