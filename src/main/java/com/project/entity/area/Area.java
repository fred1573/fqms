package com.project.entity.area;

import javax.persistence.*;
import java.util.Set;

/**
 *
 * @author frd
 */
@Entity
@Table(name = "tomato_base_area")
public class Area {

    public static final Integer LEVEL_COUNTRY = 1;
    public static final Integer LEVEL_PROVINCE = 2;
    public static final Integer LEVEL_CITY = 3;
    public static final Integer LEVEL_DISTRICT = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String code;

    @Column
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent")
    private Area parent;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", targetEntity = Area.class, fetch = FetchType.LAZY)
    private Set<Area> children;

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Area getParent() {
        return parent;
    }

    public void setParent(Area parent) {
        this.parent = parent;
    }

    public Set<Area> getChildren() {
        return children;
    }

    public void setChildren(Set<Area> children) {
        this.children = children;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
