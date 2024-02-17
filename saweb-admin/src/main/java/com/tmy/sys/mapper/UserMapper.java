package com.tmy.sys.mapper;

import com.tmy.sys.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tmy
 * @since 2023-11-03
 */
public interface UserMapper extends BaseMapper<User> {
    public List<String> getRoleNamesByUserId(Integer userId);

    public List<User> getUsersByRoleId(Integer roleId);

    public List<User> getAllUsersWithRoleIds();
}
