package com.project.service.finance;

import com.project.dao.finance.FinanceInnSettlementDao;
import com.project.entity.finance.FinanceInnChannelSettlement;
import com.project.entity.finance.FinanceInnSettlement;
import com.project.entity.finance.FinanceInnSettlementInfo;
import com.project.utils.CollectionsUtil;
import com.project.utils.ExcelExportUtil;
import com.project.utils.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/3/21.
 */
@Service("specialInnService")
@Transactional
public class FinanceOutNormalExportServiceImpl implements FinanceOutNormalExportService {
    @Resource
    private FinanceInnSettlementDao financeInnSettlementDao;

    @Override
    public void createFinanceExcel(HttpServletRequest request, List<FinanceInnSettlement> financeInnSettlementList, List<List<FinanceInnChannelSettlement>> financeInnChannelSettlementList) throws Exception {
        if (CollectionsUtil.isEmpty(financeInnSettlementList)) {
            throw new RuntimeException("当前结算月份没有客栈结算数据");
        }
        if (CollectionsUtil.isEmpty(financeInnChannelSettlementList)) {
            throw new Exception("当月结算月份没有渠道结算的订单数据");
        }
//        OutputStream outputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
            HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
            // 第一个sheet是总表，统计客栈全部数据
            HSSFSheet totalSheet = workbook.createSheet("总表");
            // 构建总表
            fillTotalSheetData(totalSheet, financeInnSettlementList, normalCellStyle, boldCellStyle);
            //构建总表文件
            String fileTotalName = ExcelUtil.getFinanceExcelName(financeInnSettlementList) + "(正常结算总表)V" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls";
            createExcelFile(request, fileTotalName, workbook);

            List<List<FinanceInnSettlement>> listList = CollectionsUtil.splitList(financeInnSettlementList, 800);

            if (CollectionsUtil.isNotEmpty(listList)) {
                int k = 0;
                HSSFWorkbook workbookInn;
                HSSFCellStyle normalCellStyleInn;
                HSSFCellStyle boldCellStyleInn;
                for (List<FinanceInnSettlement> list : listList) {
                    String fileName = null;
                    k++;
                    workbookInn = new HSSFWorkbook();
                    //以分割后的list为单位遍历
                    normalCellStyleInn = ExcelExportUtil.getNormalCellStyle(workbookInn);
                    boldCellStyleInn = ExcelExportUtil.getBoldCellStyle(workbookInn);
                    for (int i = 0; i < list.size(); i++) {
                        FinanceInnSettlement financeInnSettlement = list.get(i);
                        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
                        String innName = financeInnSettlementInfo.getInnName();
                        innName = ExcelUtil.getSheetNameByInnName(innName);
                        HSSFSheet sheet = workbookInn.createSheet(innName + "(" + financeInnSettlementInfo.getId() + ")");
                        sheet.autoSizeColumn(8, true);
                        sheet.setDefaultColumnWidth(18);
                        // 创建表头
                        createExcelTitle(sheet, financeInnSettlement, boldCellStyleInn);
                        List<FinanceInnChannelSettlement> innChannelList = financeInnChannelSettlementList.get(i);
                        fillExcelData(sheet, innChannelList, normalCellStyleInn);
                        fileName = ExcelUtil.getFinanceExcelName(list) + "[正常结算-(" + k + ")]V" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls";
                    }
                    createExcelFile(request, fileName, workbookInn);
                }

            }


        } catch (Exception e) {
            throw new Exception("表格导出时出错!", e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                throw new Exception("表格导出时出错!", e);
            }
        }
    }

    /**
     * 构建execl文件
     *
     * @param request
     * @param workbook
     * @throws Exception
     */
    public void createExcelFile(HttpServletRequest request, String fileName, HSSFWorkbook workbook) throws Exception {
        FileOutputStream fileOutputStream = null;
        try {
            fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
            // 导出Excel
            String realPath = request.getSession().getServletContext().getRealPath("/") + "download";
            File file = new File(realPath + "/" + fileName);
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            throw new Exception("表格导出时出错!", e);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    /**
     * 创建出账核对的总表
     *
     * @param totalSheet               总表的sheet
     * @param financeInnSettlementList 客栈结算对象集合
     * @param normalCellStyle          普通单元格样式
     * @param boldCellStyle            加粗单元格样式
     */
    private void fillTotalSheetData(HSSFSheet totalSheet, List<FinanceInnSettlement> financeInnSettlementList, HSSFCellStyle normalCellStyle, HSSFCellStyle boldCellStyle) {
        FinanceInnSettlement financeInnSettlement = financeInnSettlementList.get(0);
        totalSheet.autoSizeColumn(8, true);
        totalSheet.setDefaultColumnWidth(18);
        // 创建表头
        buildTotalSheetTitle(totalSheet, financeInnSettlement);
        ExcelUtil.renderedExcel(totalSheet, boldCellStyle, 0);
        buildTotalSheetData(totalSheet, financeInnSettlementList);
        ExcelUtil.renderedExcel(totalSheet, normalCellStyle, 2);
    }

    /**
     * 填充出账核算总表表头
     *
     * @param totalSheet
     * @param financeInnSettlement
     */
    private void buildTotalSheetTitle(HSSFSheet totalSheet, FinanceInnSettlement financeInnSettlement) {
        List<Map<String, Object>> dataMapList = financeInnSettlementDao.selectFinanceInnSettlementCount(financeInnSettlement.getSettlementTime(), null, "normal");
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
        BigDecimal channelamount = (BigDecimal) dataMap.get("channelamount");
        // 第一行展示合计内容
        HSSFRow row = totalSheet.createRow(0);
        HSSFCell cell = row.createCell(1);
        cell.setCellValue("合计:      " + innCount);
        cell = row.createCell(4);
        cell.setCellValue(orders);
        cell = row.createCell(5);
        cell.setCellValue(String.valueOf(total));
        cell = row.createCell(6);
        cell.setCellValue(String.valueOf(channelamount));
        cell = row.createCell(7);
        cell.setCellValue(String.valueOf(channels));
        cell = row.createCell(8);
        cell.setCellValue(String.valueOf(fqs));
        cell = row.createCell(9);
        cell.setCellValue(String.valueOf(inns));
        // 第二行展示表头
        row = totalSheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("城市");
        cell = row.createCell(1);
        cell.setCellValue("客栈名称");
        cell = row.createCell(2);
        cell.setCellValue("客栈id");
        cell = row.createCell(3);
        cell.setCellValue("收款信息(银行卡)");
        cell = row.createCell(4);
        cell.setCellValue("订单总数(个)");
        cell = row.createCell(5);
        cell.setCellValue("客栈订单总金额");
        cell = row.createCell(6);
        cell.setCellValue("分销商订单总金额");
        cell = row.createCell(7);
        cell.setCellValue("分销商结算金额");
        cell = row.createCell(8);
        cell.setCellValue("番茄收入金额");
        cell = row.createCell(9);
        cell.setCellValue("客栈结算金额");
        cell = row.createCell(10);
        cell.setCellValue("联系电话");
    }


    /**
     * 填充出账核算客栈结算总表数据
     *
     * @param totalSheet
     * @param financeInnSettlementList
     */
    private void buildTotalSheetData(HSSFSheet totalSheet, List<FinanceInnSettlement> financeInnSettlementList) {
        for (int i = 0; i < financeInnSettlementList.size(); i++) {
            FinanceInnSettlement financeInnSettlement = financeInnSettlementList.get(i);
            HSSFRow row = totalSheet.createRow(i + 2);
            HSSFCell cell = row.createCell(0);
            FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
            cell.setCellValue(financeInnSettlementInfo.getRegionName());
            cell = row.createCell(1);
            cell.setCellValue(financeInnSettlementInfo.getInnName());

            cell = row.createCell(2);
            cell.setCellValue( financeInnSettlementInfo.getId());

            cell = row.createCell(3);
            cell.setCellValue(ExcelUtil.buildBankInfo(financeInnSettlement));
            cell = row.createCell(4);
            cell.setCellValue(financeInnSettlement.getTotalOrder());
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(financeInnSettlement.getTotalAmount()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(financeInnSettlement.getChannelAmount()));
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(financeInnSettlement.getChannelSettlementAmount()));
            cell = row.createCell(8);
            cell.setCellValue(String.valueOf(financeInnSettlement.getFqSettlementAmount()));
            cell = row.createCell(9);
            String status = financeInnSettlement.getIsArrears();
            if (StringUtils.isNotBlank(status)) {
                if (status.equals("0")) {
                    cell.setCellValue(String.valueOf(financeInnSettlement.getAfterPaymentAmount()));
                } else {
                    cell.setCellValue(String.valueOf(financeInnSettlement.getAfterArrearsAmount()));
                }
            }
            cell = row.createCell(10);
            cell.setCellValue(financeInnSettlementInfo.getInnContact());
        }
    }

    private void createExcelTitle(HSSFSheet sheet, FinanceInnSettlement financeInnSettlement, HSSFCellStyle boldCellStyle) {
        // 第一行展示客栈名称+客栈联系电话+结算周期
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 0, 0, 7);
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(1, 1, 0, 7);

        sheet.addMergedRegion(cellRangeAddress1);
        sheet.addMergedRegion(cellRangeAddress2);
        // 第一行展示客栈信息
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue(buildInnInfo(financeInnSettlement));

        // 第二行展示客栈的银行卡信息
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(ExcelUtil.buildBankInfo(financeInnSettlement));

        // 第三行展示合计数据
        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("合计");
        cell = row.createCell(2);
        cell.setCellValue("总个数:" + financeInnSettlement.getTotalOrder());
        cell = row.createCell(5);
        cell.setCellValue("总金额:" + financeInnSettlement.getInnSettlementAmount());
        // 第四行展示标题
        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("分销商");
        cell = row.createCell(1);
        cell.setCellValue("订单总个数");
        cell = row.createCell(2);
        cell.setCellValue("客栈订单总金额");
        cell = row.createCell(3);
        cell.setCellValue("分销商订单总金额");
        cell = row.createCell(4);
        cell.setCellValue("分销商结算金额");
        cell = row.createCell(5);
        cell.setCellValue("番茄收入金额");
        cell = row.createCell(6);
        cell.setCellValue("客栈实际结算金额");

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


    private void fillExcelData(HSSFSheet sheet, List<FinanceInnChannelSettlement> channelSettlementsList, HSSFCellStyle normalCellStyle) {
        for (int i = 0; i < channelSettlementsList.size(); i++) {
            FinanceInnChannelSettlement financeInnChannelSettlement = channelSettlementsList.get(i);
            HSSFRow row = sheet.createRow(i + 4);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(financeInnChannelSettlement.getChannelName());
            cell = row.createCell(1);
            cell.setCellValue(financeInnChannelSettlement.getTotalOrder());
            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getTotalAmount()));
            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getChannelAmount()));
            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getChannelSettlementAmount()));
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getFqIncomeAmount()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(financeInnChannelSettlement.getInnSettlementAmount()));
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

}

