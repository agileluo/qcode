package io.github.agileluo.qcode.model;

import java.util.Date;
import javax.persistence.*;

import lombok.Data;
@Data 
@Table(name = "scan_user")
public class ScanUser {
    @Id
    private Long id;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    @Column(name = "real_name")
    private String realName;

    /**
     * 微信id
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 微信昵称
     */
    @Column(name = "wx_name")
    private String wxName;

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