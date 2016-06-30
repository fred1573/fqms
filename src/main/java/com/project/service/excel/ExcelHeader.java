package com.project.service.excel;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelHeader {
    private List<String> headerNameList = new ArrayList<>();
    private List<ExcelCellConfig> cellConfigList = new ArrayList<>();
    private int index;
    private HSSFCellStyle style;

    public HSSFCellStyle getStyle() {
        return style;
    }

    public void setStyle(HSSFCellStyle style) {
        this.style = style;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void put(String name, ExcelCellConfig cellConfig) {
        headerNameList.add(name);
        cellConfigList.add(cellConfig);
    }

    public List<String> getHeaderNameList() {
        return headerNameList;
    }

    public List<ExcelCellConfig> getCellConfigList() {
        return cellConfigList;
    }
}