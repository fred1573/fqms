package com.project.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;

/**
 * Excel读取对象
 * Created by 番茄桑 on 2015/9/19.
 */
public class ExcelReaderUtils {
    /**
     * 根据HSSFCell类型设置数据
     *
     * @param cell
     * @return
     */
    public static String getCellFormatValue(Cell cell) {
        String cellValue = "";
        if (cell != null) {
            switch (cell.getCellType()) {
                // 数字
                case Cell.CELL_TYPE_NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        return DateFormatUtils.format(cell.getDateCellValue(), "yyyy-MM-dd");
                    } else {
                        double doubleValue = cell.getNumericCellValue();
                        long round = Math.round(doubleValue);
                        if (Double.parseDouble(round + ".0") == doubleValue) {
                            return String.valueOf(round);
                        } else {
                            return String.valueOf(doubleValue);
                        }
                    }
                    // 字符串
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue();
                // Boolean
                case Cell.CELL_TYPE_BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                // 公式
                case Cell.CELL_TYPE_FORMULA:
                    return cell.getCellFormula();
                default:
                    return null;
            }
        }
        return cellValue;
    }


    public static void main(String[] args) {
        File f = new File("D:\\work\\绿番茄\\tp淘宝_2015-07_结算单模板.xlsx");
        try {
            FileInputStream is = new FileInputStream(f);
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("行数：" + sheet.getLastRowNum());
            // System.out.println(childSheet.getPhysicalNumberOfRows());
            System.out.println("有行数" + sheet.getLastRowNum());
            for (int j = 0; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                // System.out.println(row.getPhysicalNumberOfCells());
                // System.out.println("有列数" + row.getLastCellNum());
                if (null != row) {
                    for (int k = 0; k < row.getLastCellNum(); k++) {
                        Cell cell = row.getCell(k);
                        if (null != cell) {
                            System.out.println(getCellFormatValue(cell));
                        } else {
                            System.out.print("-   ");
                        }
                    }
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
