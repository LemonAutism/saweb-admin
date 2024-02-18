package com.tmy.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tmy.common.vo.Result;
import com.tmy.sys.entity.Role;
import com.tmy.sys.entity.User;
import com.tmy.sys.entity.UserRole;
import com.tmy.sys.mapper.UserRoleMapper;
import com.tmy.sys.service.IRoleService;
import io.netty.util.internal.StringUtil;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tmy
 * @since 2023-11-03
 */

@RestController
@RequestMapping("/userRole")
public class UserRoleController {

    @Resource
    private UserRoleMapper userRoleMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> getUserRoles() {
        // 查询所有用户角色关联记录
        List<UserRole> userRoleList = userRoleMapper.selectList(null);
        // 封装结果数据
        Map<String, Object> userRolesData = new HashMap<>();
        userRolesData.put("userRoles", userRoleList);

        return Result.success(userRolesData);}
}

