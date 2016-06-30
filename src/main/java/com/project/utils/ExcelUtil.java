package com.project.utils;

import com.project.entity.finance.FinanceInnSettlement;
import com.project.entity.finance.FinanceInnSettlementInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtil<T> {


    @SuppressWarnings({"unchecked", "deprecation", "rawtypes"})
    public void exportExcel(String title, String[] headers, List<T> dataset, HttpServletRequest req) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            sheet.setColumnWidth(i, headers[i].getBytes().length * 2 * 120);
            cell.setCellValue(text);
        }

        // 遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        int index = 0;
        try {
            while (it.hasNext()) {
                index++;
                row = sheet.createRow(index);
                T t = (T) it.next();
                // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < fields.length; i++) {
                    HSSFCell cell = row.createCell(i);
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
                    cell.setCellValue(new HSSFRichTextString(null == value ? "" : value.toString()));
                }
            }

            String filePath = req.getSession().getServletContext().getRealPath("/") + "download";
            File file = new File(filePath + "/inn_list.xls");
            OutputStream ouputStream = new FileOutputStream(file);
            workbook.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 客栈结算详情集合生成Excel文件名称
     *
     * @param financeInnSettlementList 客栈结算详情集合
     * @return Excel文件名称
     */
    public static String getFinanceExcelName(List<FinanceInnSettlement> financeInnSettlementList) {
        FinanceInnSettlement financeInnSettlement = financeInnSettlementList.get(0);
        String settlementTime = financeInnSettlement.getSettlementTime();
        if (financeInnSettlementList.size() == 1) {
            FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
            String innName = financeInnSettlementInfo.getInnName();
            return settlementTime + "_" + innName + "_结算汇总表";
        } else {
            return settlementTime + "客栈结算汇总表";
        }
    }

    /**
     * 根据客栈名称获取Excel的sheet名称
     * 1、长度超过31自动截取，后面的内容舍弃
     * 2、过滤特殊字符：\ / ? * [ ]
     *
     * @param innName
     * @return
     */
    public static String getSheetNameByInnName(String innName) {
        if (StringUtils.isNotBlank(innName)) {
            String regEx = "[\\[\\]\\*\\?\\\\/:]";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(innName);
            // 将特殊字符替换为空字符串
            innName = matcher.replaceAll("").trim();
            int length = innName.length();
            // sheet最大支持31位长度，因为要添加客栈ID，格式为（xxxxx），所以最大长度变为24
            int allowLength = 24;
            if (length >= allowLength) {
                innName = innName.substring(0, allowLength);
            }
            return innName;
        }
        return null;
    }

    /**
     * 按照指定样式渲染Excel单元格
     *
     * @param sheet       表格对象
     * @param cellStyle   单元格样式对象
     * @param beginRowNum 开始渲染的行数
     */
    public static void renderedExcel(HSSFSheet sheet, HSSFCellStyle cellStyle, int beginRowNum) {
        for (int i = beginRowNum; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow hr = sheet.getRow(i);
            if (hr == null) {
                return;
            }
            for (int k = 0; k < hr.getLastCellNum(); k++) {
                HSSFCell hc = hr.getCell(k);
                if (hc != null) {
                    hc.setCellStyle(cellStyle);
                }
            }
        }
    }
    /**
     * 按照指定样式渲染Excel单元格
     *
     * @param sheet       表格对象
     * @param cellStyle   单元格样式对象
     * @param beginRowNum 开始渲染的行数
     */
    public static void renderedExcelCell(HSSFSheet sheet, HSSFCellStyle cellStyle, int beginRowNum,int beginCell,int endCell) {
        for (int i = beginRowNum; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow hr = sheet.getRow(i);
            if (hr == null) {
                return;
            }
            for (int k = beginCell; k <= endCell; k++) {
                HSSFCell hc = hr.getCell(k);
                if (hc != null) {
                    hc.setCellStyle(cellStyle);
                }
            }
        }
    }

    /**
     * 根据客栈结算对象，构建出账核算EXCEL中的客栈银行卡支付信息
     *
     * @param financeInnSettlement 客栈结算对象
     * @return 银行卡支付信息
     */
    public static String buildBankInfo(FinanceInnSettlement financeInnSettlement) {
        StringBuilder bankInfo = new StringBuilder();
        String defaultString = "暂无";
        FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankType(), defaultString));
        bankInfo.append(":");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankAccount(), defaultString));
        bankInfo.append("/");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankCode(), defaultString));
        bankInfo.append("\r\n");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankProvince(), defaultString));
        bankInfo.append("/");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankCity(), defaultString));
        bankInfo.append("\r\n");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankName(), defaultString));
        bankInfo.append("(");
        bankInfo.append(StringUtils.defaultString(financeInnSettlementInfo.getBankRegion(), defaultString));
        bankInfo.append(")");
        return bankInfo.toString();
    }

    /**
     * 构建sheet
     *
     * @param sheetName
     * @return
     */
    public static HSSFSheet getSheet( HSSFWorkbook workbook,String sheetName, Integer bold, Integer normal, Integer sizeColumn, Integer defaultColumn) {
        HSSFCellStyle normalCellStyle = ExcelExportUtil.getNormalCellStyle(workbook);
        HSSFCellStyle boldCellStyle = ExcelExportUtil.getBoldCellStyle(workbook);
        //构建表
        HSSFSheet totalSheet = workbook.createSheet(sheetName);
        renderedExcel(totalSheet, boldCellStyle, bold);
        renderedExcel(totalSheet, normalCellStyle, normal);
        totalSheet.autoSizeColumn(sizeColumn, true);
        totalSheet.setDefaultColumnWidth(defaultColumn);
        return totalSheet;
    }

    /**
     * 创建表头
     *
     * @param headerNames
     * @param sheet
     * @param index
     */
    public static void createSheetHeader(List<String> headerNames, HSSFSheet sheet, int index) {
        if (CollectionsUtil.isNotEmpty(headerNames)) {
            HSSFRow row = sheet.createRow(index);
            for (int i = 0; i < headerNames.size(); i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(headerNames.get(i));
            }
        }
    }
}

