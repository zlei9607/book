package com.zl.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zl.book.common.Response;
import com.zl.book.model.AdminUser;
import com.zl.book.service.IAdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhanglei
 */
@Controller
public class LoginController {

    @Resource
    private IAdminUserService adminUserService;

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/toLogin")
    @ResponseBody
    public Response<AdminUser> toLogin(@RequestBody AdminUser adminUser, HttpSession session) {
        AdminUser loginUser = adminUserService.getOne(new QueryWrapper<AdminUser>().lambda()
                .eq(AdminUser::getUsername, adminUser.getUsername())
                .eq(AdminUser::getPassword, adminUser.getPassword()));
        if (Objects.isNull(loginUser)) {
            return Response.error("用户不存在或密码错误");
        }
        session.setAttribute("loginUser", loginUser);
        // 更新登录时间
        loginUser.setLastLoginTime(new Date());
        adminUserService.updateById(loginUser);
        return Response.success(loginUser);
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loginUser");
        return "redirect:/login";
    }

}
