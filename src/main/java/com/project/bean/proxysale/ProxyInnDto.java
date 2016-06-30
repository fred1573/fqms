package com.project.bean.proxysale;


public class ProxyInnDto {
	
	private String  area ;
	
	private String innName;
	
	private String	status; 
	
	private String	priceModel; 
	
	private String	percent; 
	
	private String	create;
	
	private String	manager;
	
	private String	commonChannel;
	
	private String	boutiqueChannel;
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getInnName() {
		return innName;
	}
	public void setInnName(String innName) {
		this.innName = innName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		if(status.equals("3")){
			this.status = "已上架";
		}else if(status.equals("2")){
			this.status = "已上架精品代销";
		}else if(status.equals("1")){
			this.status = "已上架普通代销";
		}else if(status.equals("0")){
			this.status = "已下架";
		}else{
			this.status = "状态异常，快联系技术";
		}
	}
	public String getPriceModel() {
		return priceModel;
	}
	public void setPriceModel(String priceModel) {
		this.priceModel = priceModel;
	}
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public String getCreate() {
		return create;
	}
	public void setCreate(String create) {
		this.create = create;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getCommonChannel() {
		return commonChannel;
	}
	public void setCommonChannel(String commonChannel) {
		this.commonChannel = commonChannel;
	}
	public String getBoutiqueChannel() {
		return boutiqueChannel;
	}
	public void setBoutiqueChannel(String boutiqueChannel) {
		this.boutiqueChannel = boutiqueChannel;
	} 

}
