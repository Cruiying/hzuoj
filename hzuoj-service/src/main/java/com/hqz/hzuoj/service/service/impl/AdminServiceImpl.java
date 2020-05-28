package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.user.Admin;
import com.hqz.hzuoj.mapper.user.AdminMapper;
import com.hqz.hzuoj.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 管理员服务
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/18 18:15
 * @Description: TODO
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 管理员登陆[管理员方法]
     *
     * @param admin
     * @return 登陆管理员
     */
    @Override
    public Admin loginAdmin(Admin admin) {
        return adminMapper.loginAdmin(admin);
    }

    /**
     * 根据id返回管理员信息[管理员方法]
     *
     * @param adminId 管理员主键
     * @return 管理员信息
     */
    @Override
    public Admin selectAdmin(Integer adminId) {
        return adminMapper.selectAdmin(adminId);
    }

    /**
     * 返回所有管理员[管理员方法]
     *
     * @param page 页数
     * @return 管理员分页列表
     */
    @Override
    public PageInfo<Admin> getAll(Integer page) {
        if (page == null || page <= 0) {
            page = 1;
        }
        PageHelper.startPage(page, 20, true);
        List<Admin> admins = adminMapper.getAll();
        return new PageInfo<>(admins, 20);
    }

    @Override
    public String updateAdminStatus(Admin admin) {
        adminMapper.updateAdminStatus(admin);
        return "success";
    }

    @Override
    public Admin addAdmin(Admin admin) {
        adminMapper.saveAdmin(admin);
        return admin;
    }
}
