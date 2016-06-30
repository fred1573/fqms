package com.project.service.finance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.core.orm.Page;
import com.project.dao.finance.FinanceInnChannelSettlementDao;
import com.project.dao.finance.FinanceInnSettlementDao;
import com.project.dao.finance.FinanceOrderDao;
import com.project.entity.finance.FinanceInnChannelSettlement;
import com.project.entity.finance.FinanceInnSettlement;
import com.project.entity.finance.FinanceInnSettlementInfo;
import com.project.entity.finance.FinanceParentOrder;
import com.project.utils.CollectionsUtil;
import com.project.utils.ExcelExportUtil;
import com.project.utils.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/1/13.
 */
@Service("financeInnChannelSettlementService")
@Transactional
public class FinanceInnChannelSettlementServiceImpl implements FinanceInnChannelSettlementService {
    @Resource
    private FinanceInnChannelSettlementDao financeInnChannelSettlementDao;
    @Resource
    private FinanceOrderDao financeOrderDao;
    @Resource
    private FinanceInnSettlementDao financeInnSettlementDao;

    @Override
    public Map<String, Object> findInnChannelSettlementStatus(String settlementTime, Integer channelId) {

        if (StringUtils.isNotBlank(settlementTime) && channelId != null) {
            return financeInnChannelSettlementDao.findInnChannelSettlementStatus(settlementTime, channelId).get(0);
        } else {
            if (channelId == null) {
                throw new RuntimeException("统计渠道ID不能为空");
            }
            if (StringUtils.isBlank(settlementTime)) {
                throw new RuntimeException("统计账期不能为空");
            }
            throw new RuntimeException("统计" + channelId + "渠道相应数据出现错误");
        }
    }

    @Override
    public void fillRealPay(String settlementTime, Integer innId) {
        if (StringUtils.isNotBlank(settlementTime) && innId != null) {
            financeInnChannelSettlementDao.fillInnChannelRealPay(settlementTime, innId);
        } else {
            if (innId == null) {
                throw new RuntimeException("填充客栈ID不能为空");
            }
            if (StringUtils.isBlank(settlementTime)) {
                throw new RuntimeException("填充账期不能为空");
            }
            throw new RuntimeException("填充" + innId + "客栈相应数据出现错误");
        }
    }

    @Override
    public void updateRealPay(String jsonData) {
        if (StringUtils.isBlank(jsonData)) {
            throw new RuntimeException("数据异常");
        }
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Integer id = jsonObject.getInteger("id");
        if (id == null) {
            throw new RuntimeException("ID不能为空");
        }
        BigDecimal realPayment = jsonObject.getBigDecimal("realPayment");
        if (realPayment == null) {
            throw new RuntimeException("实付金额不能为空");
        }
        String paymentRemark = jsonObject.getString("paymentRemark");
        if (id != null) {
            boolean isMatch = false;
            FinanceInnChannelSettlement financeInnChannelSettlement = financeInnChannelSettlementDao.findFinanceInnChannelSettlementById(id);
            BigDecimal pay = financeInnChannelSettlement.getInnPayment();
            BigDecimal refund = financeInnChannelSettlement.getRefundAmount();
            BigDecimal fqReplenishment = financeInnChannelSettlement.getFqReplenishment();
            if (null == pay) {
                pay = BigDecimal.ZERO;
            }
            if (null == refund) {
                refund = BigDecimal.ZERO;
            }
            if (null == fqReplenishment) {
                fqReplenishment = BigDecimal.ZERO;
            }
            BigDecimal specialAmount = (pay.add(refund)).subtract(fqReplenishment);
            if ((financeInnChannelSettlement.getInnSettlementAmount().subtract(specialAmount)).compareTo(realPayment) == 0) {
                isMatch = true;
            }
            //更新客栈结算表
            Integer innId = financeInnChannelSettlement.getFinanceInnSettlementInfo().getId();
            String settlementTime = financeInnChannelSettlement.getSettlementTime();
            financeInnChannelSettlementDao.updateRealPay(realPayment, paymentRemark, id, isMatch);
            FinanceInnSettlement financeInnSettlement = financeInnSettlementDao.selectFinanceInnSettlement(innId, settlementTime);
            BigDecimal afterAmout;
            if (financeInnSettlement.getIsArrears().equals("1")) {
                afterAmout = financeInnSettlement.getAfterArrearsAmount();
            }
            afterAmout = financeInnSettlement.getAfterPaymentAmount();
            if (null == afterAmout) {
                afterAmout = BigDecimal.ONE.ZERO;
            }
            Map<String, Object> map = financeInnSettlementDao.selectPayment(innId, settlementTime);
            BigDecimal innRealPayment = (BigDecimal) map.get("pay");
            if (null == innRealPayment) {
                innRealPayment = BigDecimal.ZERO;
            }
            if (afterAmout.compareTo(innRealPayment) == 0) {
                financeInnSettlement.setIsMatch(true);
            } else {
                financeInnSettlement.setIsMatch(false);
            }
            financeInnSettlementDao.save(financeInnSettlement);
        } else {
            throw new RuntimeException("请选择修改客栈");
        }
    }

    @Override
    public void exportInnOrder(HttpServletResponse response, Integer channelId, String settlementTime) {
        List<FinanceInnChannelSettlement> financeInnChannelSettlementList = new ArrayList<>();/*financeInnChannelSettlementDao.selectFinanceInnChannelSettlementBySettlementTime(settlementTime, channelId);*/
        List<List<FinanceParentOrder>> financeParentOrderList = new ArrayList<>();
        List<Integer> innIds = financeOrderDao.getSettlementInnId(settlementTime, channelId);
        if (!CollectionsUtil.isEmpty(innIds)) {
            for (Integer inn : innIds) {
                // 获取客栈结算数据
                FinanceInnChannelSettlement financeInnChannelSettlement = financeInnChannelSettlementDao.selectFinanceInnChannelSettlement(inn, settlementTime, channelId);
                if (financeInnChannelSettlement != null) {
                    financeInnChannelSettlementList.add(financeInnChannelSettlement);
                }
                // 获得结算订单集合
                List<FinanceParentOrder> parentOrderList = financeOrderDao.findFinanceParentOrderWithChannelId(channelId, settlementTime, inn);
                if (CollectionsUtil.isNotEmpty(parentOrderList)) {
                    financeParentOrderList.add(parentOrderList);
                }
            }
        }
        createFinanceExcel(response, financeInnChannelSettlementList, financeParentOrderList);
    }


    @Override
    public void batchExportInnOrder(HttpServletRequest request, Integer channelId, String settlementTime, String channelName) {
        List<FinanceInnChannelSettlement> financeInnChannelSettlementList = new ArrayList<>();/*financeInnChannelSettlementDao.selectFinanceInnChannelSettlementBySettlementTime(settlementTime, channelId);*/
        List<List<FinanceParentOrder>> financeParentOrderList = new ArrayList<>();
        List<Integer> innIds = financeOrderDao.getSettlementInnId(settlementTime, channelId);
        if (!CollectionsUtil.isEmpty(innIds)) {
            for (Integer inn : innIds) {
                // 获取客栈结算数据
                FinanceInnChannelSettlement financeInnChannelSettlement = financeInnChannelSettlementDao.selectFinanceInnChannelSettlement(inn, settlementTime, channelId);
                if (financeInnChannelSettlement != null) {
                    financeInnChannelSettlementList.add(financeInnChannelSettlement);
                }
                // 获得结算订单集合
                List<FinanceParentOrder> parentOrderList = financeOrderDao.findFinanceParentOrderWithChannelId(channelId, settlementTime, inn);
                if (CollectionsUtil.isNotEmpty(parentOrderList)) {
                    financeParentOrderList.add(parentOrderList);
                }
            }
        }
        batchCreateFinanceExcel(request, financeInnChannelSettlementList, financeParentOrderList, channelName);
    }


    @Override
    public void createFinanceExcel(HttpServletResponse response, List<FinanceInnChannelSettlement> financeInnChannelSettlementList, List<List<FinanceParentOrder>> financeParentOrderList) {

        if (CollectionsUtil.isEmpty(financeInnChannelSettlementList)) {
            throw new RuntimeException("当前结算月份没有客栈结算数据");
        }
        if (CollectionsUtil.isEmpty(financeParentOrderList)) {
            throw new RuntimeException("当月结算月份没有客栈结算的订单数据");
        }
        OutputStream outputStream = null;
        try {
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            // 第一个sheet是总表，统计客栈全部数据
            HSSFSheet totalSheet = workbook.createSheet("总表");
            // 构建总表
            fillTotalSheetData(totalSheet, financeInnChannelSettlementList, normalCellStyle, boldCellStyle);
            for (int i = 0; i < financeInnChannelSettlementList.size(); i++) {
                FinanceInnChannelSettlement financeInnChannelSettlement = financeInnChannelSettlementList.get(i);
                FinanceInnSettlementInfo financeInnSettlementInfo = financeInnChannelSettlement.getFinanceInnSettlementInfo();
                String sheetName = "";
                if (financeInnSettlementInfo != null) {
                    String innName = financeInnSettlementInfo.getInnName();
                    sheetName = ExcelUtil.getSheetNameByInnName(innName);
                }
                HSSFSheet sheet = workbook.createSheet(sheetName + "(" + financeInnChannelSettlement.getId() + ")");
                sheet.autoSizeColumn(8, true);
                sheet.setDefaultColumnWidth(18);

                // 创建表头
                createExcelTitle(sheet, financeInnChannelSettlementList.get(i), boldCellStyle);
                List<FinanceParentOrder> parentOrderList = financeParentOrderList.get(i);
                fillExcelData(sheet, parentOrderList, normalCellStyle, financeInnChannelSettlementList.get(i));
            }

            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            // 拼接Excel文件名称
            String fileName = getFinanceExcelName(financeInnChannelSettlementList);
            // 设置文件名
            response.addHeader("Content-Disposition", new String(("attachment; filename=" + fileName + ".xls").getBytes("GBK"), "ISO-8859-1"));
            outputStream = response.getOutputStream();
            // 导出Excel
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("表格导出时出错!", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                throw new RuntimeException("表格导出时出错!", e);
            }
        }
    }


    @Override
    public void batchCreateFinanceExcel(HttpServletRequest request, List<FinanceInnChannelSettlement> financeInnChannelSettlementList, List<List<FinanceParentOrder>> financeParentOrderList, String channelName) {

        if (CollectionsUtil.isEmpty(financeInnChannelSettlementList)) {
            throw new RuntimeException("当前结算月份没有客栈结算数据");
        }
        if (CollectionsUtil.isEmpty(financeParentOrderList)) {
            throw new RuntimeException("当月结算月份没有客栈结算的订单数据");
        }
        FileOutputStream fileOutputStream = null;
        try {
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            // 第一个sheet是总表，统计客栈全部数据
            HSSFSheet totalSheet = workbook.createSheet("总表");
            // 构建总表
            fillTotalSheetData(totalSheet, financeInnChannelSettlementList, normalCellStyle, boldCellStyle);
            for (int i = 0; i < financeInnChannelSettlementList.size(); i++) {
                HSSFSheet sheet = workbook.createSheet(ExcelUtil.getSheetNameByInnName(financeInnChannelSettlementList.get(i).getFinanceInnSettlementInfo().getInnName()) + "(" + financeInnChannelSettlementList.get(i).getId() + ")");
                sheet.autoSizeColumn(8, true);
                sheet.setDefaultColumnWidth(18);

                // 创建表头
                createExcelTitle(sheet, financeInnChannelSettlementList.get(i), boldCellStyle);
                List<FinanceParentOrder> parentOrderList = financeParentOrderList.get(i);
                fillExcelData(sheet, parentOrderList, normalCellStyle, financeInnChannelSettlementList.get(i));
            }

            // 拼接Excel文件名称
            String fileName = getFinanceExcelName(financeInnChannelSettlementList, channelName) + "V" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls";
            fileName = new String(fileName.getBytes("utf-8"), "utf-8");
            String realPath = request.getSession().getServletContext().getRealPath("/") + "download";
            File file = new File(realPath);
            fileOutputStream = new FileOutputStream(file + "/" + fileName);

            // 导出Excel
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            throw new RuntimeException("表格导出时出错!", e);
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
     * 按条件查询客栈
     *
     * @param page
     * @param settlementTime
     * @param channelId
     * @param innName
     * @param isMatch
     * @return
     */
    @Override
    public Page<FinanceInnChannelSettlement> financeInnChannelSettlementWithRequire(Page<FinanceInnChannelSettlement> page, String settlementTime, Integer channelId, String innName, Boolean isMatch) {
        return financeInnChannelSettlementDao.financeInnChannelSettlementWithRequire(page, settlementTime, channelId, innName, isMatch);

    }

    /**
     * 创建出账核对的总表
     *
     * @param totalSheet               总表的sheet
     * @param financeInnSettlementList 客栈结算对象集合
     * @param normalCellStyle          普通单元格样式
     * @param boldCellStyle            加粗单元格样式
     */
    private void fillTotalSheetData(HSSFSheet totalSheet, List<FinanceInnChannelSettlement> financeInnSettlementList, HSSFCellStyle normalCellStyle, HSSFCellStyle boldCellStyle) {
        FinanceInnChannelSettlement financeInnChannelSettlement = financeInnSettlementList.get(0);
        totalSheet.autoSizeColumn(8, true);
        totalSheet.setDefaultColumnWidth(18);
        // 创建表头
        buildTotalSheetTitle(totalSheet, financeInnChannelSettlement);
        renderedExcel(totalSheet, boldCellStyle, 0);
        buildTotalSheetData(totalSheet, financeInnSettlementList);
        renderedExcel(totalSheet, normalCellStyle, 2);
    }

    /**
     * 填充渠道出账核算总表表头
     *
     * @param totalSheet
     * @param financeInnChannelSettlement
     */
    private void buildTotalSheetTitle(HSSFSheet totalSheet, FinanceInnChannelSettlement financeInnChannelSettlement) {
        List<Map<String, Object>> dataMapList = financeInnChannelSettlementDao.selectFinanceInnChannelSettlementCount(financeInnChannelSettlement.getSettlementTime(), financeInnChannelSettlement.getChannelId());
        if (CollectionsUtil.isEmpty(dataMapList)) {
            throw new RuntimeException("本月没有客栈的结算记录");
        }
        Map<String, Object> dataMap = dataMapList.get(0);
        int innCount = Integer.parseInt(String.valueOf(dataMap.get("inncount")));
        int orders = Integer.parseInt(String.valueOf(dataMap.get("orders")));
        BigDecimal total = (BigDecimal) (dataMap.get("total"));
        BigDecimal channels = (BigDecimal) dataMap.get("channels");
        BigDecimal fqs = (BigDecimal) dataMap.get("fqs");
        BigDecimal inns = (BigDecimal) dataMap.get("inns");
        BigDecimal pay = (BigDecimal) dataMap.get("pay");
        BigDecimal channelAmount = (BigDecimal) dataMap.get("channelamount");

        // 第一行展示合计内容
        HSSFRow row = totalSheet.createRow(0);
        HSSFCell cell = row.createCell(1);

        cell.setCellValue("合计:      " + innCount);
        cell = row.createCell(3);
        cell.setCellValue(orders);
        cell = row.createCell(4);
        cell.setCellValue(String.valueOf(total));
        cell = row.createCell(5);
        cell.setCellValue(String.valueOf(channelAmount));
        cell = row.createCell(6);
        cell.setCellValue(String.valueOf(channels));
        cell = row.createCell(7);
        cell.setCellValue(String.valueOf(fqs));
        cell = row.createCell(8);
        cell.setCellValue(String.valueOf(inns));
        cell = row.createCell(9);
        cell.setCellValue(String.valueOf(pay));
        // 第二行展示表头
        row = totalSheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("城市");
        cell = row.createCell(1);
        cell.setCellValue("客栈名称");
        cell = row.createCell(2);
        cell.setCellValue("收款信息(银行卡)");
        cell = row.createCell(3);
        cell.setCellValue("订单总数(个)");
        cell = row.createCell(4);
        cell.setCellValue("客栈订单总金额");
        cell = row.createCell(5);
        cell.setCellValue("分销商订单总金额");
        cell = row.createCell(6);
        cell.setCellValue("分销商结算金额");
        cell = row.createCell(7);
        cell.setCellValue("番茄收入金额");
        cell = row.createCell(8);
        cell.setCellValue("客栈结算金额");
        cell = row.createCell(9);
        cell.setCellValue("实付金额");
        cell = row.createCell(10);
        cell.setCellValue("联系电话");
        cell = row.createCell(11);
        cell.setCellValue("备注");

    }

    /**
     * 按照指定样式渲染Excel单元格
     *
     * @param sheet       表格对象
     * @param cellStyle   单元格样式对象
     * @param beginRowNum 开始渲染的行数
     */
    private void renderedExcel(HSSFSheet sheet, HSSFCellStyle cellStyle, int beginRowNum) {
        for (int i = beginRowNum; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow hr = sheet.getRow(i);
            for (int k = 0; k < hr.getLastCellNum(); k++) {
                HSSFCell hc = hr.getCell(k);
                if (hc != null) {
                    hc.setCellStyle(cellStyle);
                }
            }
        }
    }

    /**
     * 填充出账核算客栈结算总表数据
     *
     * @param totalSheet
     * @param financeInnChannelSettlementList
     */
    private void buildTotalSheetData(HSSFSheet totalSheet, List<FinanceInnChannelSettlement> financeInnChannelSettlementList) {
        for (int i = 0; i < financeInnChannelSettlementList.size(); i++) {
            FinanceInnChannelSettlement financeInnChannelSettlement = financeInnChannelSettlementList.get(i);
            HSSFRow row = totalSheet.createRow(i + 2);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(financeInnChannelSettlement.getFinanceInnSettlementInfo().getRegionName());
            cell = row.createCell(1);
            cell.setCellValue(financeInnChannelSettlement.getFinanceInnSettlementInfo().getInnName());
            financeInnChannelSettlement.getFinanceInnSettlementInfo().getInnName();
            cell = row.createCell(2);
            cell.setCellValue(buildBankInfo(financeInnChannelSettlement));
            cell = row.createCell(3);
            cell.setCellValue(financeInnChannelSettlement.getTotalOrder());
            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getTotalAmount()));
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getChannelAmount()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getChannelSettlementAmount()));
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getFqSettlementAmount()));
            cell = row.createCell(8);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getInnSettlementAmount()));
            cell = row.createCell(9);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getRealPayment()));
            cell = row.createCell(10);
            cell.setCellValue(financeInnChannelSettlement.getFinanceInnSettlementInfo().getInnContact());
            cell = row.createCell(11);
            cell.setCellValue(financeInnChannelSettlement.getPaymentRemark());
        }
    }

    /**
     * 根据客栈结算对象，构建出账核算EXCEL中的客栈银行卡支付信息
     *
     * @param financeInnChannelSettlement 客栈结算对象
     * @return 银行卡支付信息
     */
    private String buildBankInfo(FinanceInnChannelSettlement financeInnChannelSettlement) {
        StringBuilder bankInfo = new StringBuilder();
        String defaultString = "暂无";
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnChannelSettlement.getFinanceInnSettlementInfo();
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankType(), defaultString));
        bankInfo.append(":");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankAccount(), defaultString));
        bankInfo.append("/");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankCode(), defaultString));
        bankInfo.append("\r\n");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankName(), defaultString));
        bankInfo.append("(");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankRegion(), defaultString));
        bankInfo.append(")");
        return bankInfo.toString();
    }

    private void createExcelTitle(HSSFSheet sheet, FinanceInnChannelSettlement financeInnChannelSettlement, HSSFCellStyle boldCellStyle) {
        // 第一行展示客栈名称+客栈联系电话+结算周期
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 0, 0, 7);
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(1, 1, 0, 7);

        sheet.addMergedRegion(cellRangeAddress1);
        sheet.addMergedRegion(cellRangeAddress2);
        // 第一行展示客栈信息
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue(buildInnInfo(financeInnChannelSettlement));

        // 第二行展示客栈的银行卡信息
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(buildBankInfo(financeInnChannelSettlement));

        // 第三行展示合计数据
        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("合计");
        cell = row.createCell(2);
        cell.setCellValue("总个数:" + financeInnChannelSettlement.getTotalOrder());
        cell = row.createCell(5);
        cell.setCellValue("总金额:" + financeInnChannelSettlement.getInnSettlementAmount());
        // 第四行展示标题
        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("渠道");
        cell = row.createCell(1);
        cell.setCellValue("订单模式");
        cell = row.createCell(2);
        cell.setCellValue("订单号");
        cell = row.createCell(3);
        cell.setCellValue("核单");
        cell = row.createCell(4);
        cell.setCellValue("渠道商款项");
        cell = row.createCell(5);
        cell.setCellValue("客栈结算金额");
        cell = row.createCell(6);
        cell.setCellValue("渠道商结算金额");
        cell = row.createCell(7);
        cell.setCellValue("番茄收入金额");
        cell = row.createCell(8);
        cell.setCellValue("实付金额");
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow hr = sheet.getRow(i);
            for (int k = 0; k < hr.getLastCellNum(); k++) {
                HSSFCell hc = hr.getCell(k);
                if (hc != null) {
                    hc.setCellStyle(boldCellStyle);
                }
            }
        }
    }

    /**
     * 根据客栈结算对象，构建出账核算EXCEL中的客栈信息
     *
     * @param financeInnChannelSettlement 客栈结算对象
     * @return 客栈信息
     */
    private String buildInnInfo(FinanceInnChannelSettlement financeInnChannelSettlement) {
        StringBuilder innInfo = new StringBuilder();
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnChannelSettlement.getFinanceInnSettlementInfo();
        innInfo.append(financeInnSettlementInfo.getInnName());
        innInfo.append("(");
        innInfo.append(financeInnSettlementInfo.getInnContact());
        innInfo.append(")  ");
        innInfo.append("结算周期:");
        innInfo.append(financeInnChannelSettlement.getSettlementTime());
        return innInfo.toString();
    }

    private void fillExcelData(HSSFSheet sheet, List<FinanceParentOrder> parentOrderList, HSSFCellStyle normalCellStyle, FinanceInnChannelSettlement fin) {
        for (int i = 0; i < parentOrderList.size(); i++) {
            FinanceParentOrder financeParentOrder = parentOrderList.get(i);
            HSSFRow row = sheet.createRow(i + 4);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(financeParentOrder.getChannelName());
            cell = row.createCell(1);
            cell.setCellValue(financeParentOrder.getOrderMode());
            cell = row.createCell(2);
            cell.setCellValue(financeParentOrder.getChannelOrderNo());
            cell = row.createCell(3);
            cell.setCellValue(financeParentOrder.getAuditStatusStr());
            cell = row.createCell(4);
            cell.setCellValue(financeParentOrder.getIsArrivalStr());
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(financeParentOrder.getInnSettlementAmount()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(financeParentOrder.getChannelSettlementAmount()));
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(financeParentOrder.getFqSettlementAmount()));
            cell = row.createCell(8);
            cell.setCellValue(String.valueOf(fin.getRealPayment()));
        }
        for (int i = 4; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow hr = sheet.getRow(i);
            for (int k = 0; k < hr.getLastCellNum(); k++) {
                HSSFCell hc = hr.getCell(k);
                if (hc != null) {
                    hc.setCellStyle(normalCellStyle);
                }
            }
        }
    }


    /**
     * 客栈结算详情集合生成Excel文件名称
     *
     * @param financeInnChannelSettlementList 客栈结算详情集合
     * @return Excel文件名称
     */
    private String getFinanceExcelName(List<FinanceInnChannelSettlement> financeInnChannelSettlementList) {
        FinanceInnChannelSettlement financeInnChannelSettlement = financeInnChannelSettlementList.get(0);
        String settlementTime = financeInnChannelSettlement.getSettlementTime();
        if (financeInnChannelSettlementList.size() == 1) {
            FinanceInnSettlementInfo financeInnSettlementInfo = financeInnChannelSettlement.getFinanceInnSettlementInfo();
            String innName = financeInnSettlementInfo.getInnName();
            return settlementTime + "_" + innName + "_结算汇总表";
        } else {
            return settlementTime + "客栈结算汇总表";
        }
    }

    /**
     * 客栈结算详情集合生成Excel文件名称（包含分销商名称）
     *
     * @param financeInnChannelSettlementList 客栈结算详情集合
     * @return Excel文件名称
     */
    private String getFinanceExcelName(List<FinanceInnChannelSettlement> financeInnChannelSettlementList, String channelName) {
        FinanceInnChannelSettlement financeInnChannelSettlement = financeInnChannelSettlementList.get(0);
        String settlementTime = financeInnChannelSettlement.getSettlementTime();
        if (financeInnChannelSettlementList.size() == 1) {
            FinanceInnSettlementInfo financeInnSettlementInfo = financeInnChannelSettlement.getFinanceInnSettlementInfo();
            String innName = financeInnSettlementInfo.getInnName();

            return settlementTime + "_" + innName + "_结算汇总表";
        } else {
            return settlementTime + "【" + channelName + "】客栈结算汇总表";
        }
    }

    @Override
    public void createFinanceExcelWithPay(HttpServletResponse response, String settlementTime) throws Exception {

        List<FinanceInnSettlement> financeInnSettlementList = financeInnSettlementDao.selectFinanceInnSettlementBySettlementTime(settlementTime, true);
        List<Map<String, Object>> mapList = financeInnSettlementDao.getInnSettlementAmount(settlementTime);
        //除去平账过后结算金额为0的客栈
        if (CollectionsUtil.isNotEmpty(financeInnSettlementList)) {
            for (FinanceInnSettlement financeInnSettlement : financeInnSettlementList) {
                if (null != financeInnSettlement.getIsArrears() && financeInnSettlement.getIsArrears().equals("1")) {
                    if (null == financeInnSettlement.getAfterArrearsAmount() && financeInnSettlement.getAfterArrearsAmount().compareTo(BigDecimal.ZERO) != 1) {
                        financeInnSettlementList.remove(financeInnSettlement);
                    }
                }
            }
        }
        if (CollectionsUtil.isEmpty(financeInnSettlementList)) {
            throw new Exception("当前结算月份没有客栈结算数据");
        }

        OutputStream outputStream = null;
        try {
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            //构建表
            HSSFSheet totalSheet = workbook.createSheet("总表");
            renderedExcel(totalSheet, boldCellStyle, 0);
            renderedExcel(totalSheet, normalCellStyle, 3);
            totalSheet.autoSizeColumn(8, true);
            totalSheet.setDefaultColumnWidth(18);
            //第一行
            HSSFRow row = totalSheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("商户号");
            cell = row.createCell(1);
            cell.setCellValue("批次序列号");
            cell = row.createCell(2);
            cell.setCellValue("总笔数");
            cell = row.createCell(3);
            cell.setCellValue("总金额(元)");
            cell = row.createCell(4);
            cell.setCellValue("付款说明");
            //第二行
            row = totalSheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("1306724301");
            cell = row.createCell(1);
            cell.setCellValue("FQLL" + replaceChar2(settlementTime) + "01");
            cell = row.createCell(2);
            cell.setCellValue(mapList.get(0).get("totalorder").toString());
            cell = row.createCell(3);
            cell.setCellValue(mapList.get(0).get("totalamount").toString());
            CellRangeAddress cellRangeAddress = new CellRangeAddress(1, 1, 4, 7);
            totalSheet.addMergedRegion(cellRangeAddress);
            cell = row.createCell(4);

            cell.setCellValue(replaceChar(settlementTime) + "代销平台结算款");
            //第三行
            row = totalSheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("单笔序列");
            cell = row.createCell(1);
            cell.setCellValue("账户类型");
            cell = row.createCell(2);
            cell.setCellValue("真实姓名");
            cell = row.createCell(3);
            cell.setCellValue("收款方银行账号");
            cell = row.createCell(4);
            cell.setCellValue("开户地区");
            cell = row.createCell(5);
            cell.setCellValue("开户城市");
            cell = row.createCell(6);
            cell.setCellValue("银行类型");
            cell = row.createCell(7);
            cell.setCellValue("支行名称");
            cell = row.createCell(8);
            cell.setCellValue("付款金额(元)");
            cell = row.createCell(9);
            cell.setCellValue("付款说明");
            cell = row.createCell(10);
            cell.setCellValue("收款人手机号");
            cell = row.createCell(11);
            cell.setCellValue("卡类型");
            cell = row.createCell(12);
            cell.setCellValue("商户单号");
            cell = row.createCell(13);
            cell.setCellValue("商户备注");
            cell = row.createCell(14);
            cell.setCellValue("备用");

            //填充数据
            fillExcelWithPay(totalSheet, financeInnSettlementList);
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            String fileName = "代销平台" + replaceChar(financeInnSettlementList.get(0).getSettlementTime()) + "批量支付";
            //设置文件名
            response.addHeader("Content-Disposition", new String(("attachment; filename=" + fileName + ".xls").getBytes("GBK"), "ISO-8859-1"));
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new Exception("表格导出时出错!", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                throw new Exception("表格导出时出错!", e);
            }
        }
    }

    @Override
    public List<FinanceInnChannelSettlement> selectChannelOrder(Integer innId, String settlementTime, Boolean isMatch) {
        return financeInnChannelSettlementDao.selectChannelOrder(innId, settlementTime, isMatch);
    }

    @Override
    public List<Map<String, Object>> statisticChannelOrderTotal(Integer innId, String settlementTime) {
        return financeInnChannelSettlementDao.statisticChannelOrderTotal(innId, settlementTime);
    }

    /**
     * 填充数据到excel
     *
     * @param totalSheet
     * @param financeInnSettlements
     */
    private void fillExcelWithPay(HSSFSheet totalSheet, List<FinanceInnSettlement> financeInnSettlements) {
        for (int i = 0, j = 1; i < financeInnSettlements.size(); i++, j++) {
            FinanceInnSettlement financeInnSettlement = financeInnSettlements.get(i);
            HSSFRow row = totalSheet.createRow(i + 3);
            totalSheet.autoSizeColumn(i + 3);
            HSSFCell cell = row.createCell(0);
            FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
            cell.setCellValue(j);
            cell = row.createCell(1);
            cell.setCellValue(financeInnSettlementInfo.getBankType());
            cell = row.createCell(2);
            cell.setCellValue(financeInnSettlementInfo.getBankAccount());
            cell = row.createCell(3);
            cell.setCellValue(financeInnSettlementInfo.getBankCode());
            cell = row.createCell(4);
            cell.setCellValue(financeInnSettlementInfo.getBankProvince());
            cell = row.createCell(5);
            cell.setCellValue(financeInnSettlementInfo.getBankCity());

            String[] bankNames = {"招商银行", "中国工商银行", "中国建设银行", "上海浦东发展银行", "中国农业银行", "中国民生银行", "兴业银行", "平安银行", "中国交通银行", "中国实业银行", "中国光大银行", "中国银行借记卡", "广东发展银行", "中国银行信用卡"};
            String name = financeInnSettlementInfo.getBankName();
            for (int k = 0; k < bankNames.length; k++) {
                if (StringUtils.isBlank(name)) {
                    cell = row.createCell(6);
                    cell.setCellValue(name);
                    cell = row.createCell(7);
                    cell.setCellValue(financeInnSettlementInfo.getBankRegion());
                } else {
                    if (name.equals(bankNames[k])) {
                        cell = row.createCell(6);
                        cell.setCellValue(name);
                        cell = row.createCell(7);
                        cell.setCellValue(financeInnSettlementInfo.getBankRegion());
                        break;
                    } else {
                        cell = row.createCell(6);
                        cell.setCellValue("其他银行");
                        cell = row.createCell(7);
                        cell.setCellValue(name + financeInnSettlementInfo.getBankRegion());
                    }
                }
            }
            cell = row.createCell(8);
            String status = financeInnSettlement.getIsArrears();
            if (StringUtils.isNotBlank(status)) {
                if (status.equals("0")) {
                    cell.setCellValue(wrap(financeInnSettlement.getAfterPaymentAmount()).toString());
                } else {
                    cell.setCellValue(wrap(financeInnSettlement.getAfterArrearsAmount()).toString());
                }
            }
            cell = row.createCell(9);
            cell.setCellValue(financeInnSettlementInfo.getInnName() + replaceChar(financeInnSettlement.getSettlementTime()) + "代销平台结算款");
            cell = row.createCell(10);
            cell.setCellValue(financeInnSettlementInfo.getInnContact());
            cell = row.createCell(11);
            cell.setCellValue("借记卡");
            cell = row.createCell(12);
            cell.setCellValue(j);
            cell = row.createCell(13);
            cell.setCellValue(financeInnSettlementInfo.getInnName());
            cell = row.createCell(14);
            cell.setCellValue("");
        }

    }

    //替换字符串"-"为".","至"为"-"
    private String replaceChar(String oldString) {
        String newString = oldString.replace("-", ".");
        newString = newString.replace("至", "-");
        return newString;
    }

    @Override
    public Map<String, Object> getInnChannelOrderAmount(String settlementTime, Integer channelId, String innName) {
        String sql = "SELECT \n" + "COUNT (DISTINCT t1.inn_id) AS inn_count,SUM (t1.total_order) AS total,COALESCE (SUM (t1.channel_real_settlement_amount),0) AS real_amount,COALESCE(SUM (t1.inn_settlement_amount),0)-COALESCE(SUM (t1.inn_payment),0)-COALESCE(SUM (t1.refund_amount),0)+COALESCE(SUM (t1.fq_replenishment),0) AS inn_settlement_amount " +
                " FROM finance_inn_channel_settlement t1 \n" +
                "WHERE t1.settlement_time = '" + settlementTime + "' and t1.channel_id=" + channelId;
        Map<String, Object> map = financeInnChannelSettlementDao.findMapWithSql(sql);
        List<Map<String, Object>> fqRefundContractsAmount = financeOrderDao.getFqRefundContractsAmount(settlementTime, channelId, null);
        /*if (CollectionUtils.isNotEmpty(fqRefundContractsAmount)) {
            for (Map<String, Object> content : fqRefundContractsAmount) {
                Object contactsStatus = content.get("contacts_status");
                BigDecimal sum = (BigDecimal) content.get("sum");
                BigDecimal realAmount = (BigDecimal) content.get("realAmount");
                if (contactsStatus == null) {
                    continue;
                }
                if (contactsStatus.toString().equals(FinanceOrderService.AFTER)) {
                    map.put("real_amount", (wrap(realAmount)).add(wrap(sum)));
                }
                if (contactsStatus.toString().equals(FinanceOrderService.CURRENT)) {
                    map.put("real_amount", (wrap(realAmount)).subtract(wrap(sum)));
                }
            }
        }*/

        return map;
    }

    private BigDecimal wrap(BigDecimal sour) {
        return sour == null ? new BigDecimal(0) : sour;
    }

    @Override
    public List<FinanceInnChannelSettlement> findFinanceInnChannelSettlementByInnId(Integer id, String settlementTime) {
        return financeInnChannelSettlementDao.findFinanceInnChannelSettlement(id, settlementTime);
    }

    @Override
    public Map<String, Object> statisticArrearsChannel(String settlementTime, Integer innId, Boolean isMatch) {
        return financeInnChannelSettlementDao.statisticArrearsChannel(settlementTime, innId, isMatch);
    }

    @Override
    public Page<Map<String,Object>> findFqTempInn(Page<Map<String,Object>> page, Integer channelId, String settlementTime) {
        return financeInnChannelSettlementDao.findFqTempInn(page, channelId, settlementTime);
    }


    //去除字符串中的"-"
    private String replaceChar2(String oldString) {
        String newString = oldString.replace("至", "");
        newString = newString.replace("-", "");
        return newString;
    }



}
