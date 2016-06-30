package com.project.entity.wg;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;




import org.apache.commons.lang3.builder.ToStringBuilder;




import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.collect.Lists;

/**
 * wg房型
 * 
 * @author momo
 * 
 */
@Entity
@Table(name = "wg_room_type")
public class WgRoomType implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_wg_room_type_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	private Integer wgRoomTypeId;

	private String wgRoomTypeName;

	/** 所属wg账号 */
	@ManyToOne
	@JoinColumn(name = "account_id")
	@JsonBackReference
	private WgAccount wgAccount = new WgAccount();

	// /** 绑定的客栈房型 */
	// @OneToOne
	// @JoinColumn(name = "inn_room_type_id")
	// public InnRoomType innRoomType = new InnRoomType();

	// 绑定房型价格
	private Double price;

	// 绑定房型设置原价（小站）
	private Double originalPrice = 0D;

	// 状态（0：禁用 1：普通 2：团购）
	private String status;

	@JsonIgnore
	private String rmk;

	// 房间描述
	private String roomDesc;

	// 房型设施和服务:,存放数据:1,2,3
	public String roomService;

	/** ota房型对应系统客栈房间id */
	@OneToMany(cascade = { CascadeType.ALL })
	@JoinColumn(name = "room_type_id", referencedColumnName = "id")
	@JsonManagedReference
	private List<WgRoomTypeToInnRoom> wgRoomTypeToInnRooms = Lists.newArrayList();


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getWgRoomTypeId() {
		return wgRoomTypeId;
	}


	public void setWgRoomTypeId(Integer wgRoomTypeId) {
		this.wgRoomTypeId = wgRoomTypeId;
	}


	public String getWgRoomTypeName() {
		return wgRoomTypeName;
	}


	public void setWgRoomTypeName(String wgRoomTypeName) {
		this.wgRoomTypeName = wgRoomTypeName;
	}


	public WgAccount getWgAccount() {
		return wgAccount;
	}


	public void setWgAccount(WgAccount wgAccount) {
		this.wgAccount = wgAccount;
	}


	public Double getPrice() {
		return price;
	}


	public void setPrice(Double price) {
		this.price = price;
	}


	public Double getOriginalPrice() {
		return originalPrice;
	}


	public void setOriginalPrice(Double originalPrice) {
		this.originalPrice = originalPrice;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getRmk() {
		return rmk;
	}


	public void setRmk(String rmk) {
		this.rmk = rmk;
	}


	public String getRoomDesc() {
		return roomDesc;
	}


	public void setRoomDesc(String roomDesc) {
		this.roomDesc = roomDesc;
	}


	public String getRoomService() {
		return roomService;
	}


	public void setRoomService(String roomService) {
		this.roomService = roomService;
	}


	public List<WgRoomTypeToInnRoom> getWgRoomTypeToInnRooms() {
		return wgRoomTypeToInnRooms;
	}


	public void setWgRoomTypeToInnRooms(
			List<WgRoomTypeToInnRoom> wgRoomTypeToInnRooms) {
		this.wgRoomTypeToInnRooms = wgRoomTypeToInnRooms;
	}
	
	

}
