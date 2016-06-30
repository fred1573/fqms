package com.project.entity.ota;

import com.project.entity.proxysale.Channel;

/**
 * Created by Administrator on 2015/6/25.
 */
//@Entity
//@Table(name = "tomato_oms_ota_info")
public class OtaInfo {

//    @Id
    private Integer id;
//    @Column
    private String name;
//    @Column(name = "ota_id")
    private Integer otaId;
//    @Column(name = "pid")
    private Integer parent;
//    @Transient
    private Channel channel;

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

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Integer getOtaId() {
        return otaId;
    }

    public void setOtaId(Integer otaId) {
        this.otaId = otaId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
