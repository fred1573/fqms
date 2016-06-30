package com.project.service.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yuneng.huang on 2016/6/15.
 */
public class ExcelServiceImpl implements ExcelService {

    public void createHeaderSheet(String name,ExcelHeader header, HSSFWorkbook workbook,List<?> data) throws IllegalAccessException {
        HSSFSheet sheet = workbook.createSheet(name);
        int headerIndex = header.getIndex();
        HSSFRow row = sheet.createRow(headerIndex);
        List<String> names = header.getHeaderNameList();
        for (int i = 0; i < names.size(); i++) {
            HSSFCell cell = row.createCell(i);
            HSSFCellStyle style = header.getStyle();
            if (style != null) {
                cell.setCellStyle(style);
            }
            cell.setCellValue(names.get(i));
        }
        int dataIndex = headerIndex + 1;
        for (int i = dataIndex; i < data.size()+dataIndex; i++)
        {
            createRow(sheet, header.getCellConfigList(), data.get(i-dataIndex),i);
        }
    }

    public void createSheet(String name, List<ExcelCellConfig> excelCellConfigs, HSSFWorkbook workbook, List<?> data) throws IllegalAccessException {
        HSSFSheet sheet = workbook.createSheet(name);
        for (int i = 0; i < data.size(); i++)
        {
            createRow(sheet, excelCellConfigs, data.get(i),i);
        }
    }


    protected void createRow(HSSFSheet sheet, List<ExcelCellConfig> excelCellConfigs, Object data, int index) throws  IllegalAccessException {
        HSSFRow row = sheet.createRow(index);
        for (int i = 0; i < excelCellConfigs.size(); i++) {
            ExcelCellConfig cellConfig = excelCellConfigs.get(i);
            HSSFCell cell = row.createCell(i);
            HSSFCellStyle style = cellConfig.getStyle();
            if (style != null) {
                cell.setCellStyle(style);
            }
            Class<?> dataClass = data.getClass();
            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(dataClass, cellConfig.getField());
            if (propertyDescriptor == null) {
                continue;
            }
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod == null) {
                continue;
            }
            Object fieldValue = null;
            try {
                fieldValue = readMethod.invoke(data);
            } catch (InvocationTargetException e) {
            }
            Converter<Object,String> converter = cellConfig.getConverter();
            if (fieldValue instanceof String) {
                cell.setCellValue((String) fieldValue);
            } else if ( converter!= null) {
                cell.setCellValue(converter.convert(fieldValue));
            } else {
                cell.setCellValue(fieldValue==null?"":fieldValue.toString());
            }
        }
    }
}
