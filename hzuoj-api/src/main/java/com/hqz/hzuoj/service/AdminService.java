package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.Admin;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/18 18:13
 * @Description: TODO
 */
public interface AdminService {

    Admin loginAdmin(Admin admin);

    Admin selectAdmin(Integer adminId);

    PageInfo<Admin> getAll(Integer page);

}
