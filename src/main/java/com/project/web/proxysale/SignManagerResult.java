package com.project.web.proxysale;

import java.util.List;

public class SignManagerResult {
	
	private SignManager data;
	
	private String type;
	
	private Integer status;

	public SignManager getData() {
		return data;
	}

	public void setData(SignManager data) {
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public class SignManager{
		
		private String signManagerMobile;
		
		private String signManagerName;
		
		
		private  List<Boss> contactVoBean;

		public String getSignManagerMobile() {
			return signManagerMobile;
		}

		public void setSignManagerMobile(String signManagerMobile) {
			this.signManagerMobile = signManagerMobile;
		}

		public String getSignManagerName() {
			return signManagerName;
		}

		public void setSignManagerName(String signManagerName) {
			this.signManagerName = signManagerName;
		}

		public List<Boss> getContactVoBean() {
			return contactVoBean;
		}

		public void setContactVoBean(List<Boss> contactVoBean) {
			this.contactVoBean = contactVoBean;
		}
		
	public	class Boss{
			
			private String bossName;
			
			private String bossMobile;

			public String getBossName() {
				return bossName;
			}

			public void setBossName(String bossName) {
				this.bossName = bossName;
			}

			public String getBossMobile() {
				return bossMobile;
			}

			public void setBossMobile(String bossMobile) {
				this.bossMobile = bossMobile;
			}
			
		}
		
	}
}






