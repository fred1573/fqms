package com.project.entity.wg;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ota
 * 
 * @author X
 * 
 */
@Entity
@Table(name = "wg_ota_info")
public class WgOtaInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_wg_ota_info_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;
	
	private String name;
	
	private String pic;
	
	private String url;
	
	private String rmk;
	
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getPic() {
		return pic;
	}



	public void setPic(String pic) {
		this.pic = pic;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public String getRmk() {
		return rmk;
	}



	public void setRmk(String rmk) {
		this.rmk = rmk;
	}



	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
