package com.tmy.sys.service;

import com.tmy.sys.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tmy
 * @since 2023-11-03
 */
public interface IUserService extends IService<User> {

    Map<String, Object> login(User user);

    Map<String, Object> getUserInfo(String token);

    void logout(String token);

    void addUser(User user);

    User getUserById(Integer id);

    void updateUser(User user);

    void deleteUser(Integer id);

    List<User> getUsersByRoleId(Integer roleId);

    List<User> getAllUsersWithRoleIds();
}
