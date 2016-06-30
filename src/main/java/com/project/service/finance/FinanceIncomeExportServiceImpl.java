package com.project.service.finance;

import com.project.core.orm.Page;
import com.project.dao.finance.*;
import com.project.dao.proxysale.ChannelDao;
import com.project.entity.finance.*;
import com.project.utils.CollectionsUtil;
import com.project.utils.ExcelExportUtil;
import com.project.utils.ExcelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by admin on 2016/3/22.
 */

@Service("financeIncomeExportService")
@Transactional
public class FinanceIncomeExportServiceImpl implements FinanceIncomeExportService {

    private static final int PAGE_SIZE = 100;
    @Autowired
    private ChannelDao channelDao;
    @Resource
    private FinanceChannelSettlementDao financeChannelSettlementDao;
    @Resource
    private FinanceInnChannelSettlementDao financeInnChannelSettlementDao;
    @Resource
    private FinanceManualOrderDao financeManualOrderDao;
    @Resource
    private FinanceOrderDao financeOrderDao;
    @Resource
    private FinanceSpecialOrderDao financeSpecialOrderDao;

    @Override
    public void createIncomeChannelFinanceExcel(HttpServletRequest request, String settlementTime) {

        List<FinanceChannelSettlement> financeChannelSettlementList = new ArrayList<>();
        List<List<FinanceInnChannelSettlement>> financeInnChannelSettlementList = new ArrayList<>();
        List<List<FinanceManualOrder>> noBearFinanceManualOrderList = new ArrayList<>();
        //进账结算渠道
        financeChannelSettlementList = financeChannelSettlementDao.selectFinanceChannelSettlement(settlementTime);
        //渠道下的结算客栈
        if (CollectionsUtil.isEmpty(financeChannelSettlementList)) {
            throw new RuntimeException("当前结算月份没有渠道结算数据");
        }
        for (FinanceChannelSettlement f : financeChannelSettlementList) {
            Integer channelId = f.getChannelId();
            List<FinanceInnChannelSettlement> list = financeInnChannelSettlementDao.findFinanceInnChannelSettlements(channelId, settlementTime);
            //手动订单
            List<FinanceManualOrder> noBearList = financeManualOrderDao.findFinanceManualOrdersWithChannelId(channelId, settlementTime);
            if (CollectionsUtil.isNotEmpty(list)) {
                financeInnChannelSettlementList.add(list);
            }
            if (CollectionsUtil.isNotEmpty(noBearList)) {
                noBearFinanceManualOrderList.add(noBearList);
            }
        }
        if (CollectionsUtil.isEmpty(financeInnChannelSettlementList)) {
            throw new RuntimeException("当月渠道结算没有客栈结算的数据");
        }

//        OutputStream outputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            HSSFCellStyle style = workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            font.setColor(IndexedColors.RED.getIndex());
            style.setFont(font);
            // 第一个sheet是总表，统计客栈全部数据
            HSSFSheet totalSheet = workbook.createSheet("总表");
            // 构建总表
            fillTotalSheetData(totalSheet, financeChannelSettlementList, normalCellStyle, boldCellStyle, style);

            for (int i = 0; i < financeInnChannelSettlementList.size(); i++) {
                List<FinanceInnChannelSettlement> financeInnChannelSettlements = financeInnChannelSettlementList.get(i);
                if (StringUtils.isBlank(financeInnChannelSettlements.get(0).getChannelName())) {
                    throw new RuntimeException("渠道名称为空");
                }
                String channelName = financeInnChannelSettlements.get(0).getChannelName();
                String sheetName = ExcelUtil.getSheetNameByInnName(channelName);
                HSSFSheet sheet = workbook.createSheet(sheetName);
                sheet.autoSizeColumn(8, true);
                sheet.setDefaultColumnWidth(18);
                // 创建表头
                createExcelTitle(sheet, financeInnChannelSettlements.get(0), boldCellStyle);
                fillExcelData(sheet, financeInnChannelSettlements, normalCellStyle);
            }
            //无订单扣款
            for (int i = 0; i < noBearFinanceManualOrderList.size(); i++) {
                List<FinanceManualOrder> financeManualOrders = noBearFinanceManualOrderList.get(i);
                String channelName = financeManualOrders.get(0).getChannel().getChannelName();
                String sheetName = ExcelUtil.getSheetNameByInnName(channelName);
                HSSFSheet sheet = workbook.createSheet(sheetName + "无订单扣款");
                sheet.autoSizeColumn(8, true);
                sheet.setDefaultColumnWidth(18);
                // 创建表头
                createNoBearExcelTitle(sheet, financeManualOrders.get(0), boldCellStyle);
                fillNoBearExcelData(sheet, financeManualOrders, normalCellStyle);
            }


//            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            // 拼接Excel文件名称
            String fileName = financeChannelSettlementList.get(0).getSettlementTime() + "进账总表" + "V" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls";
            fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
            // 设置文件名
//            response.addHeader("Content-Disposition", new String(("attachment; filename=" + fileName + ".xls").getBytes("GBK"), "ISO-8859-1"));
//            outputStream = response.getOutputStream();
            // 导出Excel
            String realPath = request.getSession().getServletContext().getRealPath("/") + "download";
            File file = new File(realPath + "/" + fileName);
            fileOutputStream = new FileOutputStream(file);
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
     * 创建出账核对的总表
     *
     * @param totalSheet      总表的sheet
     * @param normalCellStyle 普通单元格样式
     * @param boldCellStyle   加粗单元格样式
     */
    private void fillTotalSheetData(HSSFSheet totalSheet, List<FinanceChannelSettlement> financeChannelSettlementList, HSSFCellStyle normalCellStyle, HSSFCellStyle boldCellStyle, HSSFCellStyle style) {
        FinanceChannelSettlement financeChannelSettlement = financeChannelSettlementList.get(0);
        totalSheet.autoSizeColumn(8, true);
        totalSheet.setDefaultColumnWidth(18);
        // 创建表头
        buildTotalSheetTitle(totalSheet, financeChannelSettlement);
        ExcelUtil.renderedExcel(totalSheet, boldCellStyle, 0);
        buildTotalSheetData(totalSheet, financeChannelSettlementList, style);
        ExcelUtil.renderedExcel(totalSheet, normalCellStyle, 2);
        ExcelUtil.renderedExcelCell(totalSheet,style,2,3,7);
    }

    /**
     * 填充出账核算总表表头
     *
     * @param totalSheet
     */
    private void buildTotalSheetTitle(HSSFSheet totalSheet, FinanceChannelSettlement financeChannelSettlement) {
        Map<String, Object> dataMap = financeChannelSettlementDao.statisticChannelAmount(financeChannelSettlement.getSettlementTime());
        if (dataMap.size() <= 0) {
            throw new RuntimeException("本月没有渠道结算");
        }
        int innCount = Integer.parseInt(String.valueOf(dataMap.get("count")));
        BigDecimal camount = (BigDecimal) (dataMap.get("camount"));
        BigDecimal debit = (BigDecimal) dataMap.get("debit");
        BigDecimal refunded = (BigDecimal) dataMap.get("refunded");
        BigDecimal cur = (BigDecimal) dataMap.get("cur");
        BigDecimal next = (BigDecimal) dataMap.get("next");
        BigDecimal namount = (BigDecimal) dataMap.get("namount");
        BigDecimal ramount = (BigDecimal) dataMap.get("ramount");
        BigDecimal fq = (BigDecimal) dataMap.get("fq");
        // 第一行展示合计内容
        HSSFRow row = totalSheet.createRow(0);
        HSSFCell cell = row.createCell(1);
        cell.setCellValue("合计:      " + innCount);
        cell = row.createCell(2);
        cell.setCellValue(String.valueOf(camount));
        cell = row.createCell(3);
        cell.setCellValue(String.valueOf(debit));
        cell = row.createCell(4);
        cell.setCellValue(String.valueOf(cur));
        cell = row.createCell(5);
        cell.setCellValue(String.valueOf(next));
        cell = row.createCell(6);
        cell.setCellValue(String.valueOf(refunded));
        cell = row.createCell(7);
        cell.setCellValue(String.valueOf(namount));
        cell = row.createCell(8);
        cell.setCellValue(String.valueOf(fq));
        cell = row.createCell(9);
        cell.setCellValue(String.valueOf(ramount));

        // 第二行展示表头
        row = totalSheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("序号");
        cell = row.createCell(1);
        cell.setCellValue("分销商");
        cell = row.createCell(2);
        cell.setCellValue("分销商应结算金额(正常订单)");
        cell = row.createCell(3);
        cell.setCellValue("分销商扣番茄金额(赔付)");
        cell = row.createCell(4);
        cell.setCellValue("上期未结算,本期平账(退款)");
        cell = row.createCell(5);
        cell.setCellValue("本期不结算,下期平账(退款)");
        cell = row.createCell(6);
        cell.setCellValue("已结算退款(退款)");
        cell = row.createCell(7);
        cell.setCellValue("分销商扣款(无订单)");
        cell = row.createCell(8);
        cell.setCellValue("番茄暂收");
        cell = row.createCell(9);
        cell.setCellValue("分销商实际结算金额");
        cell = row.createCell(10);
        cell.setCellValue("实收金额");
        cell = row.createCell(11);
        cell.setCellValue("备注");
        cell = row.createCell(12);
        cell.setCellValue("番茄收入(实际收入)");
    }


    /**
     * 填充出账核算客栈结算总表数据
     *
     * @param totalSheet
     */
    private void buildTotalSheetData(HSSFSheet totalSheet, List<FinanceChannelSettlement> financeChannelSettlementList, HSSFCellStyle style) {
        for (int i = 0; i < financeChannelSettlementList.size(); i++) {
            FinanceChannelSettlement financeChannelSettlement = financeChannelSettlementList.get(i);
            HSSFRow row = totalSheet.createRow(i + 2);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell = row.createCell(1);
            cell.setCellValue(financeChannelSettlement.getChannelName());
            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getChannelSettlementAmount()));
            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getChannelDebit()));
            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getCurrentRefundAmount()));
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getRefundedAmount()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getNextRefundAmount()));
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getNoOrderDebitAmount()));
            cell = row.createCell(8);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getFqTemp()));
            cell = row.createCell(9);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getChannelRealAmount()));
            cell = row.createCell(10);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getIncomeAmount()));
            cell = row.createCell(11);
            cell.setCellValue(financeChannelSettlement.getRemarks());
            cell = row.createCell(12);
            cell.setCellValue(String.valueOf(financeChannelSettlement.getFqRealIncome()));
        }
    }

    private void createExcelTitle(HSSFSheet sheet, FinanceInnChannelSettlement financeInnChannelSettlement, HSSFCellStyle boldCellStyle) {
        Map<String, Object> mapList = financeInnChannelSettlementDao.statisticChannelInnAmount(financeInnChannelSettlement.getChannelId(), financeInnChannelSettlement.getSettlementTime());
        Integer inncount = Integer.parseInt(String.valueOf(mapList.get("inncount")));
        BigDecimal channel = (BigDecimal) mapList.get("channel");
        BigDecimal payment = (BigDecimal) mapList.get("payment");
        BigDecimal bear = (BigDecimal) mapList.get("bear");
        BigDecimal income = (BigDecimal) mapList.get("income");
        BigDecimal refund = (BigDecimal) mapList.get("refund");
        BigDecimal fqrefund = (BigDecimal) mapList.get("fqrefund");
        BigDecimal currefund = (BigDecimal) mapList.get("currefund");
        BigDecimal aftrefund = (BigDecimal) mapList.get("aftrefund");
        BigDecimal real = (BigDecimal) mapList.get("real");
        BigDecimal normal = (BigDecimal) mapList.get("normal");
        BigDecimal inns = (BigDecimal) mapList.get("inns");
        // 第一行展示客栈合计
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(2);
        cell.setCellValue("合计  " + inncount);
        cell = row.createCell(3);
        cell.setCellValue(String.valueOf(channel));
        cell = row.createCell(4);
        cell.setCellValue(String.valueOf(inns));
        cell = row.createCell(5);
        cell.setCellValue(String.valueOf(payment));
        cell = row.createCell(6);
        cell.setCellValue(String.valueOf(bear));
        cell = row.createCell(7);
        cell.setCellValue(String.valueOf(income));
        cell = row.createCell(8);
        cell.setCellValue(String.valueOf(refund));
        cell = row.createCell(9);
        cell.setCellValue(String.valueOf(fqrefund));
        cell = row.createCell(10);
        cell.setCellValue(String.valueOf(currefund));
        cell = row.createCell(11);
        cell.setCellValue(String.valueOf(aftrefund));
        cell = row.createCell(12);
        cell.setCellValue(String.valueOf(real));
        cell = row.createCell(13);
        cell.setCellValue(String.valueOf(normal));


        // 第二行展示标题
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("序号");
        cell = row.createCell(1);
        cell.setCellValue("城市");
        cell = row.createCell(2);
        cell.setCellValue("客栈名称\r\n(id)");
        cell = row.createCell(3);
        cell.setCellValue("分销商应结算金额(正常订单)");
        cell = row.createCell(4);
        cell.setCellValue("客栈应结算金额(正常订单)");
        cell = row.createCell(5);
        cell.setCellValue("客栈赔付金额");
        cell = row.createCell(6);
        cell.setCellValue("客栈赔付番茄承担金额");
        cell = row.createCell(7);
        cell.setCellValue("客栈赔付番茄收入金额");
        cell = row.createCell(8);
        cell.setCellValue("本期客栈退款金额");
        cell = row.createCell(9);
        cell.setCellValue("本期番茄退收入金额");
        cell = row.createCell(10);
        cell.setCellValue("本期番茄退往来金额");
        cell = row.createCell(11);
        cell.setCellValue("后期番茄退往来金额");
        cell = row.createCell(12);
        cell.setCellValue("分销商实际结算金额");
        cell = row.createCell(13);
        cell.setCellValue("番茄正常订单收入");


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

    //无赔付订单
    private void createNoBearExcelTitle(HSSFSheet sheet, FinanceManualOrder financeManualOrder, HSSFCellStyle boldCellStyle) {

        // 第一行展示客栈合计
        Map<String, Object> map = financeManualOrderDao.getManualOrderAmount(financeManualOrder.getChannel().getId(), financeManualOrder.getSettlementTime(), null);
        Integer inncount = Integer.parseInt(String.valueOf(map.get("count")));
        BigDecimal amount = (BigDecimal) map.get("amount");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("合计: " + inncount);
        cell = row.createCell(2);
        cell.setCellValue(String.valueOf(amount));
        // 第二行展示标题
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("序号");
        cell = row.createCell(1);
        cell.setCellValue("订单号");
        cell = row.createCell(2);
        cell.setCellValue("分销商扣番茄金额");
        cell = row.createCell(3);
        cell.setCellValue("备注");

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
     * @param financeInnSettlement 客栈结算对象
     * @return 客栈信息
     */
    private String buildInnInfo(FinanceInnSettlement financeInnSettlement) {
        StringBuilder innInfo = new StringBuilder();
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
        innInfo.append(financeInnSettlementInfo.getInnName());
        innInfo.append("(");
        innInfo.append(financeInnSettlementInfo.getInnContact());
        innInfo.append(")  ");
        innInfo.append("结算周期:");
        innInfo.append(financeInnSettlement.getSettlementTime());
        return innInfo.toString();
    }


    private void fillExcelData(HSSFSheet sheet, List<FinanceInnChannelSettlement> financeInnChannelSettlements, HSSFCellStyle normalCellStyle) {
        for (int i = 0; i < financeInnChannelSettlements.size(); i++) {
            FinanceInnChannelSettlement financeInnChannelSettlement = financeInnChannelSettlements.get(i);
            HSSFRow row = sheet.createRow(i + 2);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell = row.createCell(1);
            cell.setCellValue(financeInnChannelSettlement.getFinanceInnSettlementInfo().getRegionName());
            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getFinanceInnSettlementInfo().getInnName()) + "\r\n(" + financeInnChannelSettlement.getFinanceInnSettlementInfo().getId() + ")");
            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getChannelSettlementAmount()));
            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getInnSettlementAmount()));
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getInnPayment()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getFqBearAmount()));
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getFqIncomeAmount()));
            cell = row.createCell(8);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getRefundAmount()));
            cell = row.createCell(9);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getFqRefundCommissionAmount()));
            cell = row.createCell(10);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getCurFqRefundContacts()));
            cell = row.createCell(11);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getAftFqRefundContacts()));
            cell = row.createCell(12);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getChannelRealSettlementAmount()));
            cell = row.createCell(13);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getFqNormalIncome()));
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

    //无赔付订单
    private void fillNoBearExcelData(HSSFSheet sheet, List<FinanceManualOrder> financeManualOrders, HSSFCellStyle normalCellStyle) {
        for (int i = 0; i < financeManualOrders.size(); i++) {
            FinanceManualOrder financeManualOrder = financeManualOrders.get(i);
            HSSFRow row = sheet.createRow(i + 2);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(financeManualOrder.getOrderId());
            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(financeManualOrder.getRefund()));
            cell = row.createCell(3);
            cell.setCellValue(financeManualOrder.getRemark());
        }

    }


    @Override
    public void exportInnSettlement(HttpServletRequest request, String settlementTime, Integer channelId) {
        FinanceInnChannelSettlement channelNameWithChannelId = financeInnChannelSettlementDao.findChannelNameWithChannelId(channelId, settlementTime);
        String channelName;
        if (null != channelNameWithChannelId) {
            channelName = channelNameWithChannelId.getChannelName();
        } else {
            channelName = null;
        }
        FileOutputStream fileOutputStream = null;
        try {
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            // 第一个sheet是总表，统计分销商全部数据 ok
            createMainSheet(workbook, channelName, settlementTime, channelId, boldCellStyle, normalCellStyle);
            // 正常订单表
            createNormalSheet(workbook, settlementTime, channelId, boldCellStyle, normalCellStyle);
            // 赔付订单表
            createDebitSheet(workbook, settlementTime, channelId, boldCellStyle, normalCellStyle);
            // 退款订单表
            createRefundSheet(workbook, settlementTime, channelId, boldCellStyle, normalCellStyle);
            // 补款订单表
            createReplenishmentSheet(workbook, settlementTime, channelId, boldCellStyle, normalCellStyle);
            // 无订单赔付表
            createManualOrderSheet(workbook, settlementTime, channelId, boldCellStyle, normalCellStyle);
            fileOutputStream = getFileOutput(request, settlementTime, channelName);
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createMainSheet(HSSFWorkbook workbook, String channelName, String settlementTime, Integer channelId, HSSFCellStyle boldCellStyle, HSSFCellStyle normalCellStyle) {
        String sheetName = ExcelUtil.getSheetNameByInnName(channelName);
        HSSFSheet sheet = workbook.createSheet(sheetName + "汇总表");
        sheet.autoSizeColumn(8, true);
        sheet.setDefaultColumnWidth(18);
        createMainTitleCells(sheet);
        ExcelUtil.renderedExcel(sheet, boldCellStyle, 2);
        Map<String, Object> map = createMainContentCells(sheet, settlementTime, channelId);
        createMainTotalCells(sheet, map);
        ExcelUtil.renderedExcel(sheet, normalCellStyle, 2);
    }

    private void createMainTotalCells(HSSFSheet sheet, Map<String, Object> map) {
        HSSFRow row = sheet.createRow(0);

        HSSFCellUtil.createCell(row, 1, "合计");
        createCell(row, 2, Integer.valueOf(map.get("innCount").toString()));
        createCell(row, 4, Double.valueOf(map.get("channelSettlementAmount").toString()));
        createCell(row, 5, Double.valueOf(map.get("innSettlementAmount").toString()));
        createCell(row, 6, Double.valueOf(map.get("innPayAmount").toString()));
        createCell(row, 7, Double.valueOf(map.get("fqBearAmount").toString()));
        createCell(row, 8, Double.valueOf(map.get("fqIncomeAmount").toString()));
        createCell(row, 9, Double.valueOf(map.get("refundAmount").toString()));
        createCell(row, 10, Double.valueOf(map.get("fqRefundCommissionAmount").toString()));
        createCell(row, 11, Double.valueOf(map.get("curFqRefundContacts").toString()));
        createCell(row, 12, Double.valueOf(map.get("aftFqRefundContacts").toString()));
        createCell(row, 13, Double.valueOf(map.get("channelRealSettlementAmount").toString()));
        createCell(row, 14, Double.valueOf(map.get("fqIncome").toString()));
    }

    private Map<String, Object> createMainContentCells(HSSFSheet sheet, String settlementTime, Integer channelId) {
        Page<FinanceInnChannelSettlement> page = new Page<>();
        int index = 2;//内容从第二行开始
        int innCount = 0;
        BigDecimal channelSettlementAmount = new BigDecimal(0);
        BigDecimal innSettlementAmount = new BigDecimal(0);
        BigDecimal innPayAmount = new BigDecimal(0);
        BigDecimal fqBearAmount = new BigDecimal(0);
        BigDecimal fqIncomeAmount = new BigDecimal(0);
        BigDecimal refundAmount = new BigDecimal(0);
        BigDecimal fqRefundCommissionAmount = new BigDecimal(0);
        BigDecimal curFqRefundContacts = new BigDecimal(0);
        BigDecimal aftFqRefundContacts = new BigDecimal(0);
        BigDecimal channelRealSettlementAmount = new BigDecimal(0);
        BigDecimal fqIncome = new BigDecimal(0);
        HSSFRow row;
        while (true) {
            page = financeInnChannelSettlementDao.financeInnChannelSettlementWithRequire(page, settlementTime, channelId, null, null);
            List result = page.getResult();
            if (CollectionUtils.isNotEmpty(result)) {
                for (Object o : result) {
                    row = sheet.createRow(index++);
                    FinanceInnChannelSettlement financeInnChannelSettlement = (FinanceInnChannelSettlement) o;
                    createCell(row, 0, ++innCount);
                    FinanceInnSettlementInfo innSettlementInfo = financeInnChannelSettlement.getFinanceInnSettlementInfo();
                    HSSFCellUtil.createCell(row, 1, innSettlementInfo.getRegionName());
                    HSSFCellUtil.createCell(row, 2, innSettlementInfo.getInnName() + "(" + innSettlementInfo.getId() + ")");
                    HSSFCellUtil.createCell(row, 3, String.valueOf(innSettlementInfo.getId()));

                    BigDecimal channelSettlementAmountExc = wrap(financeInnChannelSettlement.getChannelSettlementAmount());
                    createCell(row, 4, channelSettlementAmountExc.doubleValue());//分销商应结算金额
                    channelSettlementAmount = channelSettlementAmount.add(channelSettlementAmountExc);

                    BigDecimal innSettlementAmountExc = wrap(financeInnChannelSettlement.getInnSettlementAmount());
                    createCell(row, 5, innSettlementAmountExc.doubleValue());//客栈应结算金额
                    innSettlementAmount = innSettlementAmount.add(innSettlementAmountExc);

                    BigDecimal innPayAmountExc = wrap(financeInnChannelSettlement.getInnPayment());
                    createCell(row, 6, innPayAmountExc.doubleValue());//客栈赔付金额
                    innPayAmount = innPayAmount.add(innPayAmountExc);

                    BigDecimal fqBearAmountExc = wrap(financeInnChannelSettlement.getFqBearAmount());
                    createCell(row, 7, fqBearAmountExc.doubleValue());//客栈赔付番茄承担金额
                    fqBearAmount = fqBearAmount.add(fqBearAmountExc);

                    BigDecimal fqIncomeAmountExc = wrap(financeInnChannelSettlement.getFqIncomeAmount());
                    createCell(row, 8, fqIncomeAmountExc.doubleValue());//客栈赔付番茄收入金额
                    fqIncomeAmount = fqIncomeAmount.add(fqIncomeAmountExc);


                    BigDecimal refundAmountExc = wrap(financeInnChannelSettlement.getRefundAmount());
                    createCell(row, 9, refundAmountExc.doubleValue());//本期客栈退款金额
                    refundAmount = refundAmount.add(refundAmountExc);

                    BigDecimal fqRefundCommissionAmountExc = wrap(financeInnChannelSettlement.getFqRefundCommissionAmount());
                    createCell(row, 10, fqRefundCommissionAmountExc.doubleValue());//本期番茄退收入金额
                    fqRefundCommissionAmount = fqRefundCommissionAmount.add(fqRefundCommissionAmountExc);

                    BigDecimal curFqRefundContactsExc = wrap(financeInnChannelSettlement.getCurFqRefundContacts());
                    createCell(row, 11, curFqRefundContactsExc.doubleValue());//本期番茄退往来金额
                    curFqRefundContacts = curFqRefundContacts.add(curFqRefundContactsExc);

                    BigDecimal aftFqRefundContactsExc = wrap(financeInnChannelSettlement.getAftFqRefundContacts());
                    createCell(row, 12, aftFqRefundContactsExc.doubleValue());//后期番茄退往来金额
                    aftFqRefundContacts = aftFqRefundContacts.add(aftFqRefundContactsExc);

                    BigDecimal channelRealSettlementAmountExc = wrap(financeInnChannelSettlement.getChannelRealSettlementAmount());
                    createCell(row, 13, channelRealSettlementAmountExc.doubleValue());//分销商实际结算金额
                    channelRealSettlementAmount = channelRealSettlementAmount.add(channelRealSettlementAmountExc);

                    BigDecimal fqIncomeExc = wrap(channelSettlementAmountExc.subtract(financeInnChannelSettlement.getInnSettlementAmount()));
                    createCell(row, 14, fqIncomeExc.doubleValue());//番茄收入
                    fqIncome = fqIncome.add(fqIncomeExc);
                }
            }
            if (page.isHasNext()) {
                page.setPageNo(page.getNextPage());
            } else {
                break;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("innCount", innCount);
        map.put("channelSettlementAmount", channelSettlementAmount);
        map.put("innSettlementAmount", innSettlementAmount);
        map.put("innPayAmount", innPayAmount);
        map.put("fqBearAmount", fqBearAmount);
        map.put("fqIncomeAmount", fqIncomeAmount);
        map.put("refundAmount", refundAmount);
        map.put("fqRefundCommissionAmount", fqRefundCommissionAmount);
        map.put("curFqRefundContacts", curFqRefundContacts);
        map.put("aftFqRefundContacts", aftFqRefundContacts);
        map.put("channelRealSettlementAmount", channelRealSettlementAmount);
        map.put("fqIncome", fqIncome);
        return map;
    }

    private BigDecimal wrap(BigDecimal sour) {
        return sour == null ? new BigDecimal(0) : sour;
    }

    private void createMainTitleCells(HSSFSheet sheet) {
        HSSFRow row = sheet.createRow(1);
        HSSFCellUtil.createCell(row, 0, "序号");
        HSSFCellUtil.createCell(row, 1, "城市");
        HSSFCellUtil.createCell(row, 2, "客栈名称");
        HSSFCellUtil.createCell(row, 3, "客栈id");
        HSSFCellUtil.createCell(row, 4, "分销商应结算金额（正常订单）");
        HSSFCellUtil.createCell(row, 5, "客栈应结算金额（正常订单）");
        HSSFCellUtil.createCell(row, 6, "客栈赔付金额");
        HSSFCellUtil.createCell(row, 7, "客栈赔付番茄承担金额");
        HSSFCellUtil.createCell(row, 8, "客栈赔付番茄收入金额");
        HSSFCellUtil.createCell(row, 9, "本期客栈退款金额");
        HSSFCellUtil.createCell(row, 10, "本期番茄退收入金额");
        HSSFCellUtil.createCell(row, 11, "本期番茄退往来金额");
        HSSFCellUtil.createCell(row, 12, "后期番茄退往来金额");
        HSSFCellUtil.createCell(row, 13, "分销商实际结算金额");
        HSSFCellUtil.createCell(row, 14, "番茄收入（实际收入）");
    }

    private void createNormalSheet(HSSFWorkbook workbook, String settlementTime, Integer channelId, HSSFCellStyle boldCellStyle, HSSFCellStyle normalCellStyle) {
        HSSFSheet sheet = workbook.createSheet("正常订单表");
        sheet.autoSizeColumn(8, true);
        sheet.setDefaultColumnWidth(18);
        createNormalTitleCells(sheet);
        ExcelUtil.renderedExcel(sheet, boldCellStyle, 0);
        createNormalContentCells(sheet, settlementTime, channelId);
        ExcelUtil.renderedExcel(sheet, normalCellStyle, 0);
//         ExcelUtil.renderedExcel(sheet, boldCellStyle, 0);
    }

    private void createNormalTitleCells(HSSFSheet sheet) {

        HSSFRow row = sheet.createRow(1);
        HSSFCellUtil.createCell(row, 0, "订单模式");
        HSSFCellUtil.createCell(row, 1, "订单号");
        HSSFCellUtil.createCell(row, 2, "客栈名称");
        HSSFCellUtil.createCell(row, 3, "预订人");
        HSSFCellUtil.createCell(row, 4, "手机号码");
        HSSFCellUtil.createCell(row, 5, "房型");
        HSSFCellUtil.createCell(row, 6, "住离日期");
        HSSFCellUtil.createCell(row, 7, "产生周期");
        HSSFCellUtil.createCell(row, 8, "分销商订单金额");
        HSSFCellUtil.createCell(row, 9, "分销商结算金额");
        HSSFCellUtil.createCell(row, 10, "番茄总调价");
        HSSFCellUtil.createCell(row, 11, "客栈结算金额");
        HSSFCellUtil.createCell(row, 12, "番茄佣金收入");
    }

    /**
     * 获取正常订单内容
     */
    private void createNormalContentCells(HSSFSheet sheet, String settlementTime, Integer channelId) {
        int orderCount = 0;
        BigDecimal channelOrderAmount = new BigDecimal(0);
        BigDecimal channelSettlementAmount = new BigDecimal(0);
        BigDecimal fqExtraPriceAmount = new BigDecimal(0);
        BigDecimal innSettlementAmount = new BigDecimal(0);
        BigDecimal fqCommissionIncomeAmount = new BigDecimal(0);

        Page<FinanceParentOrder> page = new Page<>(PAGE_SIZE);
        page.setOrder("desc");
        page.setOrderBy("inn_name");
        HSSFRow row;
        int rowIndex = 2;
        while (true) {
            page = financeOrderDao.selectFinanceParentOrderPage(page, null, channelId, settlementTime, null, null, null, null, true);
            List result = page.getResult();
            if (CollectionUtils.isNotEmpty(result)) {
                for (Object o : result) {
                    FinanceParentOrder parentOrder = (FinanceParentOrder) o;
                    row = sheet.createRow(rowIndex++);
                    orderCount++;
                    channelOrderAmount = channelOrderAmount.add(wrap(parentOrder.getTotalAmount()));
                    channelSettlementAmount = channelSettlementAmount.add(wrap(parentOrder.getChannelSettlementAmount()));
                    fqExtraPriceAmount = fqExtraPriceAmount.add(wrap(parentOrder.getExtraPrice()));
                    innSettlementAmount = innSettlementAmount.add(wrap(parentOrder.getInnSettlementAmount()));
                    fqCommissionIncomeAmount = fqCommissionIncomeAmount.add(wrap(parentOrder.getFqSettlementAmount()));
                    HSSFCellUtil.createCell(row, 0, priceStrategyName(parentOrder.getPriceStrategy()));
                    HSSFCellUtil.createCell(row, 1, parentOrder.getChannelOrderNo());
                    HSSFCellUtil.createCell(row, 2, parentOrder.getInnName());
                    HSSFCellUtil.createCell(row, 3, parentOrder.getUserName());
                    HSSFCellUtil.createCell(row, 4, parentOrder.getContact());
                    HSSFCellUtil.createCell(row, 5, parentOrder.getChannelRoomTypeName());
                    HSSFCellUtil.createCell(row, 6, parentOrder.getCheckDate());//住离日期
                    HSSFCellUtil.createCell(row, 7, parentOrder.getProduceTime());//产生日期
                    HSSFCellUtil.createCell(row, 8, String.valueOf(parentOrder.getTotalAmount()));//分销商订单金额
                    HSSFCellUtil.createCell(row, 9, String.valueOf(wrap(parentOrder.getChannelSettlementAmount())));
                    HSSFCellUtil.createCell(row, 10, String.valueOf(wrap(parentOrder.getExtraPrice())));
                    HSSFCellUtil.createCell(row, 11, String.valueOf(wrap(parentOrder.getInnSettlementAmount())));
                    HSSFCellUtil.createCell(row, 12, String.valueOf(wrap(parentOrder.getFqSettlementAmount())));
                }


            }
            if (page.isHasNext()) {
                page.setPageNo(page.getNextPage());
            } else {
                break;
            }
        }
        row = sheet.createRow(0);
        HSSFCellUtil.createCell(row, 0, "合计");
        HSSFCellUtil.createCell(row, 1, String.valueOf(orderCount));
        HSSFCellUtil.createCell(row, 8, String.valueOf(channelOrderAmount));
        HSSFCellUtil.createCell(row, 9, String.valueOf(channelSettlementAmount));
        HSSFCellUtil.createCell(row, 10, String.valueOf(fqExtraPriceAmount));
        HSSFCellUtil.createCell(row, 11, String.valueOf(innSettlementAmount));
        HSSFCellUtil.createCell(row, 12, String.valueOf(fqCommissionIncomeAmount));
    }

    private String priceStrategyName(Short strategy) {
        if (strategy == 1) {
            return "精品";
        } else if (strategy == 2) {
            return "普通(卖)";
        } else if (strategy == 3) {
            return "普通(卖转底)";
        } else {
            return StringUtils.EMPTY;
        }
    }

    private void createDebitSheet(HSSFWorkbook workbook, String settlementTime, Integer channelId, HSSFCellStyle boldCellStyle, HSSFCellStyle normalCellStyle) {
        HSSFSheet sheet = workbook.createSheet("赔付订单表");
        sheet.autoSizeColumn(8, true);
        sheet.setDefaultColumnWidth(18);
        createDebitTitleCells(sheet);
        ExcelUtil.renderedExcel(sheet, boldCellStyle, 0);
        createDebitContentCells(sheet, settlementTime, channelId);
        ExcelUtil.renderedExcel(sheet, normalCellStyle, 0);

    }

    private void createDebitTitleCells(HSSFSheet sheet) {

        HSSFRow row = sheet.createRow(1);
        HSSFCellUtil.createCell(row, 0, "订单模式");
        HSSFCellUtil.createCell(row, 1, "订单号");
        HSSFCellUtil.createCell(row, 2, "客栈名称");
        HSSFCellUtil.createCell(row, 3, "预订人");
        HSSFCellUtil.createCell(row, 4, "手机号码");
        HSSFCellUtil.createCell(row, 5, "房型");
        HSSFCellUtil.createCell(row, 6, "住离日期");
        HSSFCellUtil.createCell(row, 7, "产生周期");
        HSSFCellUtil.createCell(row, 8, "分销商订单金额");
        HSSFCellUtil.createCell(row, 9, "分销商扣赔付金额");
        HSSFCellUtil.createCell(row, 10, "番茄总调价");
        HSSFCellUtil.createCell(row, 11, "客栈赔付金额");
        HSSFCellUtil.createCell(row, 12, "番茄承担客栈赔付");
        HSSFCellUtil.createCell(row, 13, "番茄营业外收入");


    }

    private void createDebitContentCells(HSSFSheet sheet, String settlementTime, Integer channelId) {
        Integer innCount = 0;
        BigDecimal channelAmount = new BigDecimal(0);
        BigDecimal channelDebit = new BigDecimal(0);
        BigDecimal fqExtra = new BigDecimal(0);
        BigDecimal innDebit = new BigDecimal(0);
        BigDecimal fqDebit = new BigDecimal(0);
        BigDecimal fqIncome = new BigDecimal(0);
        Page<FinanceSpecialOrder> page = new Page<>(PAGE_SIZE);
        page.setOrder("desc");
        page.setOrderBy("inn_name");
        HSSFRow row;
        int rowIndex = 2;
        while (true) {
            page = financeSpecialOrderDao.findFinanceSpecialOrder(page, FinanceSpecialOrder.STATUS_KEY_DEBIT, null, channelId, settlementTime, null, null, null);
            List result = page.getResult();
            if (CollectionUtils.isNotEmpty(result)) {
                for (Object o : result) {
                    FinanceSpecialOrder financeSpecialOrder = (FinanceSpecialOrder) o;
                    row = sheet.createRow(rowIndex++);
                    innCount++;
                    channelAmount = channelAmount.add(wrap(financeSpecialOrder.getFinanceParentOrder().getTotalAmount()));
                    channelDebit = channelDebit.add(wrap(financeSpecialOrder.getChannelDebit()));
                    fqExtra = fqExtra.add(wrap(financeSpecialOrder.getFinanceParentOrder().getExtraPrice()));
                    innDebit = innDebit.add(wrap(financeSpecialOrder.getInnPayment()));
                    fqDebit = fqDebit.add(wrap(financeSpecialOrder.getFqBear()));
                    fqIncome = fqIncome.add(wrap(financeSpecialOrder.getFqIncome()));

                    HSSFCellUtil.createCell(row, 0, priceStrategyName(financeSpecialOrder.getFinanceParentOrder().getPriceStrategy()));
                    HSSFCellUtil.createCell(row, 1, financeSpecialOrder.getFinanceParentOrder().getChannelOrderNo());
                    HSSFCellUtil.createCell(row, 2, financeSpecialOrder.getFinanceParentOrder().getInnName());
                    HSSFCellUtil.createCell(row, 3, financeSpecialOrder.getFinanceParentOrder().getUserName());
                    HSSFCellUtil.createCell(row, 4, financeSpecialOrder.getFinanceParentOrder().getContact());
                    HSSFCellUtil.createCell(row, 5, financeSpecialOrder.getFinanceParentOrder().getChannelRoomTypeName());
                    HSSFCellUtil.createCell(row, 6, financeSpecialOrder.getFinanceParentOrder().getCheckDate());//住离日期
                    HSSFCellUtil.createCell(row, 7, financeSpecialOrder.getFinanceParentOrder().getProduceTime());//产生日期
                    HSSFCellUtil.createCell(row, 8, String.valueOf(wrap(financeSpecialOrder.getFinanceParentOrder().getTotalAmount())));//分销商订单金额
                    HSSFCellUtil.createCell(row, 9, String.valueOf(wrap(financeSpecialOrder.getChannelDebit())));
                    HSSFCellUtil.createCell(row, 10, String.valueOf(wrap(financeSpecialOrder.getFinanceParentOrder().getExtraPrice())));
                    HSSFCellUtil.createCell(row, 11, String.valueOf(wrap(financeSpecialOrder.getInnPayment())));
                    HSSFCellUtil.createCell(row, 12, String.valueOf(wrap(financeSpecialOrder.getFqBear())));
                    HSSFCellUtil.createCell(row, 13, String.valueOf(wrap(financeSpecialOrder.getFqIncome())));
                }

            }
            if (page.isHasNext()) {
                page.setPageNo(page.getNextPage());
            } else {
                break;
            }
        }
        row = sheet.createRow(0);
        HSSFCellUtil.createCell(row, 0, "合计");
        HSSFCellUtil.createCell(row, 1, String.valueOf(innCount));
        HSSFCellUtil.createCell(row, 8, String.valueOf(channelAmount));
        HSSFCellUtil.createCell(row, 9, String.valueOf(channelDebit));
        HSSFCellUtil.createCell(row, 10, String.valueOf(fqExtra));
        HSSFCellUtil.createCell(row, 11, String.valueOf(innDebit));
        HSSFCellUtil.createCell(row, 12, String.valueOf(fqDebit));
        HSSFCellUtil.createCell(row, 13, String.valueOf(fqIncome));
    }


    private void createRefundSheet(HSSFWorkbook workbook, String settlementTime, Integer channelId, HSSFCellStyle boldCellStyle, HSSFCellStyle normalCellStyle) {

        HSSFSheet refundSheet = workbook.createSheet("退款订单表");
        refundSheet.autoSizeColumn(8, true);
        refundSheet.setDefaultColumnWidth(18);
        createRefundTitleCells(refundSheet);
        ExcelUtil.renderedExcel(refundSheet, boldCellStyle, 0);
        createRefundContentCells(refundSheet, settlementTime, channelId);
        ExcelUtil.renderedExcel(refundSheet, normalCellStyle, 0);

    }

    private void createRefundTitleCells(HSSFSheet sheet) {

        HSSFRow row = sheet.createRow(1);
        HSSFCellUtil.createCell(row, 0, "订单模式");
        HSSFCellUtil.createCell(row, 1, "订单号");
        HSSFCellUtil.createCell(row, 2, "客栈名称");
        HSSFCellUtil.createCell(row, 3, "预订人");
        HSSFCellUtil.createCell(row, 4, "手机号码");
        HSSFCellUtil.createCell(row, 5, "房型");
        HSSFCellUtil.createCell(row, 6, "住离日期");
        HSSFCellUtil.createCell(row, 7, "产生周期");
        HSSFCellUtil.createCell(row, 8, "分销商订单金额");
        HSSFCellUtil.createCell(row, 9, "分销商扣退款金额");
        HSSFCellUtil.createCell(row, 10, "番茄总调价");
        HSSFCellUtil.createCell(row, 11, "客栈退款金额");
        HSSFCellUtil.createCell(row, 12, "番茄退佣金收入");
        HSSFCellUtil.createCell(row, 13, "往来状态");
        HSSFCellUtil.createCell(row, 14, "番茄退往来款");
    }

    private String getContact(Short contact) {
        if (StringUtils.isBlank(String.valueOf(contact))) {
            return "—";
        }
        if (String.valueOf(contact).equals("1")) {
            return "后期(挂)";
        }
        if (String.valueOf(contact).equals("2")) {
            return "本期(平)";
        } else {

            return "—";
        }
    }

    private void createRefundContentCells(HSSFSheet sheet, String settlementTime, Integer channelId) {

        Integer innCount = 0;
        BigDecimal channelAmount = new BigDecimal(0);
        BigDecimal channelFefund = new BigDecimal(0);
        BigDecimal fqExtra = new BigDecimal(0);
        BigDecimal innRefund = new BigDecimal(0);
        BigDecimal fqRefund = new BigDecimal(0);
        BigDecimal fqCon = new BigDecimal(0);
        Page<FinanceSpecialOrder> page = new Page<>(PAGE_SIZE);
        page.setOrder("desc");
        page.setOrderBy("inn_name");
        HSSFRow row;
        int rowIndex = 2;
        while (true) {
            page = financeSpecialOrderDao.findFinanceSpecialOrder(page, FinanceSpecialOrder.STATUS_KEY_REFUND, null, channelId, settlementTime, null, null, null);
            List result = page.getResult();
            if (CollectionUtils.isNotEmpty(result)) {
                for (Object o : result) {
                    FinanceSpecialOrder financeSpecialOrder = (FinanceSpecialOrder) o;
                    row = sheet.createRow(rowIndex++);
                    innCount++;
                    channelAmount = channelAmount.add(wrap(financeSpecialOrder.getFinanceParentOrder().getTotalAmount()));
                    channelFefund = channelFefund.add(wrap(financeSpecialOrder.getChannelRefund()));
                    fqExtra = fqExtra.add(wrap(financeSpecialOrder.getFinanceParentOrder().getExtraPrice()));
                    innRefund = innRefund.add(wrap(financeSpecialOrder.getInnRefund()));
                    fqRefund = fqRefund.add(wrap(financeSpecialOrder.getFqRefundCommission()));
                    fqCon = fqCon.add(wrap(financeSpecialOrder.getFqRefundContacts()));

                    HSSFCellUtil.createCell(row, 0, priceStrategyName(financeSpecialOrder.getFinanceParentOrder().getPriceStrategy()));
                    HSSFCellUtil.createCell(row, 1, financeSpecialOrder.getFinanceParentOrder().getChannelOrderNo());
                    HSSFCellUtil.createCell(row, 2, financeSpecialOrder.getFinanceParentOrder().getInnName());
                    HSSFCellUtil.createCell(row, 3, financeSpecialOrder.getFinanceParentOrder().getUserName());
                    HSSFCellUtil.createCell(row, 4, financeSpecialOrder.getFinanceParentOrder().getContact());
                    HSSFCellUtil.createCell(row, 5, financeSpecialOrder.getFinanceParentOrder().getChannelRoomTypeName());
                    HSSFCellUtil.createCell(row, 6, financeSpecialOrder.getFinanceParentOrder().getCheckDate());//住离日期
                    HSSFCellUtil.createCell(row, 7, financeSpecialOrder.getFinanceParentOrder().getProduceTime());//产生日期
                    HSSFCellUtil.createCell(row, 8, String.valueOf(wrap(financeSpecialOrder.getFinanceParentOrder().getTotalAmount())));//分销商订单金额
                    HSSFCellUtil.createCell(row, 9, String.valueOf(wrap(financeSpecialOrder.getChannelRefund())));
                    HSSFCellUtil.createCell(row, 10, String.valueOf(wrap(financeSpecialOrder.getFinanceParentOrder().getExtraPrice())));
                    HSSFCellUtil.createCell(row, 11, String.valueOf(wrap(financeSpecialOrder.getInnRefund())));
                    HSSFCellUtil.createCell(row, 12, String.valueOf(wrap(financeSpecialOrder.getFqRefundCommission())));
                    HSSFCellUtil.createCell(row, 13, String.valueOf(getContact(financeSpecialOrder.getContactsStatus())));
                    HSSFCellUtil.createCell(row, 14, String.valueOf(wrap(financeSpecialOrder.getFqRefundContacts())));
                }

            }
            if (page.isHasNext()) {
                page.setPageNo(page.getNextPage());
            } else {
                break;
            }
        }
        row = sheet.createRow(0);
        HSSFCellUtil.createCell(row, 0, "合计");
        HSSFCellUtil.createCell(row, 1, String.valueOf(innCount));
        HSSFCellUtil.createCell(row, 8, String.valueOf(channelAmount));
        HSSFCellUtil.createCell(row, 9, String.valueOf(channelFefund));
        HSSFCellUtil.createCell(row, 10, String.valueOf(fqExtra));
        HSSFCellUtil.createCell(row, 11, String.valueOf(innRefund));
        HSSFCellUtil.createCell(row, 12, String.valueOf(fqRefund));
        HSSFCellUtil.createCell(row, 14, String.valueOf(fqCon));
    }

    private void createReplenishmentSheet(HSSFWorkbook workbook, String settlementTime, Integer channelId, HSSFCellStyle boldCellStyle, HSSFCellStyle normalCellStyle) {
        HSSFSheet replenishmentSheet = workbook.createSheet("补款订单表");
        replenishmentSheet.autoSizeColumn(8, true);
        replenishmentSheet.setDefaultColumnWidth(18);
        createReplenishmentTitleCells(replenishmentSheet);
        ExcelUtil.renderedExcel(replenishmentSheet, boldCellStyle, 0);
        createReplenishmentContentCells(replenishmentSheet, settlementTime, channelId);
        ExcelUtil.renderedExcel(replenishmentSheet, normalCellStyle, 0);
    }

    private void createReplenishmentTitleCells(HSSFSheet sheet) {

        HSSFRow row = sheet.createRow(1);
        HSSFCellUtil.createCell(row, 0, "订单模式");
        HSSFCellUtil.createCell(row, 1, "订单号");
        HSSFCellUtil.createCell(row, 2, "客栈名称");
        HSSFCellUtil.createCell(row, 3, "预订人");
        HSSFCellUtil.createCell(row, 4, "手机号码");
        HSSFCellUtil.createCell(row, 5, "房型");
        HSSFCellUtil.createCell(row, 6, "住离日期");
        HSSFCellUtil.createCell(row, 7, "产生周期");
        HSSFCellUtil.createCell(row, 8, "客栈订单金额");
        HSSFCellUtil.createCell(row, 9, "客栈结算金额");
        HSSFCellUtil.createCell(row, 10, "番茄补款金额");


    }

    private void createReplenishmentContentCells(HSSFSheet sheet, String settlementTime, Integer channelId) {
        Integer innCount = 0;
        BigDecimal innOrder = new BigDecimal(0);
        BigDecimal inn = new BigDecimal(0);
        BigDecimal fqReplenishment = new BigDecimal(0);
        Page<FinanceSpecialOrder> page = new Page<>(PAGE_SIZE);
        page.setOrder("desc");
        page.setOrderBy("inn_name");
        HSSFRow row;
        int rowIndex = 2;
        while (true) {
            page = financeSpecialOrderDao.findFinanceSpecialOrder(page, FinanceSpecialOrder.STATUS_KEY_REPLENISHMENT, null, channelId, settlementTime, null, null, null);
            List result = page.getResult();
            if (CollectionUtils.isNotEmpty(result)) {
                for (Object o : result) {
                    FinanceSpecialOrder financeSpecialOrder = (FinanceSpecialOrder) o;
                    row = sheet.createRow(rowIndex++);
                    innCount++;
                    innOrder = innOrder.add(wrap(financeSpecialOrder.getFinanceParentOrder().getInnAmount()));
                    inn = inn.add(wrap(financeSpecialOrder.getFinanceParentOrder().getInnSettlementAmount()));
                    fqReplenishment = fqReplenishment.add(wrap(financeSpecialOrder.getFqReplenishment()));


                    HSSFCellUtil.createCell(row, 0, priceStrategyName(financeSpecialOrder.getFinanceParentOrder().getPriceStrategy()));
                    HSSFCellUtil.createCell(row, 1, financeSpecialOrder.getFinanceParentOrder().getChannelOrderNo());
                    HSSFCellUtil.createCell(row, 2, financeSpecialOrder.getFinanceParentOrder().getInnName());
                    HSSFCellUtil.createCell(row, 3, financeSpecialOrder.getFinanceParentOrder().getUserName());
                    HSSFCellUtil.createCell(row, 4, financeSpecialOrder.getFinanceParentOrder().getContact());
                    HSSFCellUtil.createCell(row, 5, financeSpecialOrder.getFinanceParentOrder().getChannelRoomTypeName());
                    HSSFCellUtil.createCell(row, 6, financeSpecialOrder.getFinanceParentOrder().getCheckDate());//住离日期
                    HSSFCellUtil.createCell(row, 7, financeSpecialOrder.getFinanceParentOrder().getProduceTime());//产生日期
                    HSSFCellUtil.createCell(row, 8, String.valueOf(wrap(financeSpecialOrder.getFinanceParentOrder().getInnAmount())));//分销商订单金额
                    HSSFCellUtil.createCell(row, 9, String.valueOf(wrap(financeSpecialOrder.getFinanceParentOrder().getInnSettlementAmount())));
                    HSSFCellUtil.createCell(row, 10, String.valueOf(wrap(financeSpecialOrder.getFqReplenishment())));

                }

            }
            if (page.isHasNext()) {
                page.setPageNo(page.getNextPage());
            } else {
                break;
            }
        }
        row = sheet.createRow(0);
        HSSFCellUtil.createCell(row, 0, "合计");
        HSSFCellUtil.createCell(row, 1, String.valueOf(innCount));
        HSSFCellUtil.createCell(row, 8, String.valueOf(innOrder));
        HSSFCellUtil.createCell(row, 9, String.valueOf(inn));
        HSSFCellUtil.createCell(row, 10, String.valueOf(fqReplenishment));
    }

    private void createManualOrderSheet(HSSFWorkbook workbook, String settlementTime, Integer channelId, HSSFCellStyle boldCellStyle, HSSFCellStyle normalCellStyle) {
        HSSFSheet sheet = workbook.createSheet("无订单赔付表");
        sheet.autoSizeColumn(8, true);
        sheet.setDefaultColumnWidth(18);
        createManualOrderTitleCells(sheet);
        ExcelUtil.renderedExcel(sheet, boldCellStyle, 0);
        Map<String, Object> map = createManualOrderContentCells(sheet, settlementTime, channelId);
        createManualOrderTotalCells(sheet, map);
        ExcelUtil.renderedExcel(sheet, normalCellStyle, 0);
    }

    private Map<String, Object> createManualOrderContentCells(HSSFSheet sheet, String settlementTime, Integer channelId) {

        Page<FinanceManualOrder> page = new Page<>(PAGE_SIZE);
        int index = 2;//内容从第二行开始
        Integer contentNo = 0;//内容序号
        BigDecimal totalRefund = new BigDecimal(0);
        HSSFRow row;
        while (true) {
            page = financeManualOrderDao.list(page, channelId, settlementTime, null);
            List result = page.getResult();
            if (CollectionUtils.isNotEmpty(result)) {
                for (Object o : result) {
                    FinanceManualOrder manualOrder = (FinanceManualOrder) o;
                    row = sheet.createRow(index++);
                    createCell(row, 0, ++contentNo);//序号
                    HSSFCellUtil.createCell(row, 1, manualOrder.getOrderId());//订单号
                    BigDecimal refund = manualOrder.getRefund() == null ? new BigDecimal(0) : manualOrder.getRefund();
                    totalRefund = totalRefund.add(refund);
                    createCell(row, 2, refund.doubleValue());//赔付金额
                    HSSFCellUtil.createCell(row, 3, manualOrder.getRemark());//备注
                }
            }
            if (page.isHasNext()) {
                page.setPageNo(page.getNextPage());
            } else {
                break;
            }
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("totalNo", contentNo);
        map.put("totalAmount", totalRefund);
        return map;
    }

    /**
     * 创建无订单赔付标题行
     */
    private void createManualOrderTitleCells(HSSFSheet sheet) {
        HSSFRow titleRow = sheet.createRow(1);
        HSSFCellUtil.createCell(titleRow, 0, "序号号");
        HSSFCellUtil.createCell(titleRow, 1, "订单号");
        HSSFCellUtil.createCell(titleRow, 2, "分销商扣番茄金额");
        HSSFCellUtil.createCell(titleRow, 3, "备注");
    }

    /**
     * 创建无订单赔付合计行
     *
     * @param sheet
     */
    private void createManualOrderTotalCells(HSSFSheet sheet, Map<String, Object> map) {
        HSSFRow row = sheet.createRow(0);
        HSSFCellUtil.createCell(row, 0, "合计：" + map.get("totalNo"));
        createCell(row, 2, new Double(map.get("totalAmount").toString()));
    }

    private FileOutputStream getFileOutput(HttpServletRequest request, String settlementTime, String channelName) throws FileNotFoundException {
        String realPath = request.getSession().getServletContext().getRealPath("/") + "download";
        String fileName = settlementTime + "(" + channelName + ")" + ".xls";
        File file = new File(realPath + "/" + fileName);
        return new FileOutputStream(file);
    }

    private void createCell(HSSFRow row, int column, double value) {
        HSSFCell cell = row.createCell(column, HSSFCell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
    }

    /**
     * 导出番茄暂收客栈详情
     */
    public void exportFqTemp(HttpServletRequest request, Integer channelId, String settlementTime, String channelName) {
        List<Map<String, Object>> innChannelSettlementList = financeInnChannelSettlementDao.exportFqTempInn(channelId, settlementTime);
        FileOutputStream fileOutputStream = null;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            String sheetName = "番茄暂收【" + channelName + "】";
            HSSFSheet sheet = ExcelUtil.getSheet(workbook, sheetName, 0, 1, 8, 18);
            List<String> headerNames = Arrays.asList("城市", "客栈名称", "订单个数", "订单总金额", "分销商应结金额", "番茄暂收");
            ExcelUtil.createSheetHeader(headerNames, sheet, 0);
            fillFqTempData(innChannelSettlementList, sheet);
            String fileName = channelName + "番茄暂收" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls";
            fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
            // 导出Excel
            String realPath = request.getSession().getServletContext().getRealPath("/") + "download";
            File file = new File(realPath + "/" + fileName);
            fileOutputStream = new FileOutputStream(file);
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
     * 填充番茄暂收数据
     *
     * @param innChannelSettlementList
     * @param sheet
     */
    public void fillFqTempData(List<Map<String, Object>> innChannelSettlementList, HSSFSheet sheet) {
        if (CollectionsUtil.isNotEmpty(innChannelSettlementList)) {
            int i = 1;
            for (Map<String, Object> map : innChannelSettlementList) {
                HSSFRow row = sheet.createRow(i);
                HSSFCell cell = row.createCell(0);
                cell.setCellValue(String.valueOf(map.get("region")));
                cell = row.createCell(1);
                cell.setCellValue(String.valueOf(map.get("innname")));
                cell = row.createCell(2);
                cell.setCellValue(String.valueOf(map.get("count")));
                cell = row.createCell(3);
                cell.setCellValue(String.valueOf(map.get("total")));
                cell = row.createCell(4);
                cell.setCellValue(String.valueOf(map.get("channel")));
                cell = row.createCell(5);
                cell.setCellValue(String.valueOf(map.get("temp")));
            }
        }
    }

}
