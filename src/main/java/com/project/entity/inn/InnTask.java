package com.project.entity.inn;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;

import org.apache.commons.lang3.builder.ToStringBuilder;


@Entity
@Table(name = "tomato_sys_daily_task")
public class InnTask implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_sys_daily_task_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
    private Integer id;

	private Integer funcItemType;
	
	private Integer num;
	
	@Valid
	@ManyToOne
	@JoinColumn(name = "inn_id")
	private Inn inn;
	
	private Date recordedAt;
	
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public Integer getFuncItemType() {
		return funcItemType;
	}



	public void setFuncItemType(Integer funcItemType) {
		this.funcItemType = funcItemType;
	}



	public Integer getNum() {
		return num;
	}



	public void setNum(Integer num) {
		this.num = num;
	}



	public Inn getInn() {
		return inn;
	}



	public void setInn(Inn inn) {
		this.inn = inn;
	}



	public Date getRecordedAt() {
		return recordedAt;
	}



	public void setRecordedAt(Date recordedAt) {
		this.recordedAt = recordedAt;
	}



	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}