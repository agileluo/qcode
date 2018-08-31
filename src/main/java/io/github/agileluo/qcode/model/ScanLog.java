package io.github.agileluo.qcode.model;

import java.util.Date;
import javax.persistence.*;

import lombok.Data;
@Data 
@Table(name = "scan_log")
public class ScanLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户Id
     */

    private String userId;

    @Column(name = "open_id")
    private String openId;

    /**
     * 一级分类
     */
    private String group1;

    /**
     * 二级分类
     */
    private String group2;

    /**
     * 三级分类
     */
    private String group3;

    /**
     * 二维码内容
     */
    private String qrcode;

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