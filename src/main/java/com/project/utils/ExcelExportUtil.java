package com.project.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.transformer.XLSTransformer;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;


import com.project.bean.excel.ExcelExportBean;
import com.project.bean.excel.ExcelSheetBean;
import com.project.common.Constants;
import com.project.utils.encode.RandomUtil;
import com.project.utils.time.DateUtil;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author mowei
 */
@SuppressWarnings("unchecked")
public class ExcelExportUtil {

    /**
     * 创建excel表格的表头列
     *
     * @param columnThemeName 表头每列的标题名的字符串数组
     * @param sheet           HSSFSheet sheet
     * @param hcs
     */
    public static void createTableHeader(String[] columnThemeName, HSSFSheet sheet, HSSFCellStyle hcs) {
        HSSFHeader header = sheet.getHeader();
        header.setCenter(sheet.getSheetName());
        HSSFRow headerRow = sheet.createRow(0);
        if (columnThemeName != null && columnThemeName.length > 0) {
            for (int i = 0; i < columnThemeName.length; i++) {
                HSSFCell headerCell = headerRow.createCell(i);
                headerCell.setCellStyle(hcs);
                headerCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                headerCell.setCellValue(columnThemeName[i]);
            }
        }
    }

    /**
     * 创建行
     *
     * @param sheet
     * @param cells
     * @param rowIndex
     */
    public static void createTableRow(HSSFSheet sheet, List<ExcelExportBean> cells, int rowIndex, HSSFCellStyle hcs) {
        //创建第rowIndex行
        HSSFRow row = sheet.createRow(rowIndex);
        if (cells != null && cells.size() > 0) {
            for (int i = 0; i < cells.size(); i++) {
                //创建第i个单元格
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(hcs);
                ExcelExportBean eeb = cells.get(i);
                if (eeb.getValue() != null && StringUtils.isNotBlank(eeb.getValue().toString())) {
                    if (Constants.DATA_TYPE_DATE.equalsIgnoreCase(eeb.getCellType())) {
                        cell.setCellValue(DateUtil.format(DateUtil.parse(eeb.getValue().toString(), "yyyy-MM-dd"), "yyyy-MM-dd"));
                    } else if (Constants.DATA_TYPE_DATE_TIME.equalsIgnoreCase(eeb.getCellType())) {
                        cell.setCellValue(DateUtil.format(DateUtil.parse(eeb.getValue().toString(), "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
                    } else if (Constants.DATA_TYPE_FLOAT.equalsIgnoreCase(eeb.getCellType())) {
                        if ("needNull".equalsIgnoreCase(eeb.getNeedNull()) && (int) Float.parseFloat(eeb.getValue().toString()) == 0) {
                            cell.setCellValue("");
                        } else {
                            cell.setCellValue(Float.parseFloat(eeb.getValue().toString()));
                        }
                    } else {
                        cell.setCellValue(eeb.getValue().toString());
                    }
                }
            }
        }
    }

    /**
     * 创建整个Excel表的sheet
     *
     * @param columnThemeName  excel标题行
     * @param propertySequence 排序规则
     * @param list             来自数据库的数据,里面装的是Domain
     * @param sheet            HSSFSheet
     * @param className
     * @param hcs              标题栏单元格样式
     * @param hcs2             内容单元格样式
     */
    @SuppressWarnings("rawtypes")
    public static void createExcelSheet(String[] columnThemeName, String[] propertySequence, List list, HSSFSheet sheet, String className, HSSFCellStyle hcs, HSSFCellStyle hcs2) throws Exception {
        createTableHeader(columnThemeName, sheet, hcs);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);
                List<ExcelExportBean> rowList = null;
                if (obj != null) {
                    try {
                        rowList = changeDomain2List(propertySequence, obj, className);
                        createTableRow(sheet, rowList, i + 1, hcs2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        sheet.setGridsPrinted(true);//网格打印可见
        HSSFFooter footer = sheet.getFooter();
        footer.setRight("Page " + HSSFFooter.page() + " of " +
                HSSFFooter.numPages());

    }

    /**
     * 按给定规则将domain的每项的值转化成List
     *
     * @param propertySequence 字段顺序
     * @param obj              domain实例
     * @param className        类的全路径名
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static List<ExcelExportBean> changeDomain2List(String[] propertySequence, Object obj, String className) throws Exception {
        List<ExcelExportBean> list = new ArrayList();
        Class c = null;
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e) {
            if (propertySequence != null && propertySequence.length > 0) {
                for (int j = 0; j < propertySequence.length; j++) {
                    String[] property = propertySequence[j].split("\\|");
                    String cellType = "";
                    String needNull = "";
                    ExcelExportBean eeb = new ExcelExportBean();
                    if (property != null && property.length > 1) {
                        cellType = property[1];
                        if (property.length > 2) {
                            needNull = property[2];
                        } else {
                            needNull = "";
                        }
                    } else {
                        cellType = "";
                    }
                    Map<String, Object> objMap = (Map<String, Object>) obj;
                    eeb.setValue(objMap.get(property[0]));
                    eeb.setCellType(cellType);
                    eeb.setNeedNull(needNull);
                    list.add(eeb);
                }
            }
            return list;
        }
        if (propertySequence != null && propertySequence.length > 0) {
            for (int j = 0; j < propertySequence.length; j++) {
                String[] property = propertySequence[j].split("\\|");
                String methodName = "";
                String cellType = "";
                String needNull = "";
                ExcelExportBean eeb = new ExcelExportBean();
                if (property != null && property.length > 1) {
                    cellType = property[1];
                    if (property.length > 2) {
                        needNull = property[2];
                    } else {
                        needNull = "";
                    }
                } else {
                    cellType = "";
                }
                methodName = "get" + property[0].substring(0, 1).toUpperCase() + property[0].substring(1);
                Method method = c.getMethod(methodName);
                Object returnObj = method.invoke(obj);
                eeb.setValue(returnObj != null ? returnObj.toString() : "");
                eeb.setCellType(cellType);
                eeb.setNeedNull(needNull);
                list.add(eeb);
            }
        }
        return list;
    }

    /**
     * 获取普通单元格样式
     *
     * @param workbook
     * @return
     */
    public static HSSFCellStyle getNormalCellStyle(HSSFWorkbook workbook) {
        HSSFFont font2 = workbook.createFont();
        font2.setFontName("宋体");
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        HSSFCellStyle hcs = workbook.createCellStyle();
        hcs.setFont(font2);
        hcs.setAlignment(CellStyle.ALIGN_CENTER);
        hcs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        hcs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        // 强制换行
        hcs.setWrapText(true);
        setBorder(hcs);
        return hcs;
    }

    /**
     * 获取加粗的单元格样式
     *
     * @param workbook
     * @return
     */
    public static HSSFCellStyle getBoldCellStyle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        HSSFCellStyle hcs = workbook.createCellStyle();
        hcs.setFont(font);
        hcs.setAlignment(CellStyle.ALIGN_CENTER);
        hcs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        hcs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        return hcs;
    }

    /**
     * 设置单元格边框
     * @param cellStyle
     */
    public static void setBorder(HSSFCellStyle cellStyle) {
        // 下边框
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        // 左边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        // 上边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 右边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
    }

    /**
     * 构建标题行单元格样式
     * @param workbook Excel对象
     * @return
     */
    public static HSSFCellStyle buildTitleCellStyle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFCellStyle hcs = workbook.createCellStyle();
        hcs.setFont(font);
        hcs.setAlignment(CellStyle.ALIGN_CENTER);
        hcs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        hcs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        hcs.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);// 设置背景色
        hcs.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        hcs.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        hcs.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        hcs.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        return hcs;
    }

    /**
     * 构建普通单元格样式
     * @param workbook Excel对象
     * @return
     */
    public static HSSFCellStyle buildNormalCellStyle(HSSFWorkbook workbook) {
        HSSFFont font2 = workbook.createFont();
        font2.setFontName("宋体");
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

        HSSFCellStyle hcs2 = workbook.createCellStyle();
        hcs2.setFont(font2);
        hcs2.setAlignment(CellStyle.ALIGN_CENTER);
        hcs2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        hcs2.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        hcs2.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        hcs2.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        hcs2.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        return hcs2;
    }

    /**
     * 执行导出
     *
     * @param response HttpServletResponse
     * @param fileName 导出文件名
     * @param list     内装有ExcelSheetBean对象
     * @throws Exception
     */
    public static void createExcel(HttpServletResponse response, String fileName, List<ExcelSheetBean> list) throws Exception {
        OutputStream os = null;
        try {
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.addHeader("Content-Disposition", new String(("attachment; filename=" + fileName + ".xls").getBytes("GBK"), "ISO-8859-1")); // 针对中文文件名
            os = response.getOutputStream();

            // 创建工作本
            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFFont font = workbook.createFont();
            font.setFontName("宋体");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle hcs = workbook.createCellStyle();
            hcs.setFont(font);
            hcs.setAlignment(CellStyle.ALIGN_CENTER);
            hcs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            hcs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            hcs.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);// 设置背景色
            hcs.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
            hcs.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
            hcs.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
            hcs.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

            HSSFFont font2 = workbook.createFont();
            font2.setFontName("宋体");
            font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

            HSSFCellStyle hcs2 = workbook.createCellStyle();
            hcs2.setFont(font2);
            hcs2.setAlignment(CellStyle.ALIGN_CENTER);
            hcs2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            hcs2.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
            hcs2.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
            hcs2.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
            hcs2.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

            if (list != null && list.size() > 0) {
                for (ExcelSheetBean esb : list) {
                    if (esb != null) {
                        HSSFSheet sheet = null;
                        if (esb.getSheetName() != null) {
                            sheet = workbook.createSheet(esb.getSheetName());
                        } else {
                            sheet = workbook.createSheet("");
                        }
                        sheet.autoSizeColumn(8, true);
                        sheet.setDefaultColumnWidth(18);

                        createExcelSheet(esb.getTableHeader(), esb.getPropertySequence(), esb.getList(), sheet, esb.getClassName(), hcs, hcs2);
                    }
                }
            }
            workbook.write(os);
//			JOptionPane.showMessageDialog(null, "表格已成功导出到 : " + fileName);
        } catch (Exception e) {
//			JOptionPane.showMessageDialog(null, "表格导出出错，错误信息 ：" + e);
            throw new Exception("表格导出时出错!", e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static String createExcelByJxls(Map beans, String xlsTemplateFileName, boolean doubleSum) throws Exception {
        XLSTransformer transformer = new XLSTransformer();
        String xlsFileName = Constants.SYS_RESOURCE_REPORT_TEMP_PATH + "report_"
                + RandomUtil.getRandomString(8) + ".xls";
        HSSFWorkbook workBook = null;
        try (InputStream is = new FileInputStream(xlsTemplateFileName)) {
            workBook = (HSSFWorkbook) transformer.transformXLS(is, beans);
            if (doubleSum) {
                /****caculate last total row****/
                HSSFSheet sheet = workBook.getSheetAt(0);
                HSSFRow firstRow = sheet.getRow(0);
                int columns = firstRow.getPhysicalNumberOfCells();
                double[] totals = new double[columns];
                for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        int first = row.getFirstCellNum();
                        int last = row.getLastCellNum();
                        for (int j = first; j < last; j++) {
                            HSSFCell cell = row.getCell(j);
                            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                totals[j] += cell.getNumericCellValue();
                            }

                        }

                    }
                }
                //last row
                HSSFRow lastRow = sheet.getRow(sheet.getLastRowNum() - 2);
                int first = lastRow.getFirstCellNum();
                int last = lastRow.getLastCellNum();
                for (int j = first; j < last; j++) {
                    HSSFCell cell = lastRow.getCell(j);
                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        cell.setCellValue(totals[j]);
                    }

                }
                /****caculate last total row  end****/
            }
            // force formula calculate
            workBook.setForceFormulaRecalculation(true);
            OutputStream os = new FileOutputStream(xlsFileName);
            workBook.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return xlsFileName;
    }

    /**
     * 用法：
     * 1.数据获取为对象形式，如：List<ProductInfo> list
     * 则需要使用对象属性：String[] propertySequence = { "createTime|datetime", ...};
     * 而且要指定class的包路径：String className = "com.vanceinfo.project.entity.product.ProductInfo";
     * 2.数据获取为map形式，如：List<Map<String,Object>> list
     * 则需要使用数据库字段属性：String[] propertySequence = { "create_time|datetime", ...};
     * class则为空
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        FileOutputStream fos = new FileOutputStream("D:\\123.xls");
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 0, 0, 5);
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(0, 0, 6, 10);

        CellRangeAddress cellRangeAddress3 = new CellRangeAddress(1, 1, 0, 1);
        CellRangeAddress cellRangeAddress4 = new CellRangeAddress(1, 1, 3, 4);
        CellRangeAddress cellRangeAddress5 = new CellRangeAddress(1, 1, 5, 7);
        CellRangeAddress cellRangeAddress6 = new CellRangeAddress(1, 1, 8, 10);

        sheet.addMergedRegion(cellRangeAddress1);
        sheet.addMergedRegion(cellRangeAddress2);
        sheet.addMergedRegion(cellRangeAddress3);
        sheet.addMergedRegion(cellRangeAddress4);
        sheet.addMergedRegion(cellRangeAddress5);
        sheet.addMergedRegion(cellRangeAddress6);

        Row row1 = sheet.createRow(0);
        Cell row1Cell1 = row1.createCell(0);
        row1Cell1.setCellValue("客栈名称(客栈电话)");
        Cell row1Cell2 = row1.createCell(6);
        row1Cell2.setCellValue("结算周期:2015-09");

        Row row2 = sheet.createRow(1);
        Cell row2Cell1 = row2.createCell(0);
        row2Cell1.setCellValue("开户类型:个人");
        Cell row2Cell2 = row2.createCell(3);
        row2Cell2.setCellValue("开户姓名:欧阳张三");
        Cell row2Cell3 = row2.createCell(5);
        row2Cell3.setCellValue("开户账号:6222023602067660415");
        Cell row2Cell4 = row2.createCell(8);
        row2Cell4.setCellValue("开户行:中国工商银行(广东省广州市广州大道支行)");

        Row row3 = sheet.createRow(2);
        Cell row3Cell1 = row3.createCell(0);
        row3Cell1.setCellValue("合计");
        Cell row3Cell2 = row3.createCell(2);
        row3Cell2.setCellValue("总个数:123");
        Cell row3Cell3 = row3.createCell(8);
        row3Cell3.setCellValue("总金额:20000.56");


        workbook.write(fos);
        fos.close();
    }

}
