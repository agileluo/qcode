package io.github.agileluo.qcode.model;

import java.util.Date;
import javax.persistence.*;

import lombok.Data;
@Data 
@Table(name = "scan_group")
public class ScanGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 分类名
     */
    private String name;

    /**
     * 父级Id
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    
}