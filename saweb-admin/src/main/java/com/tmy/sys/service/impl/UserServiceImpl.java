package com.tmy.sys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.tmy.common.utils.JwtUtil;
import com.tmy.sys.entity.Menu;
import com.tmy.sys.entity.User;
import com.tmy.sys.entity.UserRole;
import com.tmy.sys.mapper.UserMapper;
import com.tmy.sys.mapper.UserRoleMapper;
import com.tmy.sys.service.IMenuService;
import com.tmy.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tmy
 * @since 2023-11-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Autowired
    private IMenuService menuService;

    @Resource
    private UserMapper userMapper;

    @Override
    public Map<String, Object> login(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper();
        wrapper.eq(User::getUsername,user.getUsername());

        User loginUser = this.getOne(wrapper);
        if(loginUser != null && passwordEncoder.matches(user.getPassword(),loginUser.getPassword())){
            Map<String, Object> data = new HashMap<>();
            //String key = "user:" + UUID.randomUUID();
            // 待优化，最终方案jwt
            loginUser.setPassword(null);

            String Token = jwtUtil.createToken(loginUser);

            //redisTemplate.opsForValue().set(Token,loginUser,30, TimeUnit.MINUTES);
            data.put("token", Token);
            return data;
        }
        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        //根据token获取用户信息
        //Object obj = redisTemplate.opsForValue().get(token);
        User loginUser = null;
        try {
            loginUser =jwtUtil.parseToken(token, User.class);
        } catch (Exception e) {
           e.printStackTrace();
        }
        if(loginUser!= null){
            //User loginUser =JSON.parseObject(JSON.toJSONString(obj),User.class);
            Map<String, Object> data = new HashMap<>();
            data.put("id",loginUser.getId());
            data.put("name",loginUser.getUsername());
            data.put("avatar",loginUser.getAvatar());

            //角色
            List<String> roleList = this.getBaseMapper().getRoleNamesByUserId(loginUser.getId());
            data.put("roles", roleList);

            //权限列表
            List<Menu> menuList = menuService.getMenuListByUserId(loginUser.getId());
            data.put("menuList", menuList);

            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        //redisTemplate.delete(token);
    }

    @Override
    @Transactional
    public void addUser(User user) {
        this.baseMapper.insert(user);
        List<Integer> roleIdList = user.getRoleIdList();
        if(roleIdList != null){
            for (Integer roleId : roleIdList) {
                userRoleMapper.insert(new UserRole(null, user.getId(), roleId));
            }
        }
    }

    @Override
    @Transactional
    public User getUserById(Integer id) {
        User user = this.baseMapper.selectById(id);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,id);
        List<UserRole> userRoleList = userRoleMapper.selectList(wrapper);
        List<Integer> roleIdList = userRoleList.stream().map(UserRole -> {
            return UserRole.getRoleId();
        }).collect(Collectors.toList());
        user.setRoleIdList(roleIdList);
        return user;
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        this.baseMapper.updateById(user);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,user.getId());
        userRoleMapper.delete(wrapper);
        List<Integer> roleIdList = user.getRoleIdList();
        if(roleIdList != null){
            for (Integer roleId : roleIdList) {
                userRoleMapper.insert(new UserRole(null, user.getId(), roleId));
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        this.baseMapper.deleteById(id);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,id);
        userRoleMapper.delete(wrapper);
    }

    @Override
    public List<User> getUsersByRoleId(Integer roleId) {
        System.out.println(userMapper.getUsersByRoleId(roleId).toString());
        return userMapper.getUsersByRoleId(roleId);
    }

    @Override
    public List<User> getAllUsersWithRoleIds() {
        return userMapper.getAllUsersWithRoleIds();
    }

    @Override
    public void addUsers(List<User> users) {
        for (User user : users) {
            this.baseMapper.insert(user);
            userRoleMapper.insert(new UserRole(null, user.getId(), 3));
        }
        System.out.println("执行一次");
    }
}
