package com.project.entity.api;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;





import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.bean.serializer.JsonDateSerializer;

/**
 * 渠道子订单
 * 
 * @author momo
 * 
 */
@Entity
@Table(name = "tomato_channel_order")
public class ChannelOrder implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_channel_order_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	/** 预计入住时间 */
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date checkInAt;

	/** 预计退房时间 */
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date checkOutAt;

	/** 代销平台中录入的原价 */
	private Double originalPrice;
	
	/** 进价，也是下单的预定价格 */
	private Double bookPrice;

	/** 给第三方渠道的售价 */
	private Double salePrice;

	/** 房型名称*/
	private String channelRoomTypeName;
	
	/** 房间编号 */
	private String roomNo;
	
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

	public Date getCheckInAt() {
		return checkInAt;
	}

	public void setCheckInAt(Date checkInAt) {
		this.checkInAt = checkInAt;
	}

	public Date getCheckOutAt() {
		return checkOutAt;
	}

	public void setCheckOutAt(Date checkOutAt) {
		this.checkOutAt = checkOutAt;
	}

	public Double getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(Double originalPrice) {
		this.originalPrice = originalPrice;
	}

	public Double getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(Double bookPrice) {
		this.bookPrice = bookPrice;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}

	public String getChannelRoomTypeName() {
		return channelRoomTypeName;
	}

	public void setChannelRoomTypeName(String channelRoomTypeName) {
		this.channelRoomTypeName = channelRoomTypeName;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}
	

}
