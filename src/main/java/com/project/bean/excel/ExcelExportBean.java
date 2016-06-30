package com.project.bean.excel;


public class ExcelExportBean {
	//单元格值
	private Object value;
	//单元格类型
	private String cellType ;
	//是否可为空
	private String needNull ;
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getCellType() {
		return cellType;
	}
	public void setCellType(String cellType) {
		this.cellType = cellType;
	}
	public String getNeedNull() {
		return needNull;
	}
	public void setNeedNull(String needNull) {
		this.needNull = needNull;
	}
	
}
