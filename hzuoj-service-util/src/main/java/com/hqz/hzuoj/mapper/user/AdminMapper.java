package com.hqz.hzuoj.mapper.user;

import com.hqz.hzuoj.bean.user.Admin;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/18 18:16
 * @Description: TODO
 */
@Component
public interface AdminMapper extends Mapper<Admin> {

    Admin loginAdmin(Admin admin);

    Admin selectAdmin(Integer adminId);

    List<Admin> getAll();

}
