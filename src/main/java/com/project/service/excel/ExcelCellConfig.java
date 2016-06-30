package com.project.service.excel;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.springframework.core.convert.converter.Converter;

public class ExcelCellConfig<T> {

    private String field;

    private Converter<T, String> converter;

    private HSSFCellStyle style;

    public ExcelCellConfig(String field) {
        this.field = field;
    }

    public ExcelCellConfig(String field, HSSFCellStyle style) {
        this.field = field;
        this.style = style;
    }

    public HSSFCellStyle getStyle() {
        return style;
    }

    public void setStyle(HSSFCellStyle style) {
        this.style = style;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Converter<T, String> getConverter() {
        return converter;
    }

    public void setConverter(Converter<T, String> converter) {
        this.converter = converter;
    }
}