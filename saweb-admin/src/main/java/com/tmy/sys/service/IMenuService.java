package com.tmy.sys.service;

import com.tmy.sys.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tmy
 * @since 2023-11-03
 */
public interface IMenuService extends IService<Menu> {
    public List<Menu> getAllMenu();

    public List<Menu> getMenuListByUserId(Integer userId);
}
