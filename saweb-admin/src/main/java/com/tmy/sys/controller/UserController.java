package com.tmy.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tmy.common.vo.Result;
import com.tmy.sys.entity.User;
import io.netty.util.internal.StringUtil;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tmy
 * @since 2023-11-03
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private com.tmy.sys.service.IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/all")
    public Result<List<User>> getAllUsers(){
        List<User> userList = userService.list();
        return Result.success(userList, "查询成功");
    }

    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody User user){
        Map<String,Object> data = userService.login(user);
        if(data != null){
            return Result.success(data);
        }
        return Result.fail(20002,"用户名或密码错误");
    }

    @GetMapping("/info")
    public Result<Map<String,Object>> getUserInfo(@RequestParam("token") String token){
        //根据token获取用户信息
         Map<String,Object> data =userService.getUserInfo(token);
       if(data!= null){
           return Result.success(data);
       }
       return Result.fail(20003,"登录信息无效，请重新登录");
    }

    @PostMapping("/logout")
    public Result<Map<String,Object>> logout(@RequestHeader("X-Token") String token){
        userService.logout(token);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getUsersList(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value = "pageNo") Long pageNo,
            @RequestParam(value = "pageSize") Long pageSize) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 添加用户名和电话的查询条件
        if (StringUtils.hasLength(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasLength(phone)) {
            wrapper.eq(User::getPhone, phone);
        }

        // 角色ID不为空时执行多表联查（通过UserMapper中的方法）
        if (roleId != null) {
            List<User> usersByRoleId = userService.getUsersByRoleId(roleId);
            // 假设返回的是所有满足条件的User对象列表，这里可以根据业务需求处理这些结果
            // 例如：将用户的id放入一个集合中，然后使用in查询
            List<Integer> userIds = usersByRoleId.stream().map(User::getId).collect(Collectors.toList());
            wrapper.in(User::getId, userIds);
        }

        wrapper.orderByDesc(User::getId);

        Page<User> page = new Page<>(pageNo, pageSize);

        userService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }

    @PostMapping
    public Result<?> addUser(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.addUser(user);
        return Result.success("新增用户成功");
    }

    @PutMapping
    public Result<?> updateUser(@RequestBody User user){
        user.setPassword(null);
        userService.updateUser(user);
        return Result.success("修改用户成功");
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Integer id){
         User user = userService.getUserById(id);
        return Result.success(user);
    }

    @DeleteMapping("/{id}")
    public Result<User> deleteUser(@PathVariable("id") Integer id){
        userService.deleteUser(id);
        return Result.success("删除用户成功");
    }
}
