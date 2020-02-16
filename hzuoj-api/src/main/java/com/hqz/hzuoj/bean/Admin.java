package com.hqz.hzuoj.bean;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 管理员实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/12 11:59
 * @Description: TODO
 */
public class Admin implements Serializable {
    //管理员主键
    @Id
    private Integer adminId;
    //管理员姓名
    private String adminName;
    //管理员密码
    private String password;
    //管理员状态
    private Integer adminStatus;
    //管理员添加时间
    private Date adminCreateTime;
    //是否关联普通用户
    private User user;


    /**
     * 获取管理员主键
     *
     * @return
     */
    public Integer getAdminId() {
        return adminId;
    }

    /**
     * 设置管理员主键
     *
     * @param adminId
     */
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    /**
     * 获取管理员姓名
     *
     * @return
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     * 设置管理员姓名
     *
     * @param adminName
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * 获取管理密码
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置管理员密码
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取管理员状态
     *
     * @return
     */
    public Integer getAdminStatus() {
        return adminStatus;
    }

    /**
     * 设置管理员状态
     *
     * @param adminStatus
     */
    public void setAdminStatus(Integer adminStatus) {
        this.adminStatus = adminStatus;
    }

    /**
     * 获取关联的普通用户
     *
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * 设置关联的普通用户
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 获取管理员添加时间
     *
     * @return
     */
    public Date getAdminCreateTime() {
        return adminCreateTime;
    }

    /**
     * 设置管理员添加时间
     *
     * @param adminCreateTime
     */
    public void setAdminCreateTime(Date adminCreateTime) {
        this.adminCreateTime = adminCreateTime;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", adminName='" + adminName + '\'' +
                ", password='" + password + '\'' +
                ", adminStatus=" + adminStatus +
                ", adminCreateTime=" + adminCreateTime +
                ", user=" + user +
                '}';
    }
}
