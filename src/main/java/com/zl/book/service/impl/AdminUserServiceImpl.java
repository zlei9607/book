package com.zl.book.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zl.book.model.AdminUser;
import com.zl.book.mapper.AdminUserMapper;
import com.zl.book.service.IAdminUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zhanglei
 * @since 2022-08-10
 */
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements IAdminUserService {

}
