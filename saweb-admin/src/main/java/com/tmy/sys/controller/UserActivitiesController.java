package com.tmy.sys.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmy.common.vo.Result;
import com.tmy.sys.entity.User;
import com.tmy.sys.entity.UserActivities;
import com.tmy.sys.mapper.UserActivitiesMapper;
import com.tmy.sys.mapper.UserMapper;
import com.tmy.sys.service.IUserActivitiesService;
import com.tmy.sys.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tmy
 * @since 2024-02-20
 */
@RestController
@RequestMapping("/userAct")
public class UserActivitiesController {


    @Resource
    private UserMapper userMapper;

    @Resource
    private UserActivitiesMapper userActivitiesMapper;

    @Autowired
    private IUserActivitiesService userActivitiesService;

    @GetMapping("/{activityId}")
    public Result<List<User>> getUserActivitiesList(@PathVariable("activityId") Integer activityId) {
        // 通过活动ID获取所有参与该活动的用户ID
        List<Integer> userIds = userActivitiesMapper.selectList(
                new QueryWrapper<UserActivities>().eq("activity_id", activityId)
        ).stream().map(UserActivities::getUserid).collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return Result.fail("该活动暂无报名人员");
        }
        // 通过用户ID列表查询所有用户信息
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("id", userIds);
        List<User> users = userMapper.selectList(wrapper);

        return Result.success(users);
    }

    /**
     * 导出接口
     */
    @GetMapping("/export/{activityId}")
    public void export(HttpServletResponse response,@PathVariable("activityId") Integer activityId) throws Exception {
        // 从数据库查询出所有的数据
        List<Integer> userIds = userActivitiesMapper.selectList(
                new QueryWrapper<UserActivities>().eq("activity_id", activityId)
        ).stream().map(UserActivities::getUserid).collect(Collectors.toList());
        // 通过用户ID列表查询所有用户信息
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("id", userIds);
        List<User> users = userMapper.selectList(wrapper);

        try (ServletOutputStream out = response.getOutputStream()) {
            // 在内存操作，写出到浏览器
            ExcelWriter writer = ExcelUtil.getWriter(true);
            // 自定义标题别名
            writer.addHeaderAlias("id", "学号");
            writer.addHeaderAlias("username", "用户名");

            // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
            writer.write(users, true);

            // 设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = URLEncoder.encode("用户信息", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            // 写入并刷新输出流
            writer.flush(out, true);
        } catch (Exception e) {
            // 如果出现异常，在响应未提交前进行处理，如重定向至错误页面或发送错误状态码
            response.reset();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败，请稍后重试！");
        }
    }

}
