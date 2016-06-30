package com.project.entity.wg;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;


/**
 * ota房型对应系统客栈房间id
 * 
 * @author momo
 * 
 */
@Entity
@Table(name = "wg_room_type_to_inn_room")
public class WgRoomTypeToInnRoom implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_wg_room_type_to_inn_room_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	private Integer innRoomId;
	
	/** 所属ota房型 */
	@ManyToOne
	@JoinColumn(name = "room_type_id")
	@JsonBackReference
	private WgRoomType wgRoomType = new WgRoomType();
	
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

	public Integer getInnRoomId() {
		return innRoomId;
	}

	public void setInnRoomId(Integer innRoomId) {
		this.innRoomId = innRoomId;
	}

	public WgRoomType getWgRoomType() {
		return wgRoomType;
	}

	public void setWgRoomType(WgRoomType wgRoomType) {
		this.wgRoomType = wgRoomType;
	}
	
	

}
