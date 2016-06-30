package com.project.service.excel;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * @author yuneng.huang on 2016/6/15.
 */
public interface ExcelService {

    void createHeaderSheet(String name, ExcelHeader header, HSSFWorkbook workbook, List<?> data) throws IllegalAccessException;

    void createSheet(String name, List<ExcelCellConfig> excelCellConfigs, HSSFWorkbook workbook, List<?> data) throws  IllegalAccessException;
}
