package com.project.bean.excel;

import java.util.List;

public class ExcelSheetBean {
	//sheet表头
	private String[] tableHeader;
	//字段顺序
	private String[] propertySequence ;
	//类全路径
	private String className;
	//sheet名称
	private String  sheetName;
	//对应sheet的数据列表,里面装的是一个个对象
	private List list;
	
	public String[] getTableHeader() {
		return tableHeader;
	}
	public void setTableHeader(String[] tableHeader) {
		this.tableHeader = tableHeader;
	}
	public String[] getPropertySequence() {
		return propertySequence;
	}
	public void setPropertySequence(String[] propertySequence) {
		this.propertySequence = propertySequence;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
}
